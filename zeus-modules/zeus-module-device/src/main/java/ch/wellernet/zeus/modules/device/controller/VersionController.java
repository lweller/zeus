package ch.wellernet.zeus.modules.device.controller;

import java.io.IOException;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Sets;

import net.minidev.json.JSONObject;

@RestController
@CrossOrigin
@RequestMapping("/version")
public class VersionController {

	private final static Set<Attributes.Name> SHOW_ATTRIBUTES = Sets.newHashSet(
			new Attributes.Name("Implementation-Title"), new Attributes.Name("Implementation-Vendor-Id"),
			new Attributes.Name("Implementation-Version"), new Attributes.Name("Build-Host"),
			new Attributes.Name("Build-Jdk"), new Attributes.Name("Build-Number"), new Attributes.Name("Build-Time"),
			new Attributes.Name("Build-User"), new Attributes.Name("Created-By"));

	@Autowired
	private ApplicationContext applicationContext;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public JSONObject getVersion() throws IOException {
		final JSONObject result = new JSONObject();
		final Resource resource = applicationContext.getResource("/META-INF/MANIFEST.MF");

		final Manifest manifest = new Manifest(resource.getInputStream());
		if (manifest != null) {
			final Attributes mainAttributes = manifest.getMainAttributes();
			if (mainAttributes != null) {
				for (final Attributes.Name attribute : SHOW_ATTRIBUTES) {
					if (mainAttributes.containsKey(attribute)) {
						result.put(attribute.toString(), mainAttributes.get(attribute));
					}
				}
			}
		}
		return result;
	}
}
