package ch.wellernet.zeus.modules.device.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(of = "id")
@JsonDeserialize(builder = Reference.ReferenceBuilder.class)
public class Reference {
  private final UUID id;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ReferenceBuilder {
  }
}

