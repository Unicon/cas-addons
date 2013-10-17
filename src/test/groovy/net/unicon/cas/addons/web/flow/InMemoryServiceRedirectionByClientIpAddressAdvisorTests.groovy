package net.unicon.cas.addons.web.flow

import javax.servlet.http.HttpServletRequest;

import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;

import org.jasig.cas.ticket.TicketState;
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import org.springframework.core.io.FileSystemResource
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;

import spock.lang.Specification
/**
 * @author @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
@RunWith(Sputnik)
class InMemoryServiceRedirectionByClientIpAddressAdvisorTests extends Specification {
    def "Service does require a redirection"() {
        given:
    
          def evaluator = new InMemoryServiceRedirectionByClientIpAddressAdvisor()
          
          def context = Mock(RequestContext)
          def request = Mock(HttpServletRequest)
          
          request.getRemoteAddr() >> "192.168.1.2"
          request.getRemotePort >> 0
          
          context.getExternalContext() >> Mock(ExternalContext)
          context.getExternalContext().getNativeRequest() >> request
                    
          def svc = Mock(RegisteredServiceWithAttributes)
          svc.getServiceId() >> "http://www.google.com"
          
          def redirectUrl = "http://www.yahoo.com"
          
        when: 
          def doesIt = evaluator.shouldRedirectServiceRequest(context, svc, redirectUrl)
          
        then:
          doesIt
    }
     
    def "Service does not require a redirection"() {
        given:
    
          def evaluator = new InMemoryServiceRedirectionByClientIpAddressAdvisor()
          
          def context = Mock(RequestContext)
          def request = Mock(HttpServletRequest)
          
          request.getRemoteAddr() >> "192.168.1.2"
          request.getRemotePort >> 0
          
          context.getExternalContext() >> Mock(ExternalContext)
          context.getExternalContext().getNativeRequest() >> request
                    
          def svc = Mock(RegisteredServiceWithAttributes)
          svc.getServiceId() >> "http://www.google.com"
          
          def redirectUrl = "http://www.yahoo.com"
          
        when:
          def doesIt = evaluator.shouldRedirectServiceRequest(context, svc, redirectUrl) &&
                       evaluator.shouldRedirectServiceRequest(context, svc, redirectUrl)
          
        then:
          !doesIt
    }
    
}


