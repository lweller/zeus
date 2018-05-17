package ch.wellernet.zeus.server.controller;

import static ch.wellernet.zeus.server.model.BuiltInDeviceType.GENERIC_SWITCH;
import static ch.wellernet.zeus.server.model.Command.GET_SWITCH_STATE;
import static ch.wellernet.zeus.server.model.State.ON;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.OK;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import ch.wellernet.zeus.server.model.ControlUnit;
import ch.wellernet.zeus.server.model.ControlUnitAddress;
import ch.wellernet.zeus.server.model.Device;
import ch.wellernet.zeus.server.model.State;
import ch.wellernet.zeus.server.repository.DeviceRepository;
import ch.wellernet.zeus.server.service.communication.CommunicationService;
import ch.wellernet.zeus.server.service.communication.CommunicationServiceRegistry;
import ch.wellernet.zeus.server.service.communication.integrated.drivers.UndefinedCommandException;

@SpringBootTest(classes = DeviceController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class DeviceControllerTest {

	private static final String COMMUNICATION_SERVICE_NAME = "mock";
	// test data
	private final ControlUnit CONTROL_UNIT = ControlUnit.builder().id(randomUUID())
			.address(new ControlUnitAddress(COMMUNICATION_SERVICE_NAME) {
			}).build();
	private final Device DEVICE_1 = Device.builder().id(randomUUID()).name("Device 1").type(GENERIC_SWITCH)
			.controlUnit(CONTROL_UNIT).build();
	private final Device DEVICE_2 = Device.builder().id(randomUUID()).name("Device 2").type(GENERIC_SWITCH)
			.controlUnit(CONTROL_UNIT).build();
	private final Device DEVICE_3 = Device.builder().id(randomUUID()).name("Device 3").type(GENERIC_SWITCH)
			.controlUnit(CONTROL_UNIT).build();
	private final Set<Device> DEVICES = newHashSet(DEVICE_1, DEVICE_2, DEVICE_3);

	// object under test
	@Autowired
	private DeviceController deviceController;

	@MockBean
	private DeviceRepository deviceRepository;

	@MockBean
	private CommunicationServiceRegistry communicationServiceRegistry;

	@MockBean
	private CommunicationService comunicationService;

	// ----------------------------- findAll -----------------------------

	@Test
	public void findAllShouldReturnCollectionOnDevices() {
		given(deviceRepository.findAll()).willReturn(DEVICES);

		final ResponseEntity<Collection<Device>> response = deviceController.findAll();

		assertThat(response.getBody(), containsInAnyOrder(DEVICE_1, DEVICE_2, DEVICE_3));
		assertThat(response.getStatusCode(), is(OK));
	}

	@Test
	public void findAllShouldReturnEmptyCollectionIfNoDevicesAreAvailable() {
		given(deviceRepository.findAll()).willReturn(emptySet());

		final ResponseEntity<Collection<Device>> response = deviceController.findAll();

		assertThat(response.getBody(), is(empty()));
		assertThat(response.getStatusCode(), is(OK));
	}

	// ----------------------------- findById -----------------------------

	@Test
	public void findByIdShouldReturnDevice() {
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.of(DEVICE_1));

		final ResponseEntity<Device> response = deviceController.findById(DEVICE_1.getId());

		assertThat(response.getBody(), is(DEVICE_1));
		assertThat(response.getStatusCode(), is(OK));
	}

	@Test(expected = NoSuchElementException.class)
	public void findByIdShouldReturnNullWithStatusNotFoundIfNoDevicesAreAvailable() {
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.empty());

		deviceController.findById(DEVICE_1.getId());

		// exception
	}

	// ----------------------------- sendCommand -----------------------------

	@Test(expected = NoSuchElementException.class)
	public void sendCommandShouldThrowNoSuchElementExceptionIfNoDevicesAreAvailable() throws UndefinedCommandException {
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.empty());
		given(communicationServiceRegistry.findByName(COMMUNICATION_SERVICE_NAME)).willReturn(comunicationService);

		deviceController.sendCommand(DEVICE_1.getId(), GET_SWITCH_STATE);

		// exception
	}

	@Test
	public void sendCommandShouldTransmitCommandToCommunicationService() throws UndefinedCommandException {
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.of(DEVICE_1));
		given(communicationServiceRegistry.findByName(COMMUNICATION_SERVICE_NAME)).willReturn(comunicationService);

		final ResponseEntity<Device> response = deviceController.sendCommand(DEVICE_1.getId(), GET_SWITCH_STATE);

		verify(comunicationService).sendCommand(DEVICE_1, GET_SWITCH_STATE);
		assertThat(response.getBody(), is(DEVICE_1));
		assertThat(response.getStatusCode(), is(OK));
	}

	// ----------------------------- update -----------------------------

	@Test(expected = NoSuchElementException.class)
	public void updateShouldThrowNoSuchElementExceptionIfNoDevicesAreAvailable() throws UndefinedCommandException {
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.empty());

		deviceController.update(DEVICE_1.getId(), DEVICE_1);

		// exception
	}

	public void updateShouldUpdateOnlyName() throws UndefinedCommandException {
		given(deviceRepository.findById(DEVICE_1.getId())).willReturn(Optional.empty());
		given(communicationServiceRegistry.findByName(COMMUNICATION_SERVICE_NAME)).willReturn(comunicationService);
		final State originalState = DEVICE_1.getState();
		final Device updatedDevice = Device.builder().name("Renamed device").type(GENERIC_SWITCH).build();
		// change state to verify it will not updated
		updatedDevice.setState(ON);

		final ResponseEntity<Device> response = deviceController.update(DEVICE_1.getId(), updatedDevice);

		verify(deviceRepository).save(DEVICE_1);
		assertThat(response.getBody(), is(DEVICE_1));
		assertThat(response.getBody().getName(), is("Renamed device"));
		assertThat(response.getBody().getState(), is(originalState));
		assertThat(response.getStatusCode(), is(OK));
	}
}
