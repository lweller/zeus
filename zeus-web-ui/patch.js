// patch to make @biesbjerg/ngx-translate-po-http-loader compatible with angular 6

const fs = require('fs');

const f1 = 'node_modules/@angular-devkit/build-angular/src/angular-cli-files/models/webpack-configs/browser.js';
fs.readFile(f1, 'utf8', function(err, data) {
	if (err) {
		return console.log(err);
	}
	var result = data.replace(/node: false/g, 'node: {crypto: true, stream: true}');

	fs.writeFile(f1, result, 'utf8', function(err) {
		if (err) return console.log(err);
	});
});

const f2 = 'node_modules/@biesbjerg/ngx-translate-po-http-loader/dist/index.js';
fs.readFile(f2, 'utf8', function(err, data) {
	if (err) {
		return console.log(err);
	}
	var result = data.replace(/rxjs\/operators\/map/g, 'rxjs/operators');

	fs.writeFile(f2, result, 'utf8', function(err) {
		if (err) return console.log(err);
	});
});

const f3 = 'node_modules/@biesbjerg/ngx-translate-po-http-loader/dist/index.d.ts';
fs.readFile(f3, 'utf8', function(err, data) {
	if (err) {
		return console.log(err);
	}
	var result = data.replace(/rxjs\/Observable/g, 'rxjs');

	fs.writeFile(f3, result, 'utf8', function(err) {
		if (err) return console.log(err);
	});
});