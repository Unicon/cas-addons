package net.unicon.cas.addons.info.internal;

import net.unicon.cas.addons.info.SingleSignOnSessionsReport;
import net.unicon.cas.addons.support.ThreadSafe;
import net.unicon.cas.addons.ticket.BulkRetrievalOfTicketsNotSupportedException;
import net.unicon.cas.addons.ticket.TicketSupport;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Default implementation of <code>SingleSignOnSessionReport</code>
 * <p/>
 * Uses CAS' <code>TicketSupport</code> API to retrieve <code>TicketGrantingTicket</code>s
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.0.3
 */
@ThreadSafe
@Component
public class DefaultSingleSignOnSessionsReport implements SingleSignOnSessionsReport {

	private final TicketSupport ticketSupport;

	@Autowired
	public DefaultSingleSignOnSessionsReport(TicketSupport ticketSupport) {
		this.ticketSupport = ticketSupport;
	}

	@Override
	public Collection<Map<String, Object>> getActiveSsoSessions() throws BulkRetrievalOfTicketsNotSupportedException {
		final List<Map<String, Object>> activeSessions = new ArrayList<Map<String, Object>>();

		for(TicketGrantingTicket tgt : this.ticketSupport.getNonExpiredTicketGrantingTickets()) {
			final Map<String, Object> sso = new HashMap<String, Object>(3);
			sso.put(SsoSessionAttributeKeys.AUTHENTICATED_PRINCIPAL.toString(), tgt.getAuthentication().getPrincipal().getId());
			sso.put(SsoSessionAttributeKeys.AUTHENTICATION_DATE.toString(), tgt.getAuthentication().getAuthenticatedDate());
			sso.put(SsoSessionAttributeKeys.NUMBER_OF_USES.toString(), tgt.getCountOfUses());
			activeSessions.add(Collections.unmodifiableMap(sso));
		}
		return Collections.unmodifiableCollection(activeSessions);
	}
}
