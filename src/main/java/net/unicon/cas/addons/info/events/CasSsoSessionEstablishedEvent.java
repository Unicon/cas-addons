package net.unicon.cas.addons.info.events;

import org.jasig.cas.authentication.Authentication;

/**
 * Concrete subclass of <code>AbstractCasSsoEvent</code> representing single sign on session establishment event e.g. user logged in
 * and <i>TicketGrantingTicket</i> has been vended by a CAS server.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.1
 */
public final class CasSsoSessionEstablishedEvent extends AbstractCasSsoEvent {

	public CasSsoSessionEstablishedEvent(Object source, Authentication authentication) {
		super(source, authentication);
	}
}
