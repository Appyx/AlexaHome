LC_ALL=en_US.UTF-8
LANG=en_US.UTF-8

if [ -z "$1" ]; then
	echo "please provide a password for the CA"
	exit
fi

echo "Creating CA..."

#setup folders
mkdir crl
mkdir newcerts
mkdir private
mkdir certs

echo '01' > serial
touch index.txt


#create the ca's certificate and key
openssl req -config openssl_nosan.cnf -new -x509 -extensions v3_ca -keyout private/ca.key -out certs/ca.crt -days 3650 -subj "/O=AlexaHome/CN=root" -passout pass:$1

chmod 600 private/ca.key
