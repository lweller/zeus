package ch.wellernet.zeus.modules.device.controller;

import static ch.wellernet.zeus.common.ManifestUtil.buildVersionInfo;
import static ch.wellernet.zeus.common.ManifestUtil.findManifest;
import static ch.wellernet.zeus.modules.device.controller.DeviceApiVersionController.API_PATH;

import java.io.IOException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;

@RestController
@CrossOrigin
@RequestMapping(API_PATH)
public class DeviceApiVersionController implements DeviceApiV1Controller {

	static final String API_PATH = API_ROOT_PATH + "/version";

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public JSONObject getVersion() throws IOException {
		return buildVersionInfo(findManifest(getClass()));
	}
}