
package ch.wellernet.zeus.modules.device.controller;

import static java.util.Collections.emptySet;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Sets;

import ch.wellernet.zeus.modules.device.controller.ControlUnitController;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.ControlUnitAddress;
import ch.wellernet.zeus.modules.device.model.IntegratedControlUnitAddress;
import ch.wellernet.zeus.modules.device.repository.ControlUnitRepository;

@SpringBootTest(classes = ControlUnitController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class ControlUnitControllerTest {
	// test data
	private static final String COMMUNICATION_SERVICE_NAME = "mock";
	private static final ControlUnit INTEGRATED_CONTROL_UNIT = ControlUnit.builder().id(randomUUID())
			.address(new IntegratedControlUnitAddress()).build();
	private static final ControlUnit CONTROL_UNIT_1 = ControlUnit.builder().id(randomUUID())
			.address(new ControlUnitAddress(COMMUNICATION_SERVICE_NAME) {
			}).build();
	private static final ControlUnit CONTROL_UNIT_2 = ControlUnit.builder().id(randomUUID())
			.address(new ControlUnitAddress(COMMUNICATION_SERVICE_NAME) {
			}).build();
	private static final Collection<ControlUnit> CONTROL_UNITS = Sets.newHashSet(INTEGRATED_CONTROL_UNIT,
			CONTROL_UNIT_1, CONTROL_UNIT_2);

	// object under test
	@Autowired
	private ControlUnitController controlUnitController;

	@MockBean
	private ControlUnitRepository controlUnitRepository;

	@Test
	public void findAllShouldReturnCollectionOfControlUnits() {
		// given
		given(controlUnitRepository.findAll()).willReturn(CONTROL_UNITS);

		// when
		final ResponseEntity<Collection<ControlUnit>> response = controlUnitController.findAll();

		// then
		assertThat(response.getBody(), containsInAnyOrder(CONTROL_UNIT_1, CONTROL_UNIT_2, INTEGRATED_CONTROL_UNIT));
		assertThat(response.getStatusCode(), is(OK));
	}

	@Test
	public void findAllShouldReturnEmptyCollectionIfNoContronUnitsAreAvailable() {
		// given
		given(controlUnitRepository.findAll()).willReturn(emptySet());

		// when
		final ResponseEntity<Collection<ControlUnit>> response = controlUnitController.findAll();

		// then
		assertThat(response.getBody(), is(empty()));
		assertThat(response.getStatusCode(), is(OK));
	}

	@Test
	public void findByIdShouldReturnControlUnit() {
		// given
		given(controlUnitRepository.findById(CONTROL_UNIT_1.getId())).willReturn(Optional.of(CONTROL_UNIT_1));

		// when
		final ResponseEntity<ControlUnit> response = controlUnitController.findById(CONTROL_UNIT_1.getId());

		// then
		assertThat(response.getBody(), is(CONTROL_UNIT_1));
		assertThat(response.getStatusCode(), is(OK));
	}

	@Test(expected = NoSuchElementException.class)
	public void findByIdShouldReturnNullWithStatusNotFoundIfDevicesDoesNotExists() {
		// given
		given(controlUnitRepository.findById(CONTROL_UNIT_1.getId())).willReturn(Optional.empty());

		// when
		controlUnitController.findById(CONTROL_UNIT_1.getId());

		// then an exception is expected
	}

	@Test
	public void findIntegratedShouldReturnIntegratedControlUnit() {
		// given
		given(controlUnitRepository.findIntegrated()).willReturn(Optional.of(CONTROL_UNIT_1));

		// when
		final ResponseEntity<ControlUnit> response = controlUnitController.findIntegrated();

		// then
		assertThat(response.getBody(), is(CONTROL_UNIT_1));
		assertThat(response.getStatusCode(), is(OK));
	}

	@Test(expected = NoSuchElementException.class)
	public void findIntegratedThrowNoSuchElementExceptionIfIntegratedControlUnitDoesNotExists() {
		// given
		given(controlUnitRepository.findById(CONTROL_UNIT_1.getId())).willReturn(Optional.empty());

		// when
		controlUnitController.findById(CONTROL_UNIT_1.getId());

		// then an exception is expected
	}

	@Test
	public void handleNoSuchElementExceptionShouldReturnNotFoundStatus() {
		// given nothing special

		// when
		final ResponseEntity<String> response = controlUnitController.handleNoSuchElementException();

		// then
		assertThat(response.getStatusCode(), is(NOT_FOUND));
	}
}
