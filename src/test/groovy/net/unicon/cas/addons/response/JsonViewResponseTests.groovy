package net.unicon.cas.addons.response

import org.junit.runner.RunWith;
import org.spockframework.runtime.Sputnik;

import spock.lang.Specification

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * @author @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
@RunWith(Sputnik)
class JsonViewResponseTests extends Specification {

    def "Response contains JSON validation data"() {
        given:
        def mapper = new ObjectMapper()
        def attributes = [name:"value"]
        def userId = "userId"
        
        when:
        def jsonResult  = mapper.writeValueAsString(new TicketValidationJsonResponse(userId, attributes))
        def response = mapper.readValue(jsonResult, TicketValidationJsonResponse.class)
        
        then:
        response.getUser() == userId
        
        and:
        response.getAttributes() == attributes
    }
}
