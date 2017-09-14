var https = require('https');
var fs = require("fs");

exports.handler = function (request, context) {
    var options = {
        hostname: 'appyx.duckdns.org',
        port: 8443,
        path: '/',
        method: 'POST',
        ca: fs.readFileSync("tls/ca.pem"),
        key: fs.readFileSync("tls/users/robert.gstoettner@icloud.com/client-key.pem"),
        cert: fs.readFileSync("tls/users/robert.gstoettner@icloud.com/client-cert.pem"),
        passphrase: fs.readFileSync("tls/users/robert.gstoettner@icloud.com/pass.txt","UTF-8"),
        headers: {
            'Content-Type': 'application/json'
        }
    };

    var req = https.request(options, function (res) {
        res.on('data', function (data) {
            console.log(data.toString());
            context.succeed(JSON.parse(data));
        });
    });
    req.write(JSON.stringify(request));
    req.end();
};