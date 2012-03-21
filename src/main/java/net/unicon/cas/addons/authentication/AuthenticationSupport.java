package net.unicon.cas.addons.authentication;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Principal;

import java.util.Map;

/**
 * Helper strategy API to ease retrieving CAS' <code>Authentication</code> object and its associated components
 * from available CAS SSO String token called <i>Ticket Granting Ticket (TGT)</i>
 *
 * <p>Note: this API is only intended to be called by CAS server code e.g. any custom CAS server overlay extension, etc.</p>
 *
 * <p>Concurrency semantics: implementations must be thread safe.</p>
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 *
 * @since 0.1
 */
public interface AuthenticationSupport {
    
    Authentication getAuthenticationFrom(String ticketGrantingTicketId) throws RuntimeException;

    Principal getAuthenticatedPrincipalFrom(String ticketGrantingTicketId) throws RuntimeException;

    Map<String, Object> getPrincipalAttributesFrom(String ticketGrantingTicketId) throws RuntimeException;
}
