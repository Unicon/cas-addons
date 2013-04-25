package net.unicon.cas.addons.response;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Principal;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * JSON object that represents the json validation response.
 *
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, inc.
 * @since 0.6
 */
public final class TicketValidationJsonResponse {

    private String user;

    private Date authenticationTime;

    private Map<String, Object> attributes = new HashMap<String, Object>();


    public TicketValidationJsonResponse() {
        setAuthenticationTime(new Date());
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


    public Map<String, Object> getAttributes() {
        return this.attributes;
    }


    public Date getAuthenticationTime() {
        return this.authenticationTime;
    }


    public String getUser() {
        return this.user;
    }

    @JsonSetter
    public void setAttributes(final Map<String, Object> attr) {
        this.attributes = attr;
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