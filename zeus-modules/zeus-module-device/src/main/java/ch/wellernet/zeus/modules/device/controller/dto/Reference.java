package ch.wellernet.zeus.modules.device.controller.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(of = "id")
public class Reference {
  private final UUID id;
}

