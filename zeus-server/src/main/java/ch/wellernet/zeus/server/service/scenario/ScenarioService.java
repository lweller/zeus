package ch.wellernet.zeus.server.service.scenario;

import ch.wellernet.zeus.server.model.scenario.InhibitionArc;
import ch.wellernet.zeus.server.model.scenario.InputArc;
import ch.wellernet.zeus.server.model.scenario.OutputArc;
import ch.wellernet.zeus.server.model.scenario.Place;
import ch.wellernet.zeus.server.model.scenario.Transition;

public class ScenarioService {

	public boolean canFireInhibitionArc(final InhibitionArc inhibitionArc) {
		return inhibitionArc.getPlace() != null && inhibitionArc.getPlace().getCount() < inhibitionArc.getWeight();
	}

	public boolean canFireInputArc(final InputArc inputArc) {
		return inputArc.getPlace() != null && inputArc.getPlace().getCount() >= inputArc.getWeight();
	}

	public boolean canFireOutputArc(final OutputArc outputArc) {
		return outputArc.getPlace() != null
				&& outputArc.getPlace().getCount() + outputArc.getWeight() <= outputArc.getPlace().getMaxCount();
	}

	public boolean canFireTransition(final Transition transition) {
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

	public void fireTransition(final Transition transition) {
		if (canFireTransition(transition)) {
			for (final InputArc inputArc : transition.getInputArcs()) {
				final Place place = inputArc.getPlace();
				place.setCount(place.getCount() - inputArc.getWeight());
			}
			for (final OutputArc outputArc : transition.getOutputArcs()) {
				final Place place = outputArc.getPlace();
				place.setCount(place.getCount() + outputArc.getWeight());
				for (final InputArc inputArc : place.getInputArcs()) {
					fireTransition(inputArc.getTransition());
				}
			}
		}
	}
}
