package net.unicon.cas.addons.client.validation;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import net.unicon.cas.addons.response.TicketValidationJsonResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.junit.Assert;
import org.junit.Test;

public class Cas20ServiceTicketJsonValidatorTest {

    @Test
    public void testJsonResponse() throws TicketValidationException, IOException {
        final Cas20ServiceTicketJsonValidator validator = new Cas20ServiceTicketJsonValidator("https://cas.server.edu");

        final ObjectMapper mapper = new ObjectMapper();

        final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        attributes.put("name", "value");

        final String userId = "userId";

        final TicketValidationJsonResponse response = new TicketValidationJsonResponse(userId, attributes);

        final String jsonResult = mapper.writeValueAsString(response);

        final Assertion assertion = validator.parseJsonResponseFromServer(jsonResult);
        Assert.assertNotNull(assertion);

        Assert.assertEquals(userId, assertion.getPrincipal().getName());
        Assert.assertEquals(attributes, assertion.getAttributes());
        Assert.assertEquals(attributes, assertion.getPrincipal().getAttributes());

    }

}
