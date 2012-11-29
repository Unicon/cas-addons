package net.unicon.cas.addons.info.events;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.validation.Assertion;

/**
 * Concrete subclass of <code>AbstractCasServiceAccessEvent</code> representing validation of a service ticket by a CAS server.
 * <p/>
 * This subclass adds {@link Assertion} that is associated with this event to the encapsulated data.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.1
 */
public final class CasServiceTicketValidatedEvent extends AbstractCasServiceAccessEvent {

	private final Assertion assertion;

	public CasServiceTicketValidatedEvent(Object source, String serviceTicketId, Service service, Assertion assertion) {
		super(source, serviceTicketId, service);
		this.assertion = assertion;
	}

	public Assertion getAssertion() {
		return assertion;
	}

	@Override
	public String toString() {
		return super.toString() + " -- {assertion=" + assertion + "}";
	}
}
