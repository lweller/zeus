package ch.wellernet.zeus.modules.device.service;

import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.ControlUnitAddress;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationInterruptedException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationNotSuccessfulException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationService;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationServiceRegistry;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.wellernet.zeus.modules.device.model.BuiltInDeviceType.GENERIC_SWITCH;
import static ch.wellernet.zeus.modules.device.model.Command.GET_SWITCH_STATE;
import static java.util.UUID.randomUUID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(classes = DeviceService.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class DeviceServiceTest {
  // test data
  private static final String COMMUNICATION_SERVICE_NAME = "mock";
  private static final ControlUnit CONTROL_UNIT = ControlUnit.builder().id(randomUUID())
      .address(new ControlUnitAddress(COMMUNICATION_SERVICE_NAME) {
      }).build();
  private static final Device DEVICE = Device.builder().id(randomUUID()).name("Device 1").type(GENERIC_SWITCH)
      .controlUnit(CONTROL_UNIT).build();

  // object under test
  private @Autowired DeviceService deviceService;

  private @MockBean DeviceRepository deviceRepository;
  private @MockBean CommunicationService communicationService;
  private @MockBean CommunicationServiceRegistry communicationServiceRegistry;

  @Test
  public void sendCommandShouldTransmitCommandWithDataToCommunicationService()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    given(communicationServiceRegistry.findByName(COMMUNICATION_SERVICE_NAME)).willReturn(communicationService);
    final String data = "some data";

    // when
    deviceService.sendCommand(DEVICE, GET_SWITCH_STATE, data);

    // then
    verify(communicationService).sendCommand(DEVICE, GET_SWITCH_STATE, data);
    verify(deviceRepository).save(DEVICE);
  }

  @Test
  public void sendCommandShouldTransmitCommandWithoutDataToCommunicationService()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    given(communicationServiceRegistry.findByName(COMMUNICATION_SERVICE_NAME)).willReturn(communicationService);

    // when
    deviceService.sendCommand(DEVICE, GET_SWITCH_STATE);

    // then
    verify(communicationService).sendCommand(DEVICE, GET_SWITCH_STATE, null);
    verify(deviceRepository).save(DEVICE);
  }
}
