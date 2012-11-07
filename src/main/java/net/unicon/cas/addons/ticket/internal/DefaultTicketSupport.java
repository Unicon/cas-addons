package net.unicon.cas.addons.ticket.internal;

import net.unicon.cas.addons.support.ThreadSafe;
import net.unicon.cas.addons.ticket.TicketSupport;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;

/**
 * Default implementation of <code>TicketSupport</code>
 * <p/>
 * Uses CAS' <code>TicketRegistry</code> to retrieve TGT and its associated objects by provided tgt String token
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.0.3
 */
@ThreadSafe
public class DefaultTicketSupport implements TicketSupport {

	private final TicketRegistry ticketRegistry;

	public DefaultTicketSupport(TicketRegistry ticketRegistry) {
		this.ticketRegistry = ticketRegistry;
	}

	@Override
	public boolean ticketGrantingTicketExistsAndExpired(String ticketGrantingTicketId) {
		Ticket ticket = this.ticketRegistry.getTicket(ticketGrantingTicketId);
		if (ticket != null && ticket instanceof TicketGrantingTicket) {
			return TicketGrantingTicket.class.cast(ticket).isExpired();
		}
		return false;
	}

	@Override
	public void deleteExpiredTicketGrantingTicket(String ticketGrantingTicketId) {
		if(ticketGrantingTicketExistsAndExpired(ticketGrantingTicketId)) {
			this.ticketRegistry.deleteTicket(ticketGrantingTicketId);
		}
	}
}
