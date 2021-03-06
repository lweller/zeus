package ch.wellernet.zeus.server.controller;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

import static ch.wellernet.zeus.common.ManifestUtil.buildVersionInfo;

@RestController
@CrossOrigin
@RequestMapping("/version")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class VersionController {

  // injected dependencies
  private final ServletContext context;

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
