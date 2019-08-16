package ch.wellernet.zeus.modules.device.controller.mapper;

import ch.wellernet.zeus.modules.device.controller.dto.DeviceDto;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring", uses = ControlUnitMapper.class)
abstract class DeviceMapper {

  @Autowired
  private DeviceRepository deviceRepository;

  abstract DeviceDto toDto(final Device device);

  Device createOrUpdateFrom(final DeviceDto deviceDto) {
    return deviceRepository
               .findById(deviceDto.getId())
               .map(device -> copy(deviceDto, device))
               .orElse(deviceRepository.save(createFrom(deviceDto)));
  }

  Set<Device> updateFrom(final Set<DeviceDto> deviceDtos) {
    return deviceDtos.stream().map(this::createOrUpdateFrom).collect(toSet());
  }

  protected abstract Device copy(final DeviceDto deviceDto, @MappingTarget final Device device);

  protected abstract Device createFrom(DeviceDto deviceDto);
}
