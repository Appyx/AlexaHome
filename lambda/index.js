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
            console.log("profile response");
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
        var pass=fs.readFileSync("tls/users/"+user+"/pass.txt","UTF-8");
        var host=fs.readFileSync("tls/users/"+user+"/pass.txt","UTF-8");
        
        console.log("redirecting to host: "+host);
        
        var options = {
            hostname: host,
            port: 8443,
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
                callback(null,data);
            });
        });
        req.write(JSON.stringify(data));
        req.end();
    }else{
        console.log("no host foun for user: "+user);
        callback(null);
    }
}