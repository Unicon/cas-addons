package net.unicon.cas.addons.info.events;

import org.jasig.cas.authentication.principal.Service;
import org.springframework.context.ApplicationEvent;

/**
 * Base Spring <code>ApplicationEvent</code> representing a service access action executed within running CAS server.
 * <p/>
 * This event encapsulates {@link Service} and <i>serviceTicketId</i> that are associated with an abstract service access action executed in a CAS server.
 * <p/>
 * More concrete events are expected to subclass this abstract type.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.1
 */
public class AbstractCasServiceAccessEvent extends ApplicationEvent {

	private final String serviceTicketId;

	private final Service service;

	public AbstractCasServiceAccessEvent(Object source, String serviceTicketId, Service service) {
		super(source);
		this.serviceTicketId = serviceTicketId;
		this.service = service;
	}

	public String getServiceTicketId() {
		return serviceTicketId;
	}

	public Service getService() {
		return service;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{" +
				"serviceTicketId='" + serviceTicketId + '\'' +
				", service=" + service +
				'}';
	}
}
