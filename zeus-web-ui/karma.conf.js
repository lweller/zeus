// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html

module.exports = function (config) {
    // noinspection JSUnusedGlobalSymbols
    config.set({
        basePath: '',
        frameworks: ['jasmine', '@angular-devkit/build-angular'],
        plugins: [
            require('karma-jasmine'),
            require('karma-firefox-launcher'),
            require('karma-phantomjs-launcher'),
            require('karma-jasmine-html-reporter'),
            require('karma-coverage-istanbul-reporter'),
            require('karma-junit-reporter'),
            require('@angular-devkit/build-angular/plugins/karma')
        ],
        client: {
            clearContext: false // leave Jasmine Spec Runner output visible in browser
        },
        coverageIstanbulReporter: {
            dir: require('path').join(__dirname, 'coverage'), reports: ['html', 'lcovonly'],
            fixWebpackSourcePaths: true
        },
        junitReporter: {
            outputFile: 'target/surefire-reports/TEST-AngularJS.xml',
            useBrowserName: false,
            classNameFormatter: function (browser, result) {
                let name = 'AngularJS.';
                name += browser.name
                    .replace(/ /g, '_')
                    .replace(/\./g, '_') + '.';
                if (result.suite && result.suite.length > 0) {
                    name += result.suite.join(' ')
                }
                return name;
            }
        },
        reporters: ['progress', 'kjhtml', 'junit'],
        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,
        autoWatch: true,
        browsers: ['PhantomJS'],
        singleRun: false
    });
};
