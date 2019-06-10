package ch.wellernet.zeus.modules.device.controller;

import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static ch.wellernet.zeus.common.ManifestUtil.buildVersionInfo;
import static ch.wellernet.zeus.common.ManifestUtil.findManifest;
import static ch.wellernet.zeus.modules.device.controller.DeviceApiVersionController.API_PATH;

@RestController
@CrossOrigin
@RequestMapping(API_PATH)
public class DeviceApiVersionController implements DeviceApiV1Controller {

  static final String API_PATH = API_ROOT_PATH + "/version";

  @GetMapping
  @ResponseBody
  public JSONObject getVersion() throws IOException {
    return buildVersionInfo(findManifest(getClass()));
  }
}
