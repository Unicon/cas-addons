package net.unicon.cas.addons.response;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Principal;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * JSON object that represents the json validation response.
 *
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, inc.
 * @since 0.6
 */
public class TicketValidationJsonResponse {
  private String              user               = null;
  private Date                authenticationTime = null;
  private Map<String, Object> attributes         = null;

  public TicketValidationJsonResponse() {
    setAuthenticationTime(new Date());
    setAttributes(new LinkedHashMap<String, Object>());
  }

  public TicketValidationJsonResponse(final Authentication authentication, final Principal principal) {
    setUser(principal.getId());
    setAuthenticationTime(authentication.getAuthenticatedDate());
    setAttributes(principal.getAttributes());
  }

  public TicketValidationJsonResponse(final String user, final Map<String, Object> attributes) {
    setUser(user);
    setAttributes(attributes);
    setAuthenticationTime(new Date());
  }

  @JsonGetter
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @JsonGetter
  public Date getAuthenticationTime() {
    return authenticationTime;
  }

  @JsonGetter
  public String getUser() {
    return user;
  }

  @JsonSetter
  public void setAttributes(final Map<String, Object> attr) {
    attributes = attr;
  }

  @JsonSetter
  public void setAuthenticationTime(final Date authenticationTime) {
    this.authenticationTime = authenticationTime;
  }

  @JsonSetter
  public void setUser(final String user) {
    this.user = user;
  }
}