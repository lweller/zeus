// patch to make @biesbjerg/ngx-translate-po-http-loader compatible with angular 6

const fs = require('fs');

const f1 = 'node_modules/@angular-devkit/build-angular/src/angular-cli-files/models/webpack-configs/browser.js';
fs.readFile(f1, 'utf8', function (err, data) {
    if (err) {
        return console.log(err);
    }
    const result = data.replace(/node: false/g, 'node: {crypto: true, stream: true}');

    fs.writeFile(f1, result, 'utf8', function (err) {
        if (err) return console.log(err);
    });
});

// IIFE
(function () {

    'use strict';

    // node
    const fs = require('fs');
    const path = require('path');

    // Patch encoding module due to iconv issues -> make it use iconv-lite
    (function () {
        const PATCH_VERSION = '0.1.12';
        const PATCH_MODULE = 'encoding';
        const PATCH_REASON = 'Use iconv-lite instead of iconv, helpful for webpack bundling';
        console.log('patching `%s`(%s) module', PATCH_MODULE, PATCH_VERSION);
        const pathToModule = path.join(__dirname, 'node_modules', PATCH_MODULE);
        const pathToModulePackage = path.join(pathToModule, 'package.json');
        const pathToModulePatchedFile1 = path.join(pathToModule, 'lib/iconv-loader.js');
        const pathToModulePatchedFile2 = path.join(pathToModule, 'lib/encoding.js');
        const moduleInfo = require(pathToModulePackage);
        if (moduleInfo.version !== PATCH_VERSION) {
            console.error(
                'patching `encoding` failed - expected `%s` but detected `%s`',
                PATCH_VERSION,
                moduleInfo.version
            );
            process.exit(1);
        }
        let contents;
        if (fs.existsSync(pathToModulePatchedFile1)) {
            contents = [
                '\'use strict\';',
                'module.exports = require(\'iconv-lite\');',
                ''
            ].join('\n');
            fs.writeFileSync(pathToModulePatchedFile1, contents);
        } else {
            console.error('patching `%s` failed because the file does not exist in', PATCH_MODULE, pathToModule);
            process.exit(1);
        }
        if (fs.existsSync(pathToModulePatchedFile2)) {
            contents = fs.readFileSync(pathToModulePatchedFile2).toString();
            contents = contents.replace('console.error(E);', '');
            fs.writeFileSync(pathToModulePatchedFile2, contents);
        } else {
            console.error('patching `%s` failed because the file does not exist in', PATCH_MODULE, pathToModule);
            process.exit(1);
        }
        console.log('patching `%s`, reason: `%s` - completed', PATCH_MODULE, PATCH_REASON);
    })();

})(this);