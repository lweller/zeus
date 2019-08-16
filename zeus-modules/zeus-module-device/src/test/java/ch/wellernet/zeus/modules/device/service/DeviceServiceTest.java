package ch.wellernet.zeus.modules.device.service;

import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.ControlUnitAddress;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import ch.wellernet.zeus.modules.device.service.communication.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static ch.wellernet.zeus.modules.device.model.BuiltInDeviceType.GENERIC_SWITCH;
import static ch.wellernet.zeus.modules.device.model.Command.GET_SWITCH_STATE;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Optional.of;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(classes = DeviceService.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class DeviceServiceTest {
  // test data
  private static final String COMMUNICATION_SERVICE_NAME = "mock";
  private static final ControlUnit CONTROL_UNIT = ControlUnit.builder()
                                                      .id(randomUUID())
                                                      .address(new ControlUnitAddress(COMMUNICATION_SERVICE_NAME) {
                                                        @Override
                                                        public <T> T dispatch(final Dispatcher<T> dispatcher) {
                                                          return null;
                                                        }
                                                      }).build();
  private static final Device DEVICE_1 = Device.builder().id(randomUUID()).name("Device 1").type(GENERIC_SWITCH)
                                             .controlUnit(CONTROL_UNIT).build();
  private static final Device DEVICE_2 = Device.builder().id(randomUUID()).name("Device 2").type(GENERIC_SWITCH)
                                             .controlUnit(CONTROL_UNIT).build();
  private static final Device DEVICE_3 = Device.builder().id(randomUUID()).name("Device 3").type(GENERIC_SWITCH)
                                             .controlUnit(CONTROL_UNIT).build();
  private static final List<Device> DEVICES = newArrayList(DEVICE_1, DEVICE_2, DEVICE_3);

  // object under test
  @Autowired
  private DeviceService deviceService;

  @MockBean
  private DeviceRepository deviceRepository;

  @MockBean
  private CommunicationService communicationService;

  @MockBean
  private CommunicationServiceRegistry communicationServiceRegistry;

  @Test
  public void deleteShouldRemoveExistingDevice() {
    // given
    final UUID deviceId = randomUUID();
    given(deviceRepository.existsById(any())).willReturn(true);

    // when
    deviceService.delete(deviceId);

    // then
    verify(deviceRepository).deleteById(deviceId);
  }

  @Test(expected = NoSuchElementException.class)
  public void deleteShouldThrowAnExceptionIfDeviceDoesNotExist() {
    // given
    given(deviceRepository.existsById(any())).willReturn(false);

    // when
    deviceService.delete(randomUUID());

    // then an exception is expected
  }

  @Test
  public void saveShouldSaveNewDevice() {
    // given
    final Device device = defaults(Device.builder()).build();
    given(deviceRepository.findById(device.getId())).willReturn(Optional.empty());
    given(deviceRepository.save(device)).willReturn(device);

    // when
    final Device savedDevice = deviceService.save(device);

    // then
    assertThat(savedDevice, is(device));
    verify(deviceRepository).save(device);
  }

  @Test
  public void findAllShouldReturnCollectionOfDevices() {
    // given
    given(deviceRepository.findAll()).willReturn(DEVICES);

    // when
    final Collection<Device> devices = deviceService.findAll();

    // then
    assertThat(devices, containsInAnyOrder(DEVICE_1, DEVICE_2, DEVICE_3));
  }

  @Test
  public void findAllShouldReturnEmptyCollectionWhenNoDevicesAreAvailable() {
    // given
    given(deviceRepository.findAll()).willReturn(emptyList());

    // when
    final Collection<Device> devices = deviceService.findAll();

    // then
    assertThat(devices, is(empty()));
  }

  @Test(expected = NoSuchElementException.class)
  public void findByIdShouldThrowAnExceptionWhenDeviceDoesNotExists() {
    // given
    given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.empty());

    // when
    deviceService.findById(DEVICE_1.getId());

    // then an exception is expected
  }

  @Test
  public void findByIdShouldReturnDevice() {
    // given
    given(deviceRepository.findById(DEVICE_1.getId())).willReturn(of(DEVICE_1));

    // when
    final Device device = deviceService.findById(DEVICE_1.getId());

    // then
    assertThat(device, is(DEVICE_1));
  }

  @Test
  public void sendCommandShouldTransmitCommandWithDataToCommunicationService()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    given(communicationServiceRegistry.findByName(COMMUNICATION_SERVICE_NAME)).willReturn(communicationService);
    final String data = "some data";

    // when
    deviceService.executeCommand(DEVICE_1, GET_SWITCH_STATE, data);

    // then
    verify(communicationService).sendCommand(DEVICE_1, GET_SWITCH_STATE, data);
    verify(deviceRepository).save(DEVICE_1);
  }

  @Test
  public void sendCommandShouldTransmitCommandWithoutDataToCommunicationService()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    given(communicationServiceRegistry.findByName(COMMUNICATION_SERVICE_NAME)).willReturn(communicationService);

    // when
    deviceService.executeCommand(DEVICE_1, GET_SWITCH_STATE);

    // then
    verify(communicationService).sendCommand(DEVICE_1, GET_SWITCH_STATE, null);
    verify(deviceRepository).save(DEVICE_1);
  }

  private Device.DeviceBuilder defaults(final Device.DeviceBuilder builder) {
    return builder.id(randomUUID()).controlUnit(CONTROL_UNIT).name("Test Device");
  }
}
