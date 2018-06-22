package ch.wellernet.zeus.modules.device.model;

import javax.persistence.Entity;

import ch.wellernet.zeus.modules.device.service.communication.tcp.TcpCommunicationService;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class TcpControlUnitAddress extends ControlUnitAddress {
	private String host;
	private int port;

	private TcpControlUnitAddress() {
		super(TcpCommunicationService.NAME);
	}

	@Builder
	private TcpControlUnitAddress(final String communicationServiceName, final String host, final int port) {
		this();
		this.host = host;
		this.port = port;
	}
}
