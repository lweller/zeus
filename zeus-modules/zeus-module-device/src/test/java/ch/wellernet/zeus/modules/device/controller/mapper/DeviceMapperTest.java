package ch.wellernet.zeus.modules.device.controller.mapper;

import ch.wellernet.zeus.modules.device.controller.dto.DeviceDto;
import ch.wellernet.zeus.modules.device.controller.dto.Reference;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(classes = DeviceMapperImpl.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class DeviceMapperTest {

  // object under test
  @Autowired
  private DeviceMapper deviceMapper;

  @MockBean
  private DeviceRepository deviceRepository;

  @MockBean
  private ControlUnitMapper controlUnitMapper;

  @Test
  public void createOrUpdateShouldCreateDeviceWhenNotExisting() {
    // given
    final ControlUnit controlUnit = ControlUnit.builder()
                                        .id(randomUUID())
                                        .build();
    final DeviceDto deviceDto = DeviceDto.builder()
                                    .id(randomUUID())
                                    .name("New Device")
                                    .controlUnit(Reference.of(controlUnit.getId()))
                                    .build();
    given(deviceRepository.findById(any())).willReturn(Optional.empty());
    given(deviceRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
    given(controlUnitMapper.resolve(deviceDto.getControlUnit())).willReturn(controlUnit);

    // when
    final Device createdDevice = deviceMapper.createOrUpdateFrom(deviceDto);

    // then
    assertThat(createdDevice.getId(), is(deviceDto.getId()));
    assertThat(createdDevice.getName(), is(deviceDto.getName()));
    assertThat(createdDevice.getControlUnit(), is(controlUnit));
  }

  @Test
  public void createOrUpdateShouldUpdateDeviceWhenAlreadyExisting() {
    // given
    final ControlUnit controlUnit = ControlUnit.builder()
                                        .id(randomUUID())
                                        .build();
    final Device device = Device.builder()
                              .id(randomUUID())
                              .build();
    final DeviceDto deviceDto = DeviceDto.builder()
                                    .id(device.getId())
                                    .name("Updated Device")
                                    .controlUnit(Reference.of(controlUnit.getId()))
                                    .build();
    given(deviceRepository.findById(deviceDto.getId())).willReturn(Optional.of(device));
    given(controlUnitMapper.resolve(deviceDto.getControlUnit())).willReturn(controlUnit);

    // when
    final Device updatedDevice = deviceMapper.createOrUpdateFrom(deviceDto);

    // then
    assertThat(updatedDevice.getId(), is(device.getId()));
    assertThat(updatedDevice.getName(), is(deviceDto.getName()));
    assertThat(updatedDevice.getControlUnit(), is(controlUnit));
  }

  @Test
  public void toDtoShouldConvertCorrectly() {
    // given
    final Device device = Device.builder()
                              .id(randomUUID())
                              .name("Device")
                              .controlUnit(ControlUnit.builder().build())
                              .build();

    // when
    final DeviceDto deviceDto = deviceMapper.toDto(device);

    // then
    assertThat(deviceDto.getId(), is(device.getId()));
    assertThat(deviceDto.getName(), is(device.getName()));
  }
}