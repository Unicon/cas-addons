package net.unicon.cas.addons.authentication.internal

import org.jasig.cas.authentication.principal.Principal
import org.jasig.cas.authentication.Authentication
import org.jasig.cas.ticket.TicketGrantingTicket
import org.jasig.cas.ticket.registry.TicketRegistry
import spock.lang.Specification


/**
 * Spock-based tests for ${link DefaultAuthenticationSupport}
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
class DefaultAuthenticationSupportTests extends Specification {

    def "testing correctness of DefaultAuthenticationSupport implementation"() {
        given: 'All the dependencies for DefaultAuthenticationSupport are set up as mocks i.e. TicketRegistry, TGT, Authentication, and Principal with attributes'
        Principal principal = Mock()
        principal.attributes >> [attr1: 'val1', attr2: 'val2']

        Authentication authentication = Mock()
        authentication.principal >> principal

        TicketGrantingTicket tgt = Mock()
        tgt.authentication >> authentication

        TicketRegistry ticketRegistry = Mock()
        ticketRegistry.getTicket('test-tgt', TicketGrantingTicket) >> tgt

        expect: 'DefaultAuthenticationSupport is correctly implemented, that is peeling TGT onion returned by TicketRegistry for a øgiven TGT token'
        DefaultAuthenticationSupport authenticationSupportUnderTest = new DefaultAuthenticationSupport(ticketRegistry)
        //Spock's power asserts
        authenticationSupportUnderTest.getAuthenticationFrom('test-tgt') == authentication
        authenticationSupportUnderTest.getAuthenticatedPrincipalFrom('test-tgt') == principal
        authenticationSupportUnderTest.getPrincipalAttributesFrom('test-tgt') == [attr1: 'val1', attr2: 'val2']
    }
}