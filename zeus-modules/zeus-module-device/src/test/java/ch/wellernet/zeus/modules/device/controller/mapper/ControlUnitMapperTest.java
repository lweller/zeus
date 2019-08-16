package ch.wellernet.zeus.modules.device.controller.mapper;

import ch.wellernet.zeus.modules.device.controller.dto.ControlUnitDto;
import ch.wellernet.zeus.modules.device.controller.dto.TcpControlUnitAddressDto;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.IntegratedControlUnitAddress;
import ch.wellernet.zeus.modules.device.model.TcpControlUnitAddress;
import ch.wellernet.zeus.modules.device.repository.ControlUnitRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(classes = {ControlUnitMapperImpl.class, ControlUnitAddressMapperImpl.class}, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class ControlUnitMapperTest {

  // object under test
  @Autowired
  private ControlUnitMapper controlUnitMapper;

  @MockBean
  private ControlUnitRepository controlUnitRepository;

  @MockBean
  private DeviceMapper deviceMapper;

  @Test
  public void createOrUpdateShouldCreateControlUnitWhenNotExisting() {
    // given
    final ControlUnitDto controlUnitDto = ControlUnitDto.builder()
                                              .id(randomUUID())
                                              .address(TcpControlUnitAddressDto.builder()
                                                           .host("zeus.example.com")
                                                           .port(8080)
                                                           .build())
                                              .build();
    given(controlUnitRepository.findById(controlUnitDto.getId())).willReturn(Optional.empty());
    given(controlUnitRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
    given(deviceMapper.updateFrom(any())).willReturn(Stream.of(Device.builder().build()).collect(toSet()));

    // when
    final ControlUnit createdControlUnit = controlUnitMapper.createOrUpdateFrom(controlUnitDto, new ControlUnitMapperContext());

    // then
    assertThat(createdControlUnit.getId(), is(controlUnitDto.getId()));
    assertThat(createdControlUnit.getAddress(), isA(TcpControlUnitAddress.class));
    assertThat(((TcpControlUnitAddress) createdControlUnit.getAddress()).getHost(), is("zeus.example.com"));
    assertThat(((TcpControlUnitAddress) createdControlUnit.getAddress()).getPort(), is(8080));
    assertThat(createdControlUnit.getDevices(), hasSize(1));
    assertThat(createdControlUnit.getDevices(), everyItem(hasProperty("controlUnit", is(createdControlUnit))));
  }

  @Test
  public void createOrUpdateShouldUpdateControlUnitWhenAlreadyExisting() {
    // given
    final TcpControlUnitAddress controlUnitAddress;
    final ControlUnit controlUnit = ControlUnit.builder()
                                        .id(randomUUID())
                                        .address(controlUnitAddress = TcpControlUnitAddress.builder().build())
                                        .build();
    final ControlUnitDto controlUnitDto = ControlUnitDto.builder()
                                              .id(controlUnit.getId())
                                              .version(42)
                                              .address(TcpControlUnitAddressDto.builder()
                                                           .version(142)
                                                           .host("zeus.example.com")
                                                           .port(8080)
                                                           .build())
                                              .build();
    given(controlUnitRepository.findById(controlUnitDto.getId())).willReturn(Optional.of(controlUnit));
    given(deviceMapper.updateFrom(any())).willReturn(Stream.of(Device.builder().build()).collect(toSet()));

    // when
    final ControlUnit updatedControlUnit = controlUnitMapper.createOrUpdateFrom(controlUnitDto, new ControlUnitMapperContext());

    // then
    assertThat(updatedControlUnit.getId(), is(controlUnit.getId()));
    assertThat(updatedControlUnit.getVersion(), is(42L));
    assertThat(updatedControlUnit.getAddress(), sameInstance(controlUnitAddress));
    assertThat(updatedControlUnit.getAddress().getVersion(), is(142L));
    assertThat(((TcpControlUnitAddress) updatedControlUnit.getAddress()).getHost(), is("zeus.example.com"));
    assertThat(((TcpControlUnitAddress) updatedControlUnit.getAddress()).getPort(), is(8080));
    assertThat(updatedControlUnit.getDevices(), hasSize(1));
    assertThat(updatedControlUnit.getDevices(), everyItem(hasProperty("controlUnit", is(updatedControlUnit))));
  }

  @Test
  public void createOrUpdateShouldCreateNewControlUnitAddressWhenTypeChanges() {
    // given
    final ControlUnit controlUnit = ControlUnit.builder()
                                        .id(randomUUID())
                                        .address(new IntegratedControlUnitAddress())
                                        .build();
    final ControlUnitDto controlUnitDto = ControlUnitDto.builder()
                                              .id(controlUnit.getId())
                                              .address(TcpControlUnitAddressDto.builder()
                                                           .host("zeus.example.com")
                                                           .port(8080)
                                                           .build())
                                              .build();
    given(controlUnitRepository.findById(controlUnitDto.getId())).willReturn(Optional.of(controlUnit));
    given(deviceMapper.updateFrom(any())).willReturn(Stream.of(Device.builder().build()).collect(toSet()));

    // when
    final ControlUnit updatedControlUnit = controlUnitMapper.createOrUpdateFrom(controlUnitDto, new ControlUnitMapperContext());

    // then
    assertThat(updatedControlUnit.getId(), is(controlUnit.getId()));
    assertThat(updatedControlUnit.getAddress(), isA(TcpControlUnitAddress.class));
    assertThat(((TcpControlUnitAddress) updatedControlUnit.getAddress()).getHost(), is("zeus.example.com"));
    assertThat(((TcpControlUnitAddress) updatedControlUnit.getAddress()).getPort(), is(8080));
    assertThat(updatedControlUnit.getDevices(), hasSize(1));
    assertThat(updatedControlUnit.getDevices(), everyItem(hasProperty("controlUnit", is(updatedControlUnit))));
  }

  @Test
  public void toDtoShouldConvertCorrectly() {
    // given
    final ControlUnit controlUnit = ControlUnit.builder()
                                        .id(randomUUID())
                                        .build();

    // when
    final ControlUnitDto controlUnitDto = controlUnitMapper.toDto(controlUnit);

    // then
    assertThat(controlUnitDto.getId(), is(controlUnit.getId()));
  }
}