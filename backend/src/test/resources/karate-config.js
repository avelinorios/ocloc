function fn() {
    var config = {
        baseUrl: karate.properties['base.url'] || 'http://localhost:8080'
    };
    return config;
}

