package net.unicon.cas.addons.authentication.internal;

import net.unicon.cas.addons.authentication.AuthenticationSupport;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;

import java.util.Map;

/**
 * Default implementation of <code>AuthenticationSupport</code>.
 * <p/>
 * Uses CAS' <code>TicketRegistry</code> to retrieve TGT and its associated objects by provided tgt String token
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.1
 */
public class DefaultAuthenticationSupport implements AuthenticationSupport {

    private TicketRegistry ticketRegistry;

    public DefaultAuthenticationSupport(TicketRegistry ticketRegistry) {
        this.ticketRegistry = ticketRegistry;
    }

    @Override
    /** {@inheritDoc} */
    public Authentication getAuthenticationFrom(String ticketGrantingTicketId) throws RuntimeException {
        TicketGrantingTicket tgt = (TicketGrantingTicket) this.ticketRegistry.getTicket(ticketGrantingTicketId, TicketGrantingTicket.class);
        return tgt == null ? null : tgt.getAuthentication();
    }

    @Override
    /** {@inheritDoc} */
    public Principal getAuthenticatedPrincipalFrom(String ticketGrantingTicketId) throws RuntimeException {
        Authentication auth = getAuthenticationFrom(ticketGrantingTicketId);
        return auth == null ? null : auth.getPrincipal();
    }

    @Override
    /** {@inheritDoc} */
    public Map<String, Object> getPrincipalAttributesFrom(String ticketGrantingTicketId) throws RuntimeException {
        Principal principal = getAuthenticatedPrincipalFrom(ticketGrantingTicketId);
        return principal == null ? null : principal.getAttributes();
    }
}
