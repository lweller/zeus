package ch.wellernet.zeus.server.model;

import static ch.wellernet.zeus.server.model.ControlUnitAddress.SEQUENCE_NAME;
import static javax.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PRIVATE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@SequenceGenerator(name = SEQUENCE_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode(of = "communicationServiceName")
public abstract class ControlUnitAddress {
	protected static final String SEQUENCE_NAME = "SEQ_CONTROL_UNIT_ADDRESS";

	private @Id @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME) @Setter(PRIVATE) int id;
	private String communicationServiceName;

	public ControlUnitAddress(String communicationServiceName) {
		this.communicationServiceName = communicationServiceName;
	}
}
