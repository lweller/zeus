package ch.wellernet.zeus.modules.device.controller.mapper;

import ch.wellernet.zeus.modules.device.controller.dto.ControlUnitDto;
import ch.wellernet.zeus.modules.device.controller.dto.Reference;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.repository.ControlUnitRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = {DeviceMapper.class, ControlUnitAddressMapper.class})
abstract class ControlUnitMapper {

  @Autowired
  private ControlUnitRepository controlUnitRepository;

  abstract ControlUnitDto toDto(final ControlUnit controlUnit);

  ControlUnit createOrUpdateFrom(final ControlUnitDto controlUnitDto, @Context final ControlUnitMapperContext context) {
    final ControlUnit updatedControlUnit = controlUnitRepository
                                               .findById(controlUnitDto.getId())
                                               .map(controlUnit -> copy(controlUnitDto, controlUnit, context))
                                               .orElse(controlUnitRepository.save(createFrom(controlUnitDto, context)));
    updatedControlUnit.getDevices().forEach(device -> device.setControlUnit(updatedControlUnit));
    return updatedControlUnit;
  }

  protected abstract ControlUnit copy(final ControlUnitDto controlUnitDto, @MappingTarget final ControlUnit controlUnit, @Context ControlUnitMapperContext context);

  protected abstract ControlUnit createFrom(ControlUnitDto controlUnitDto, @Context ControlUnitMapperContext context);

  ControlUnit resolve(final Reference controlUnitReference) {
    return Optional
               .ofNullable(controlUnitReference)
               .flatMap(ref -> controlUnitRepository.findById(ref.getId()))
               .orElse(null);
  }
}


