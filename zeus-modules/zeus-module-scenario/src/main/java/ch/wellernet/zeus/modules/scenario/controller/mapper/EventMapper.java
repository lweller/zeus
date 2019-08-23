package ch.wellernet.zeus.modules.scenario.controller.mapper;

import ch.wellernet.zeus.modules.scenario.controller.dto.CronEventDto;
import ch.wellernet.zeus.modules.scenario.controller.dto.DayTimeEventDto;
import ch.wellernet.zeus.modules.scenario.controller.dto.EventDto;
import ch.wellernet.zeus.modules.scenario.controller.dto.FixedRateEventDto;
import ch.wellernet.zeus.modules.scenario.model.CronEvent;
import ch.wellernet.zeus.modules.scenario.model.DayTimeEvent;
import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.model.FixedRateEvent;
import ch.wellernet.zeus.modules.scenario.repository.CronEventRepository;
import ch.wellernet.zeus.modules.scenario.repository.DayTimeEventRepository;
import ch.wellernet.zeus.modules.scenario.repository.FixedRateEventRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Optional;

@Mapper(componentModel = "spring")
abstract class EventMapper {

  @Autowired
  private CronEventRepository cronEventRepository;

  @Autowired
  private DayTimeEventRepository dayTimeEventRepository;

  @Autowired
  private FixedRateEventRepository fixedRateEventRepository;

  EventDto toDto(final Event event) {
    return Optional
               .ofNullable(event)
               .map(obj -> obj.dispatch(new Event.Dispatcher<EventDto>() {
                 @Override
                 public EventDto execute(final CronEvent event) {
                   return toDto(event);
                 }

                 @Override
                 public EventDto execute(final DayTimeEvent event) {
                   return toDto(event);
                 }

                 @Override
                 public EventDto execute(final FixedRateEvent event) {
                   return toDto(event);
                 }
               }))
               .orElse(null);
  }

  abstract CronEventDto toDto(CronEvent event);

  abstract DayTimeEventDto toDto(DayTimeEvent event);

  abstract FixedRateEventDto toDto(FixedRateEvent event);

  public abstract Collection<EventDto> toDtos(Collection<Event> events);

  Event createOrUpdateFrom(final EventDto eventDto) {
    return Optional
               .ofNullable(eventDto)
               .map(dto -> dto.dispatch(new EventDto.Dispatcher<Event>() {
                 @Override
                 public Event execute(final CronEventDto eventDto) {
                   return createOrUpdateFrom(eventDto);
                 }

                 @Override
                 public Event execute(final DayTimeEventDto eventDto) {
                   return createOrUpdateFrom(eventDto);
                 }

                 @Override
                 public Event execute(final FixedRateEventDto eventDto) {
                   return createOrUpdateFrom(eventDto);
                 }
               }))
               .orElse(null);
  }

  private CronEvent createOrUpdateFrom(final CronEventDto eventDto) {
    return cronEventRepository
               .findById(eventDto.getId())
               .map(event -> copy(eventDto, event))
               .orElseGet(() -> cronEventRepository.save(createFrom(eventDto)));
  }

  @Mapping(target = "transitions", ignore = true)
  abstract CronEvent createFrom(CronEventDto eventDto);

  @Mapping(target = "transitions", ignore = true)
  @Mapping(target = "lastExecution", ignore = true)
  @Mapping(target = "nextScheduledExecution", ignore = true)
  abstract CronEvent copy(CronEventDto eventDto, @MappingTarget CronEvent event);

  private DayTimeEvent createOrUpdateFrom(final DayTimeEventDto eventDto) {
    return dayTimeEventRepository
               .findById(eventDto.getId())
               .map(event -> copy(eventDto, event))
               .orElseGet(() -> dayTimeEventRepository.save(createFrom(eventDto)));
  }

  @Mapping(target = "transitions", ignore = true)
  abstract DayTimeEvent createFrom(DayTimeEventDto eventDto);

  @Mapping(target = "transitions", ignore = true)
  @Mapping(target = "lastExecution", ignore = true)
  @Mapping(target = "nextScheduledExecution", ignore = true)
  abstract DayTimeEvent copy(DayTimeEventDto eventDto, @MappingTarget DayTimeEvent event);

  private FixedRateEvent createOrUpdateFrom(final FixedRateEventDto eventDto) {
    return fixedRateEventRepository
               .findById(eventDto.getId())
               .map(event -> copy(eventDto, event))
               .orElseGet(() -> fixedRateEventRepository.save(createFrom(eventDto)));
  }

  @Mapping(target = "transitions", ignore = true)
  abstract FixedRateEvent createFrom(FixedRateEventDto eventDto);

  @Mapping(target = "transitions", ignore = true)
  @Mapping(target = "lastExecution", ignore = true)
  @Mapping(target = "nextScheduledExecution", ignore = true)
  abstract FixedRateEvent copy(FixedRateEventDto eventDto, @MappingTarget FixedRateEvent event);
}