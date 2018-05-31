package ch.wellernet.zeus.modules.device.controller;

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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import ch.wellernet.zeus.modules.device.controller.DeviceController;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.ControlUnitAddress;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationService;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationServiceRegistry;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.UndefinedCommandException;

@SpringBootTest(classes = DeviceController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class DeviceControllerTest {

	// test data
	private static final String COMMUNICATION_SERVICE_NAME = "mock";
	private static final ControlUnit CONTROL_UNIT = ControlUnit.builder().id(randomUUID())
			.address(new ControlUnitAddress(COMMUNICATION_SERVICE_NAME) {
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
	private DeviceController deviceController;

	@MockBean
	private DeviceRepository deviceRepository;

	@MockBean
	private CommunicationServiceRegistry communicationServiceRegistry;

	@MockBean
	private CommunicationService comunicationService;

	@Test
	public void findAllShouldReturnCollectionOfDevices() {
		// given
		given(deviceRepository.findAll()).willReturn(DEVICES);

		// when
		final ResponseEntity<Collection<Device>> response = deviceController.findAll();

		// then
		assertThat(response.getBody(), containsInAnyOrder(DEVICE_1, DEVICE_2, DEVICE_3));
		assertThat(response.getStatusCode(), is(OK));
	}

	@Test
	public void findAllShouldReturnEmptyCollectionIfNoDevicesAreAvailable() {
		// given
		given(deviceRepository.findAll()).willReturn(emptyList());

		// when
		final ResponseEntity<Collection<Device>> response = deviceController.findAll();

		// then
		assertThat(response.getBody(), is(empty()));
		assertThat(response.getStatusCode(), is(OK));
	}

	@Test
	public void findByIdShouldReturnDevice() {
		// given
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.of(DEVICE_1));

		// when
		final ResponseEntity<Device> response = deviceController.findById(DEVICE_1.getId());

		// then
		assertThat(response.getBody(), is(DEVICE_1));
		assertThat(response.getStatusCode(), is(OK));
	}

	@Test(expected = NoSuchElementException.class)
	public void findByIdShouldThrowNoSuchElementExceptionIfDeviceDoesNotExists() {
		// given
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.empty());

		deviceController.findById(DEVICE_1.getId());

		// then an exception is expected
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
	public void handleUndefinedCommandExceptionShouldReturnNotAcceptableStatus() {
		// given nothing special

		// when
		final ResponseEntity<String> response = deviceController.handleUndefinedCommandException();

		// then
		assertThat(response.getStatusCode(), is(NOT_ACCEPTABLE));
	}

	@Test(expected = NoSuchElementException.class)
	public void sendCommandShouldThrowNoSuchElementExceptionIfDeviceDoesNotExists() throws UndefinedCommandException {
		// given
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.empty());
		given(communicationServiceRegistry.findByName(COMMUNICATION_SERVICE_NAME)).willReturn(comunicationService);

		// when
		deviceController.sendCommand(DEVICE_1.getId(), GET_SWITCH_STATE);

		// then an exception is expected
	}

	@Test
	public void sendCommandShouldTransmitCommandToCommunicationService() throws UndefinedCommandException {
		// given
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.of(DEVICE_1));
		given(communicationServiceRegistry.findByName(COMMUNICATION_SERVICE_NAME)).willReturn(comunicationService);

		// when
		final ResponseEntity<Device> response = deviceController.sendCommand(DEVICE_1.getId(), GET_SWITCH_STATE);

		// then
		verify(comunicationService).sendCommand(DEVICE_1, GET_SWITCH_STATE);
		verify(deviceRepository).save(DEVICE_1);
		assertThat(response.getBody(), is(DEVICE_1));
		assertThat(response.getStatusCode(), is(OK));
	}

	@Test(expected = NoSuchElementException.class)
	public void updateShouldThrowNoSuchElementExceptionIfDeviceDoesNotExists() throws UndefinedCommandException {
		// given
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.empty());

		// when
		deviceController.update(DEVICE_1.getId(), DEVICE_1, DEVICE_1.getVersion());

		// then an exception is expected
	}

	@Test
	public void updateShouldUpdateOnlyName() throws UndefinedCommandException, InterruptedException {
		// given
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.of(DEVICE_1));
		given(communicationServiceRegistry.findByName(COMMUNICATION_SERVICE_NAME)).willReturn(comunicationService);
		final State originalState = DEVICE_1.getState();
		final Device updatedDevice = Device.builder().name("Renamed device").type(GENERIC_SWITCH).build();
		// change state to verify it will not updated
		updatedDevice.setState(ON);

		// when
		final ResponseEntity<Device> response = deviceController.update(DEVICE_1.getId(), updatedDevice,
				DEVICE_1.getVersion());

		// then
		verify(deviceRepository).save(DEVICE_1);
		assertThat(response.getBody(), is(DEVICE_1));
		assertThat(response.getBody().getName(), is("Renamed device"));
		assertThat(response.getBody().getState(), is(originalState));
		assertThat(response.getStatusCode(), is(OK));
	}
}
