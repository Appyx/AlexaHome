#!/bin/bash

LC_ALL=en_US.UTF-8
LANG=en_US.UTF-8

if [ -z "$1" ]; then
	echo "please provide a password for the keystore"
	exit
fi
if [ -z "$2" ]; then
	echo "please provide a local ip address"
	exit
fi
if [ -z "$3" ]; then
	echo "please provide a remote domain"
	exit
fi
if [ -z "$4" ]; then
	echo "please provide the ca password"
	exit
fi
if [ -z "$5" ]; then
	echo "please provide a username"
	exit
fi

mkdir server

CA_ALIAS=ca-root

DIR=server
PREFIX=server
ALIAS=server

echo "Create key for server..."
keytool -keystore $DIR/$PREFIX-keystore.jks -storepass $1 -genkey -alias $ALIAS -keypass $1 -keyalg RSA -keysize 2048 -validity 3650 -dname "CN=$3,O=AlexaHome,OU=$5" -ext san=dns:$3,ip:$2,dns:localhost,ip:127.0.0.1

echo "Create csr to get a cert..."
keytool -keystore $DIR/$PREFIX-keystore.jks -storepass $1 -certreq -alias $ALIAS -file $DIR/$PREFIX.csr -ext san=dns:$3,ip:$2,dns:localhost,ip:127.0.0.1


openssl ca -batch -passin pass:$4 -policy policy_match -out $DIR/$PREFIX.crt -in $DIR/$PREFIX.csr -config openssl.cnf -extensions SAN 

rm $DIR/$PREFIX.csr

echo "Import the ca into the keystore to create ca chain..."
keytool -import -keystore $DIR/$PREFIX-keystore.jks -storepass $1 -file certs/ca.crt -alias theCARoot -noprompt

echo "Import the signed cert into the keystore..."
keytool -import -keystore $DIR/$PREFIX-keystore.jks -storepass $1 -file $DIR/$PREFIX.crt -alias $ALIAS -noprompt

echo "Import ca into server truststore..."
keytool -import -keystore $DIR/$PREFIX-truststore.jks -storepass $1 -file certs/ca.crt -alias $CA_ALIAS -noprompt

mkdir client

DIR=client
PREFIX=client
ALIAS=client-key

echo "Create key for client..."
keytool -keystore $DIR/$PREFIX-keystore.jks -storepass $1 -genkey -alias $ALIAS -keypass $1 -keyalg RSA -keysize 2048 -validity 3650 -dname "CN=client,O=AlexaHome,OU=$3"

echo "Create csr to get a cert..."
keytool -keystore $DIR/$PREFIX-keystore.jks -storepass $1 -certreq -alias $ALIAS -file $DIR/$PREFIX.csr

echo "Sign the csr..."
openssl ca -batch -config openssl.cnf -passin pass:$4 -policy policy_match -out $DIR/$PREFIX.crt -in $DIR/$PREFIX.csr
rm $DIR/$PREFIX.csr

echo "Import the ca into the keystore to create ca chain..."
keytool -import -keystore $DIR/$PREFIX-keystore.jks -storepass $1 -file certs/ca.crt -alias $CA_ALIAS -noprompt

echo "Import the signed cert into the keystore..."
keytool -import -keystore $DIR/$PREFIX-keystore.jks -storepass $1 -file $DIR/$PREFIX.crt -alias $ALIAS -noprompt

echo "Import ca into client truststore..."
keytool -import -keystore $DIR/$PREFIX-truststore.jks -storepass $1 -file certs/ca.crt -alias $CA_ALIAS -noprompt

echo "Convert client keystore to pkcs12..."
keytool -importkeystore -srckeystore $DIR/$PREFIX-keystore.jks -srcstorepass $1 -destkeystore $DIR/$PREFIX-keystore.p12 -deststorepass $1 -deststoretype PKCS12 -srcalias $ALIAS

echo "Export key and cert as pkcs12..."
openssl pkcs12 -in $DIR/$PREFIX-keystore.p12 -nokeys -out $DIR/$PREFIX-cert.pem -passin pass:$1
openssl pkcs12 -in $DIR/$PREFIX-keystore.p12 -nocerts -out $DIR/$PREFIX-key.pem -passin pass:$1 -passout pass:$1
cp certs/ca.crt $DIR
