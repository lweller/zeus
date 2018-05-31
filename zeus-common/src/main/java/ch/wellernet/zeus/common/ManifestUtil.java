package ch.wellernet.zeus.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.google.common.collect.Sets;

import net.minidev.json.JSONObject;

public final class ManifestUtil {
	private final static Set<Attributes.Name> SHOW_ATTRIBUTES = Sets.newHashSet(
			new Attributes.Name("Implementation-Title"), new Attributes.Name("Implementation-Vendor-Id"),
			new Attributes.Name("Implementation-Version"), new Attributes.Name("Build-Host"),
			new Attributes.Name("Build-Jdk"), new Attributes.Name("Build-Number"), new Attributes.Name("Build-Time"),
			new Attributes.Name("Build-User"), new Attributes.Name("Created-By"));

	public static JSONObject buildVersionInfo(final Manifest manifest) throws IOException {
		final JSONObject result = new JSONObject();

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

	public static Manifest findManifest(final Class<?> clazz) throws IOException {
		final String classFileUrl = clazz.getResource(clazz.getSimpleName() + ".class").toString();
		InputStream inputStream = null;

		try {
			return new Manifest(inputStream = new URL(
					classFileUrl.substring(0, classFileUrl.length() - (clazz.getCanonicalName() + ".class").length())
							+ "META-INF/MANIFEST.MF").openStream());
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	private ManifestUtil() {
	}
}
