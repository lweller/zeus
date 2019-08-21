package ch.wellernet.zeus.modules.device.controller.mapper;

import ch.wellernet.zeus.modules.device.controller.dto.ControlUnitAddressDto;
import ch.wellernet.zeus.modules.device.controller.dto.IntegratedControlUnitAddressDto;
import ch.wellernet.zeus.modules.device.controller.dto.TcpControlUnitAddressDto;
import ch.wellernet.zeus.modules.device.model.ControlUnitAddress;
import ch.wellernet.zeus.modules.device.model.IntegratedControlUnitAddress;
import ch.wellernet.zeus.modules.device.model.TcpControlUnitAddress;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Optional;

@Mapper(componentModel = "spring")
abstract class ControlUnitAddressMapper {

  ControlUnitAddressDto toDto(final ControlUnitAddress controlUnitAddress) {
    return Optional
               .ofNullable(controlUnitAddress)
               .map(obj -> obj.dispatch(
                   new ControlUnitAddress.Dispatcher<ControlUnitAddressDto>() {
                     @Override
                     public ControlUnitAddressDto execute(final TcpControlUnitAddress controlUnitAddress) {
                       return toDto(controlUnitAddress);
                     }

                     @Override
                     public ControlUnitAddressDto execute(final IntegratedControlUnitAddress controlUnitAddress) {
                       return toDto(controlUnitAddress);
                     }
                   }))
               .orElse(null);
  }

  abstract IntegratedControlUnitAddressDto toDto(IntegratedControlUnitAddress controlUnitAddress);

  abstract TcpControlUnitAddressDto toDto(TcpControlUnitAddress controlUnitAddress);

  ControlUnitAddress createOrUpdateFrom(final ControlUnitAddressDto controlUnitAddressDto, @Context final ControlUnitMapperContext context) {
    return Optional
               .ofNullable(controlUnitAddressDto)
               .map(dto -> dto.dispatch(
                   new ControlUnitAddressDto.Dispatcher<ControlUnitAddress>() {
                     @Override
                     public IntegratedControlUnitAddress execute(final IntegratedControlUnitAddressDto controlUnitAddressDto) {
                       return context.getAddressOfType(IntegratedControlUnitAddress.class)
                                  .map(controlUnitAddress -> copy(controlUnitAddressDto, controlUnitAddress))
                                  .orElseGet(() -> createFrom(controlUnitAddressDto));
                     }

                     @Override
                     public TcpControlUnitAddress execute(final TcpControlUnitAddressDto controlUnitAddressDto) {
                       return context.getAddressOfType(TcpControlUnitAddress.class)
                                  .map(controlUnitAddress -> copy(controlUnitAddressDto, controlUnitAddress))
                                  .orElseGet(() -> createFrom(controlUnitAddressDto));
                     }
                   }))
               .orElse(null);
  }

  abstract IntegratedControlUnitAddress createFrom(IntegratedControlUnitAddressDto controlUnitAddressDto);

  abstract IntegratedControlUnitAddress copy(IntegratedControlUnitAddressDto controlUnitAddressDto, @MappingTarget IntegratedControlUnitAddress controlUnitAddress);

  abstract TcpControlUnitAddress createFrom(TcpControlUnitAddressDto controlUnitAddressDto);

  abstract TcpControlUnitAddress copy(TcpControlUnitAddressDto controlUnitAddressDto, @MappingTarget TcpControlUnitAddress controlUnitAddress);
}
