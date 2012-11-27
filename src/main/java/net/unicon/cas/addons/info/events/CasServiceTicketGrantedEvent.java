package net.unicon.cas.addons.info.events;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;

/**
 * Concrete subclass of <code>AbstractCasServiceAccessEvent</code> representing granting of a service ticket by a CAS server.
 * <p/>
 * This subclass adds {@link Authentication} that is associated with this event to the encapsulated data.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.1
 */
public final class CasServiceTicketGrantedEvent extends AbstractCasServiceAccessEvent {

	private final Authentication authentication;

	public CasServiceTicketGrantedEvent(Object source, String serviceTicketId, Service service, Authentication authentication) {
		super(source, serviceTicketId, service);
		this.authentication = authentication;
	}

	@Override
	public String toString() {
		return super.toString() + " -- {authentication=" + authentication + "}";
	}
}
