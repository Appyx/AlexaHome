#!/bin/sh
if [ -z "$1" ]; then
    echo "please provide a password"
    exit
fi

if [ -z "$2" ]; then
    echo "please provide a local ip"
    exit
fi

if [ -z "$3" ]; then
    echo "please provide a remote domain"
    exit
fi

#set locale for correct password encoding
LC_CTYPE=en_US.utf8

mkdir server
mkdir client

#generate server keystore
keytool -genkeypair -alias server-keypair -keyalg RSA -keysize 2048 -validity 3650 -dname "CN=server,O=$2" -keypass $1 -keystore server/server-keystore.jks -storepass $1 -ext san=ip:$2,dns:$3,dns:localhost,ip:127.0.0.1

#export server cert
keytool -exportcert -alias server-keypair -file server/server-public-key.cer -keystore server/server-keystore.jks -storepass $1

#import server cert to client-truststore - will be the ca for the client
keytool -importcert -keystore client/client-truststore.jks -alias server-public-key -file server/server-public-key.cer -storepass $1 -noprompt

#convert server keystore to pkcs12
keytool -importkeystore -srckeystore server/server-keystore.jks -srcstorepass $1 -destkeystore server/server-keystore.p12 -deststorepass $1 -deststoretype PKCS12

#export server cert as pkcs12 - will be the ca for the client
openssl pkcs12 -in server/server-keystore.p12 -nokeys -out client/server-cert.pem -passin pass:$1

#generate client keystore
keytool -genkeypair -alias client-keypair -keyalg RSA -keysize 2048 -validity 3650 -dname "CN=client,O=clienthost" -keypass $1 -keystore client/client-keystore.jks -storepass $1

#export client cert
keytool -exportcert -alias client-keypair -file client/client-public-key.cer -keystore client/client-keystore.jks -storepass $1

#import client cert to server-truststore - will be the ca for the server
keytool -importcert -keystore server/server-truststore.jks -alias client-public-key -file client/client-public-key.cer -storepass $1 -noprompt

#convert client keystore to pkcs12
keytool -importkeystore -srckeystore client/client-keystore.jks -srcstorepass $1 -destkeystore client/client-keystore.p12 -deststorepass $1 -deststoretype PKCS12

#export key and cert as pkcs12
openssl pkcs12 -in client/client-keystore.p12 -nokeys -out client/client-cert.pem -passin pass:$1
openssl pkcs12 -in client/client-keystore.p12 -nocerts -out client/client-key.pem -passin pass:$1 -passout pass:$1

