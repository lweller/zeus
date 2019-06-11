package ch.wellernet.zeus.modules.device.controller;

import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static ch.wellernet.zeus.common.ManifestUtil.buildVersionInfo;
import static ch.wellernet.zeus.common.ManifestUtil.findManifest;
import static ch.wellernet.zeus.modules.device.controller.DeviceApiV1Controller.API_ROOT_PATH;

@RestController
@CrossOrigin
@RequestMapping(API_ROOT_PATH + "/version")
public class DeviceApiVersionController implements DeviceApiV1Controller {

  @GetMapping
  @ResponseBody
  public JSONObject getVersion() throws IOException {
    return buildVersionInfo(findManifest(getClass()));
  }
}
