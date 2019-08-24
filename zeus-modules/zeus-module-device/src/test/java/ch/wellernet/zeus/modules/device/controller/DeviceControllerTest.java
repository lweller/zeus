package ch.wellernet.zeus.modules.device.controller;

import ch.wellernet.zeus.modules.device.controller.dto.DeviceDto;
import ch.wellernet.zeus.modules.device.controller.mapper.DeviceMapper;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static ch.wellernet.zeus.modules.device.controller.DeviceController.COMMUNICATION_INTERRUPTED;
import static ch.wellernet.zeus.modules.device.controller.DeviceController.COMMUNICATION_NOT_SUCCESSFUL;
import static ch.wellernet.zeus.modules.device.model.Command.GET_SWITCH_STATE;
import static ch.wellernet.zeus.modules.device.model.State.ON;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(classes = DeviceController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class DeviceControllerTest {

  // object under test
  @Autowired
  private DeviceController deviceController;

  @MockBean
  private DeviceRepository deviceRepository;

  @MockBean
  private DeviceMapper deviceMapper;

  @MockBean
  private DeviceService deviceService;

  @MockBean
  private CommunicationService communicationService;

  @MockBean
  private CommunicationServiceRegistry communicationServiceRegistry;

  @Test
  public void findAllShouldReturnCollectionOfDevices() {
    // given
    final Device device1 = Device.builder().id(randomUUID()).build();
    final Device device2 = Device.builder().id(randomUUID()).build();
    final Collection<Device> devices = Stream.of(device1, device2).collect(toList());
    final DeviceDto deviceDto1 = DeviceDto.builder().id(randomUUID()).build();
    final DeviceDto deviceDto2 = DeviceDto.builder().id(randomUUID()).build();
    final Collection<DeviceDto> deviceDtos = Stream.of(deviceDto1, deviceDto2).collect(toList());
    given(deviceRepository.findAll()).willReturn(devices);
    given(deviceMapper.toDtos(devices)).willReturn(deviceDtos);

    // when
    final ResponseEntity<Collection<DeviceDto>> response = deviceController.findAll();

    // then
    assertThat(response.getBody(), containsInAnyOrder(deviceDto1, deviceDto2));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findByIdShouldReturnDevice() {
    // given
    final UUID deviceId = randomUUID();
    final Device device = Device.builder().build();
    final DeviceDto deviceDto = DeviceDto.builder().build();
    given(deviceRepository.findById(deviceId)).willReturn(Optional.of(device));
    given(deviceMapper.toDto(device)).willReturn(deviceDto);

    // when
    final ResponseEntity<DeviceDto> response = deviceController.findById(deviceId);

    // then
    assertThat(response.getBody(), is(deviceDto));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void findByIdShouldThrowNoSuchElementExceptionIfDeviceDoesNotExists() {
    // given
    final UUID deviceId = randomUUID();
    given(deviceRepository.findById(deviceId)).willReturn(Optional.empty());

    // when
    deviceController.findById(deviceId);

    // then an exception is expected
  }

  @Test
  public void createOrUpdateShouldReturnReattachDevice() {
    // given
    final UUID deviceId = randomUUID();
    final Device device = Device.builder().id(deviceId).build();
    final DeviceDto deviceDto = DeviceDto.builder().build();
    final DeviceDto updatedDeviceDto = DeviceDto.builder().id(deviceId).build();
    given(deviceMapper.createOrUpdateFrom(deviceDto)).willReturn(device);
    given(deviceMapper.toDto(device)).willReturn(updatedDeviceDto);

    // when
    final ResponseEntity<DeviceDto> response = deviceController.createOrUpdate(deviceDto);

    // then
    assertThat(response.getBody(), is(updatedDeviceDto));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void deleteShouldRemoveExistingDevice() {
    // given
    final UUID deviceId = randomUUID();
    given(deviceRepository.existsById(deviceId)).willReturn(true);

    // when
    deviceController.delete(deviceId);

    // then
    verify(deviceRepository).deleteById(deviceId);
  }

  @Test
  public void executeCommandShouldTransmitCommandToCommunicationService()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    final UUID deviceId = randomUUID();
    final Device device = Device.builder().id(deviceId).build();
    final DeviceDto deviceDto = DeviceDto.builder().id(deviceId).build();
    given(deviceRepository.findById(deviceId)).willReturn(Optional.of(device));
    given(deviceMapper.toDto(device)).willReturn(deviceDto);
    given(deviceService.executeCommand(device, GET_SWITCH_STATE)).willReturn(device);

    // when
    final ResponseEntity<DeviceDto> response = deviceController.executeCommand(deviceId, GET_SWITCH_STATE);

    // then
    assertThat(response.getBody(), is(deviceDto));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void executeCommandThrowNoSuchElementExceptionWhenDeviceDoesNotExists()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    final UUID deviceId = randomUUID();
    given(deviceRepository.findById(deviceId)).willReturn(Optional.empty());

    // when
    deviceController.executeCommand(deviceId, GET_SWITCH_STATE);

    // then an exception is expected
  }

  @Test
  public void handleOptimisticLockExceptionShouldReturnConflictStatus() {
    // given
    final UUID deviceId = randomUUID();
    final Device device = Device.builder().id(deviceId).build();
    final DeviceDto deviceDto = DeviceDto.builder().id(deviceId).build();
    given(deviceMapper.toDto(device)).willReturn(deviceDto);

    // when
    final ResponseEntity<DeviceDto> response = deviceController.handleOptimisticLockException(new OptimisticLockException(device));

    // then
    assertThat(response.getStatusCode(), is(PRECONDITION_FAILED));
    assertThat(response.getBody(), is(deviceDto));
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
