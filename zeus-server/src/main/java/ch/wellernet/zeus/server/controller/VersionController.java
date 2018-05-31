package ch.wellernet.zeus.server.controller;

import static ch.wellernet.zeus.common.ManifestUtil.buildVersionInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

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

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public JSONObject getVersion() throws IOException {
		final InputStream inputStream = getClass().getResourceAsStream("META-INF/MANIFEST.MF");
		if (inputStream != null) {
			return buildVersionInfo(new Manifest(inputStream));
		}
		return new JSONObject();
	}
}
