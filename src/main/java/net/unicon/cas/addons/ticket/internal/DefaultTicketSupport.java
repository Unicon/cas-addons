package net.unicon.cas.addons.ticket.internal;

import net.unicon.cas.addons.support.ThreadSafe;
import net.unicon.cas.addons.ticket.BulkRetrievalOfTicketsNotSupportedException;
import net.unicon.cas.addons.ticket.TicketSupport;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
@Component
public class DefaultTicketSupport implements TicketSupport {

	private final TicketRegistry ticketRegistry;

	@Autowired
	public DefaultTicketSupport(TicketRegistry ticketRegistry) {
		this.ticketRegistry = ticketRegistry;
	}

	@Override
	public boolean ticketGrantingTicketExistsAndExpired(String ticketGrantingTicketId) {
		final Ticket ticket = this.ticketRegistry.getTicket(ticketGrantingTicketId);
		if (ticket != null && ticket instanceof TicketGrantingTicket) {
			return TicketGrantingTicket.class.cast(ticket).isExpired();
		}
		return false;
	}

	@Override
	public void deleteExpiredTicketGrantingTicket(String ticketGrantingTicketId) {
		if (ticketGrantingTicketExistsAndExpired(ticketGrantingTicketId)) {
			this.ticketRegistry.deleteTicket(ticketGrantingTicketId);
		}
	}

	@Override
	public List<TicketGrantingTicket> getNonExpiredTicketGrantingTickets() throws BulkRetrievalOfTicketsNotSupportedException {
		final List<TicketGrantingTicket> tgts = new ArrayList<TicketGrantingTicket>();

		try {
			for (Ticket ticket : this.ticketRegistry.getTickets()) {
				if ((ticket instanceof TicketGrantingTicket) && !ticket.isExpired()) {
					tgts.add((TicketGrantingTicket) ticket);
				}
			}
		}
		catch (UnsupportedOperationException e) {
			throw new BulkRetrievalOfTicketsNotSupportedException("The underlying implementation of <TicketRegistry> does not support a bulk retrieval of tickets", e);
		}
		return tgts;
	}
}
