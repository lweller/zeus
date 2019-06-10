package ch.wellernet.zeus.modules.device.service.communication.integrated;

import ch.wellernet.zeus.modules.device.model.BuiltInDeviceType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static java.util.Collections.emptyList;

@Data
@ConfigurationProperties(prefix = "zeus.integrated-control-unit")
public class IntegratedControlUnitProperties {

  private UUID id;
  private List<DriverMapping> driverMappings = emptyList();

  @Data
  public static class DriverMapping {
    private UUID deviceId;
    private BuiltInDeviceType deviceType;
    private List<DriverDefinition> drivers;

    @Data
    static class DriverDefinition {
      private String name;
      private Properties properties;
    }
  }
}
