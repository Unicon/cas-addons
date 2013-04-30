package net.unicon.cas.addons.response;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonViewResponseTests {

    @Test
    public void testJsonObjectMapper() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        attributes.put("name", "value");
        final String userId = "userId";

        TicketValidationJsonResponse response = new TicketValidationJsonResponse(userId, attributes);
        final String jsonResult = mapper.writeValueAsString(response);

        assertNotNull(jsonResult);
        response = mapper.readValue(jsonResult, TicketValidationJsonResponse.class);
        assertNotNull(response);
        assertEquals(response.getUser(), userId);
        assertEquals(response.getAttributes(), attributes);
    }
}
