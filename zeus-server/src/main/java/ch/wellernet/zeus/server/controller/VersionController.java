package ch.wellernet.zeus.server.controller;

import static ch.wellernet.zeus.common.ManifestUtil.buildVersionInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;

@RestController
@CrossOrigin
@RequestMapping("/version")
public class VersionController {

	private @Autowired ServletContext context;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public JSONObject getVersion() throws IOException {
		InputStream inputStream = context.getResourceAsStream("META-INF/MANIFEST.MF");
		if (inputStream == null) {
			// fall back to META-INF ins class path
			inputStream = getClass().getResourceAsStream("META-INF/MANIFEST.MF");
		}
		if (inputStream != null) {
			return buildVersionInfo(new Manifest(inputStream));
		}
		return new JSONObject();
	}
}
