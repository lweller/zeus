package ch.wellernet.zeus.modules.scenario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@Inheritance(strategy = SINGLE_TABLE)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CronEvent.class),
    @JsonSubTypes.Type(value = DayTimeEvent.class),
    @JsonSubTypes.Type(value = FixedRateEvent.class)
})
@JGlobalMap(excluded = {"id", "version", "lastExecution", "transitions"})
public abstract class Event {
  @Id
  @NotNull
  @Setter(PRIVATE)
  private UUID id;

  @Version
  private long version;

  @NotNull
  private String name;

  @JsonProperty(access = READ_ONLY)
  private Date lastExecution;

  @OneToMany(cascade = {DETACH, REFRESH}, fetch = LAZY, mappedBy = "event")
  @JsonIgnore
  private Set<EventDrivenTransition> transitions = new HashSet<>();

  @Transient
  private Date nextScheduledExecution;

  Event(@NonNull final UUID id,
        @Nullable final String name,
        @Nullable final Set<EventDrivenTransition> transitions) {
    this.id = id;
    this.name = ofNullable(name).orElse(format("New Event (%s)", id));
    ofNullable(transitions).orElse(emptySet()).forEach(transition -> {
      this.transitions.add(transition);
      transition.setEvent(this);
    });
  }

  public abstract <T> T dispatch(Dispatcher<T> dispatcher);

  public interface Dispatcher<T> {
    default T execute(final CronEvent event) {
      return null;
    }

    default T execute(final DayTimeEvent event) {
      return null;
    }

    default T execute(final FixedRateEvent event) {
      return null;
    }
  }
}
