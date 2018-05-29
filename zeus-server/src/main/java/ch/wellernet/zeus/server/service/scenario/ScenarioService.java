package ch.wellernet.zeus.server.service.scenario;

import static javax.transaction.Transactional.TxType.REQUIRED;

import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.wellernet.zeus.server.model.scenario.InhibitionArc;
import ch.wellernet.zeus.server.model.scenario.InputArc;
import ch.wellernet.zeus.server.model.scenario.OutputArc;
import ch.wellernet.zeus.server.model.scenario.Place;
import ch.wellernet.zeus.server.model.scenario.Transition;
import ch.wellernet.zeus.server.repository.PlaceRepository;
import ch.wellernet.zeus.server.repository.TransitionRepository;

@Service
public class ScenarioService {

	private @Autowired TransitionRepository transitionRepository;
	private @Autowired PlaceRepository placeRepository;

	@Transactional(REQUIRED)
	public void fireTransition(final int transitionId) throws NoSuchElementException {
		fireTransition(transitionRepository.findById(transitionId).get());
	}

	boolean canFireInhibitionArc(final InhibitionArc inhibitionArc) {
		return inhibitionArc.getPlace() != null && inhibitionArc.getPlace().getCount() < inhibitionArc.getWeight();
	}

	boolean canFireInputArc(final InputArc inputArc) {
		return inputArc.getPlace() != null && inputArc.getPlace().getCount() >= inputArc.getWeight();
	}

	boolean canFireOutputArc(final OutputArc outputArc) {
		return outputArc.getPlace() != null
				&& outputArc.getPlace().getCount() + outputArc.getWeight() <= outputArc.getPlace().getMaxCount();
	}

	boolean canFireTransition(final Transition transition) {
		for (final InputArc inputArc : transition.getInputArcs()) {
			if (!canFireInputArc(inputArc)) {
				return false;
			}
		}

		for (final OutputArc outputArc : transition.getOutputArcs()) {
			if (!canFireOutputArc(outputArc)) {
				return false;
			}
		}
		for (final InhibitionArc inhibitionArc : transition.getInhititionArcs()) {
			if (!canFireInhibitionArc(inhibitionArc)) {
				return false;
			}
		}

		return true;
	}

	void fireTransition(final Transition transition) {
		if (canFireTransition(transition)) {
			for (final InputArc inputArc : transition.getInputArcs()) {
				final Place place = inputArc.getPlace();
				place.setCount(place.getCount() - inputArc.getWeight());
				placeRepository.save(place);
			}
			for (final OutputArc outputArc : transition.getOutputArcs()) {
				final Place place = outputArc.getPlace();
				place.setCount(place.getCount() + outputArc.getWeight());
				placeRepository.save(place);
				for (final InputArc inputArc : place.getInputArcs()) {
					fireTransition(inputArc.getTransition());
				}
			}
		}
	}
}
