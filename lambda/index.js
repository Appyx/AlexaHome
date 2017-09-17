var https = require('https');
var fs = require("fs");

exports.handler = function (event, context, callback) {
    
    console.log(JSON.stringify(event));
    var token= event.payload.accessToken;
    var options={
        hostname: 'api.amazon.com',
        port: 443,
        path: '/user/profile',
        method: 'GET',
        headers: {
            'Authorization': "Bearer " + token
        }
    };

    var req = https.request(options, function (res) {
        res.on('data', function (data) {
            var account=JSON.parse(data);
            console.log(JSON.stringify(account));
            redirect(account.email,event,callback);
        });
    });
    req.end();
};

function redirect(user,data,callback){
    if(fs.existsSync("tls/users/"+user)){
        var key=fs.readFileSync("tls/users/"+user+"/client-key.pem");
        var cert=fs.readFileSync("tls/users/"+user+"/client-cert.pem");
        
        var settings=JSON.parse(fs.readFileSync("tls/users/"+user+"/settings.json","UTF-8"));
        var pass=settings.password;
        var host=settings.remoteDomain;
        var port=settings.remotePort;
        
        console.log("redirecting to host: "+host+":"+port);
        
        var options = {
            hostname: host,
            port: port,
            path: '/',
            method: 'POST',
            ca: fs.readFileSync("tls/ca.crt"),
            key: key,
            cert: cert,
            passphrase: pass,
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        var req = https.request(options, function (res) {
            res.on('data', function (data) {
                console.log(JSON.stringify(JSON.parse(data)));
                callback(null,JSON.parse(data));
            });
        });
        req.write(JSON.stringify(data));
        req.end();
    }else{
        console.log("no host found for user: "+user);
        callback(null);
    }
}