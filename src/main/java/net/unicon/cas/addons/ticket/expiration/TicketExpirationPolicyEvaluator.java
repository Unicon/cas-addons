package net.unicon.cas.addons.ticket.expiration;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.ticket.TicketState;

/**
 * Defines the set of operations that determine whether the incoming request satisfies a set of
 * implemented rules via {@link #doesSatisfyTicketExpirationPolicy(HttpServletRequest, TicketState)},
 * such that if it does, the expiration policy of the {@link TicketState} may be handled
 * differently by the caller.
 * 
 * @author Misagh Moayyed
 * @since 1.9
 * @see IpAddressBasedExpirationPolicyEvaluator
 */
public interface TicketExpirationPolicyEvaluator {
    boolean doesSatisfyTicketExpirationPolicy(final HttpServletRequest request, final TicketState state);      
}
