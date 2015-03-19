package net.unicon.cas.addons.info.events;

import org.jasig.cas.authentication.Authentication;

/**
 * Concrete subclass of <code>AbstractCasSsoEvent</code> representing single sign on session destruction event e.g. user logged out
 * and <i>TicketGrantingTicket</i> has been destroyed by a CAS server.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.1
 */
public final class CasSsoSessionDestroyedEvent extends AbstractCasSsoEvent {

	public CasSsoSessionDestroyedEvent(Object source, Authentication authentication, String ticketGrantingTicketId) {
		super(source, authentication, ticketGrantingTicketId);
	}
}
