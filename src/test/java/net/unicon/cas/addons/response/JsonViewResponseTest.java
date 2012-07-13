package net.unicon.cas.addons.response;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonViewResponseTest {

  @Test
  public void testJsonObjectMapper() throws IOException {
    final ObjectMapper mapper = new ObjectMapper();

    final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
    attributes.put("name", "value");

    final String userId = "userId";

    TicketValidationJsonResponse response = new TicketValidationJsonResponse(userId, attributes);
    final String jsonResult = mapper.writeValueAsString(response);

    Assert.assertNotNull(jsonResult);

    response = mapper.readValue(jsonResult, TicketValidationJsonResponse.class);
    Assert.assertNotNull(response);

    Assert.assertEquals(response.getUser(), userId);
    Assert.assertEquals(response.getAttributes(), attributes);
  }

}
