package net.unicon.cas.addons.ticket.expiration

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.ticket.TicketState;
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import org.springframework.core.io.FileSystemResource

import spock.lang.Specification
/**
 * @author @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
@RunWith(Sputnik)
class IpAddressBasedExpirationPolicyEvaluatorTests extends Specification {
    def "Remote IPv4 IP address matches the IP pattern specified"() {
        given:
    
          def evaluator = new IpAddressBasedExpirationPolicyEvaluator("^192.+")
          
          def request = Mock(HttpServletRequest)
          request.getRemoteAddr() >> "192.168.1.2"
          
          def state = Mock(TicketState)
          
        when: 
          def doesIt = evaluator.doesSatisfyTicketExpirationPolicy(request, state)
          
        then:
          doesIt
    }
    
    def "Remote IPv6 IP address matches the IP pattern specified"() {
        given:
    
          def evaluator = new IpAddressBasedExpirationPolicyEvaluator("9468%11\$")
          
          def request = Mock(HttpServletRequest)
          request.getRemoteAddr() >> "fe80::919f:17b9:6401:9468%11"
          
          def state = Mock(TicketState)
          
        when:
          def doesIt = evaluator.doesSatisfyTicketExpirationPolicy(request, state)
          
        then:
          doesIt
    }
}


