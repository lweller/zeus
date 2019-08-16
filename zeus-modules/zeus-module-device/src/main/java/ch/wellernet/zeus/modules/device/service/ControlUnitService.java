package ch.wellernet.zeus.modules.device.service;

import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.repository.ControlUnitRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static javax.transaction.Transactional.TxType.MANDATORY;

@Service
@Transactional(value = MANDATORY)
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ControlUnitService {

  // injected dependencies
  private final ControlUnitRepository controlUnitRepository;

  public Collection<ControlUnit> findAll() {
    return newArrayList(controlUnitRepository.findAll());
  }

  public ControlUnit findById(final UUID controlUnitId) {
    return controlUnitRepository.findById(controlUnitId).orElseThrow(NoSuchElementException::new);
  }

  public void delete(@NonNull final UUID controlUnitId) {
    if (!controlUnitRepository.existsById(controlUnitId)) {
      throw new NoSuchElementException(format("controlUnit with ID %s does not exists", controlUnitId));
    }
    controlUnitRepository.deleteById(controlUnitId);
  }
}
