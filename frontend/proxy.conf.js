const PROXY_CONFIG = {
    "/auth": {
        "target": "http://localhost:8080",
        "secure": false,
        "changeOrigin": true,
        "logLevel": "debug",
        "bypass": function (req, res, proxyOptions) {
            if (req.headers.accept && req.headers.accept.indexOf("text/html") !== -1) {
                console.log("Skipping proxy for browser request: " + req.url);
                return "/index.html";
            }
        }
    },
    "/api": {
        "target": "http://localhost:8080",
        "secure": false,
        "changeOrigin": true,
        "logLevel": "debug"
    },
    "/posts": {
        "target": "http://localhost:8080",
        "secure": false,
        "changeOrigin": true,
        "logLevel": "debug"
    },
    "/feed": {
        "target": "http://localhost:8080",
        "secure": false,
        "changeOrigin": true,
        "logLevel": "debug",
        "bypass": function (req, res, proxyOptions) {
            if (req.headers.accept && req.headers.accept.indexOf("text/html") !== -1) {
                return "/index.html";
            }
        }
    },
    "/connections": {
        "target": "http://localhost:8080",
        "secure": false,
        "changeOrigin": true,
        "logLevel": "debug",
        "bypass": function (req, res, proxyOptions) {
            if (req.headers.accept && req.headers.accept.indexOf("text/html") !== -1) {
                return "/index.html";
            }
        }
    },
    "/uploads": {
        "target": "http://localhost:8080",
        "secure": false,
        "changeOrigin": true,
        "logLevel": "debug"
    }
};

module.exports = PROXY_CONFIG;
