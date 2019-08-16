package ch.wellernet.zeus.modules.device.controller;

import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.service.DeviceService;
import ch.wellernet.zeus.modules.device.service.communication.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.OptimisticLockException;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static ch.wellernet.zeus.modules.device.controller.DeviceController.COMMUNICATION_INTERRUPTED;
import static ch.wellernet.zeus.modules.device.controller.DeviceController.COMMUNICATION_NOT_SUCCESSFUL;
import static ch.wellernet.zeus.modules.device.model.BuiltInDeviceType.GENERIC_SWITCH;
import static ch.wellernet.zeus.modules.device.model.Command.GET_SWITCH_STATE;
import static ch.wellernet.zeus.modules.device.model.State.ON;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(classes = DeviceController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class DeviceControllerTest {

  // test data
  private static final Device DEVICE_1 = Device.builder().id(randomUUID()).name("Device 1").type(GENERIC_SWITCH).build();
  private static final Device DEVICE_2 = Device.builder().id(randomUUID()).name("Device 2").type(GENERIC_SWITCH).build();
  private static final Device DEVICE_3 = Device.builder().id(randomUUID()).name("Device 3").type(GENERIC_SWITCH).build();
  private static final List<Device> DEVICES = newArrayList(DEVICE_1, DEVICE_2, DEVICE_3);

  // object under test
  private @Autowired
  DeviceController deviceController;

  private @MockBean
  DeviceService deviceService;
  private @MockBean
  CommunicationService communicationService;
  private @MockBean
  CommunicationServiceRegistry communicationServiceRegistry;

  @Test
  public void executeCommandShouldTransmitCommandToCommunicationService()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    given(deviceService.findById(DEVICE_1.getId())).willReturn(DEVICE_1);

    final ResponseEntity<Device> response = deviceController.executeCommand(DEVICE_1.getId(), GET_SWITCH_STATE);

    // then
    verify(deviceService).executeCommand(DEVICE_1, GET_SWITCH_STATE);
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void executeCommandThrowNoSuchElementExceptionWhenDeviceDoesNotExists()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    given(deviceService.findById(DEVICE_1.getId())).willThrow(NoSuchElementException.class);
    given(communicationServiceRegistry.findByName(any())).willReturn(communicationService);

    // when
    deviceController.executeCommand(DEVICE_1.getId(), GET_SWITCH_STATE);

    // then an exception is expected
  }

  @Test
  public void handleOptimisticLockExceptionShouldReturnConflictStatus() {
    // given nothing special

    // when
    final ResponseEntity<Device> response = deviceController.handleOptimisticLockException(new OptimisticLockException(DEVICE_1));

    // then
    assertThat(response.getStatusCode(), is(PRECONDITION_FAILED));
    assertThat(response.getBody(), is(DEVICE_1));
  }

  @Test
  public void handleNoSuchElementExceptionShouldReturnNotFoundStatus() {
    // given nothing special

    // when
    final ResponseEntity<String> response = deviceController.handleNoSuchElementException();

    // then
    assertThat(response.getStatusCode(), is(NOT_FOUND));
  }

  @Test
  public void saveShouldReturnSaveNewDevice() {
    // given
    given(deviceService.save(DEVICE_1)).willReturn(DEVICE_1);

    // when
    final ResponseEntity<Device> response = deviceController.save(DEVICE_1);

    // then
    assertThat(response.getBody(), is(DEVICE_1));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void deleteShouldRemoveExistingDevice() {
    // given
    final UUID deviceId = randomUUID();

    // when
    deviceController.delete(deviceId);

    // then
    verify(deviceService).delete(deviceId);
  }

  @Test(expected = NoSuchElementException.class)
  public void deleteShouldThrowNoSuchElementExceptionIfDeviceDoesNotExists() {
    // given
    doThrow(NoSuchElementException.class).when(deviceService).delete(any());

    // when
    deviceController.delete(randomUUID());

    // then an exception is expected
  }

  @Test
  public void findAllShouldReturnCollectionOfDevices() {
    // given
    given(deviceService.findAll()).willReturn(DEVICES);

    // when
    final ResponseEntity<Collection<Device>> response = deviceController.findAll();

    // then
    assertThat(response.getBody(), containsInAnyOrder(DEVICE_1, DEVICE_2, DEVICE_3));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findAllShouldReturnEmptyCollectionWhenNoDevicesAreAvailable() {
    // given
    given(deviceService.findAll()).willReturn(emptyList());

    // when
    final ResponseEntity<Collection<Device>> response = deviceController.findAll();

    // then
    assertThat(response.getBody(), is(empty()));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findByIdShouldReturnDevice() {
    // given
    given(deviceService.findById(DEVICE_1.getId())).willReturn(DEVICE_1);

    // when
    final ResponseEntity<Device> response = deviceController.findById(DEVICE_1.getId());

    // then
    assertThat(response.getBody(), is(DEVICE_1));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void findByIdShouldThrowNoSuchElementExceptionIfDeviceDoesNotExists() {
    // given
    given(deviceService.findById(DEVICE_1.getId())).willThrow(NoSuchElementException.class);

    // when
    deviceController.findById(DEVICE_1.getId());

    // then an exception is expected
  }

  @Test
  public void handleCommunicationInterruptedExceptionShouldReturnCommunicationInterruptedStatus() {
    // given nothing special

    // when
    final ResponseEntity<Device> response = deviceController.handleCommunicationInterruptedException(
        new CommunicationInterruptedException("something went terribly wrong!"));

    // then
    assertThat(response.getStatusCodeValue(), is(COMMUNICATION_INTERRUPTED));
  }

  @Test
  public void handleCommunicationNotSuccessfulExceptionShouldReturnCommunicationNotSuccessfulStatus() {
    // given nothing special

    // when
    final ResponseEntity<Device> response = deviceController.handleCommunicationNotSuccessfulException(
        new CommunicationNotSuccessfulException("can't do it, sorry", ON));

    // then
    assertThat(response.getStatusCodeValue(), is(COMMUNICATION_NOT_SUCCESSFUL));
  }

  @Test
  public void handleUndefinedCommandExceptionShouldReturnNotFoundStatus() {
    // given nothing special

    // when
    final ResponseEntity<String> response = deviceController.handleUndefinedCommandException();

    // then
    assertThat(response.getStatusCode(), is(NOT_FOUND));
  }
}
