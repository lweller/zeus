package ch.wellernet.zeus.modules.device.controller;

import ch.wellernet.zeus.modules.device.controller.dto.ControlUnitDto;
import ch.wellernet.zeus.modules.device.controller.mapper.ControlUnitMapper;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.repository.ControlUnitRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(classes = ControlUnitController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class ControlUnitControllerTest {
  // object under test
  @Autowired
  private ControlUnitController controlUnitController;

  @MockBean
  private ControlUnitRepository controlUnitRepository;

  @MockBean
  private ControlUnitMapper controlUnitMapper;

  @Test
  public void findAllShouldReturnCollectionOfControlUnits() {
    // given
    final ControlUnit controlUnit1 = ControlUnit.builder().id(randomUUID()).build();
    final ControlUnit controlUnit2 = ControlUnit.builder().id(randomUUID()).build();
    final Collection<ControlUnit> controlUnits = Stream.of(controlUnit1, controlUnit2).collect(toSet());
    final ControlUnitDto controlUnitDto1 = ControlUnitDto.builder().id(randomUUID()).build();
    final ControlUnitDto controlUnitDto2 = ControlUnitDto.builder().id(randomUUID()).build();
    final Collection<ControlUnitDto> controlUnitDtos = Stream.of(controlUnitDto1, controlUnitDto2).collect(toSet());
    given(controlUnitRepository.findAll()).willReturn(controlUnits);
    given(controlUnitMapper.toDtos(controlUnits)).willReturn(controlUnitDtos);

    // when
    final ResponseEntity<Collection<ControlUnitDto>> response = controlUnitController.findAll();

    // then
    assertThat(response.getBody(), containsInAnyOrder(controlUnitDto1, controlUnitDto2));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findAllShouldReturnEmptyCollectionIfNoControlUnitsAreAvailable() {
    // given
    given(controlUnitRepository.findAll()).willReturn(emptySet());

    // when
    final ResponseEntity<Collection<ControlUnitDto>> response = controlUnitController.findAll();

    // then
    assertThat(response.getBody(), is(empty()));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findByIdShouldReturnControlUnit() {
    // given
    final UUID controlUnitId = randomUUID();
    final ControlUnit controlUnit = ControlUnit.builder().id(controlUnitId).build();
    final ControlUnitDto controlUnitDto = ControlUnitDto.builder().id(controlUnitId).build();
    given(controlUnitRepository.findById(controlUnitId)).willReturn(Optional.of(controlUnit));
    given(controlUnitMapper.toDto(controlUnit)).willReturn(controlUnitDto);

    // when
    final ResponseEntity<ControlUnitDto> response = controlUnitController.findById(controlUnitId);

    // then
    assertThat(response.getBody(), is(controlUnitDto));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void findByIdShouldReturnNullWithStatusNotFoundIfDevicesDoesNotExists() {
    // given
    final UUID controlUnitId = randomUUID();
    given(controlUnitRepository.findById(controlUnitId)).willReturn(Optional.empty());

    // when
    controlUnitController.findById(controlUnitId);

    // then an exception is expected
  }

  @Test
  public void findIntegratedShouldReturnIntegratedControlUnit() {
    // given
    final UUID controlUnitId = randomUUID();
    final ControlUnit controlUnit = ControlUnit.builder().id(controlUnitId).build();
    final ControlUnitDto controlUnitDto = ControlUnitDto.builder().id(controlUnitId).build();
    given(controlUnitRepository.findIntegrated()).willReturn(Optional.of(controlUnit));
    given(controlUnitMapper.toDto(controlUnit)).willReturn(controlUnitDto);

    // when
    final ResponseEntity<ControlUnitDto> response = controlUnitController.findIntegrated();

    // then
    assertThat(response.getBody(), is(controlUnitDto));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void findIntegratedThrowNoSuchElementExceptionIfIntegratedControlUnitDoesNotExists() {
    // given
    final UUID controlUnitId = randomUUID();
    given(controlUnitRepository.findById(controlUnitId)).willReturn(Optional.empty());

    // when
    controlUnitController.findById(controlUnitId);

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
