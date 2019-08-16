package ch.wellernet.zeus.modules.device.controller.mapper;

import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.ControlUnitAddress;
import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;

import java.util.Optional;

class ControlUnitMapperContext {
  private ControlUnitAddress controlUnitAddress;

  @BeforeMapping
  void update(@MappingTarget final ControlUnit controlUnit) {
    controlUnitAddress = Optional.ofNullable(controlUnit).map(ControlUnit::getAddress).orElse(null);
  }

  <T> Optional<T> getAddressOfType(final Class<T> type) {
    return Optional.ofNullable(controlUnitAddress).filter(type::isInstance).map(type::cast);
  }
}
