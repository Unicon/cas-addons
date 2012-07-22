package net.unicon.cas.addons.authentication.handler;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.shiro.codec.Base64;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 *  An authentication handler for <a href="http://www.stormpath.com">Stormpath</a>. Further documentation
 *  on how the Stormpath REST API operates can be found <a href="https://www.stormpath.com/docs/api/applications#loginAttempts">here</a>.
 *
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, inc.
 * @since 0.8
 */
public class StormpathBasicAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    private String accessId      = null;
    private String secretKey     = null;
    private String applicationId = null;

    /**
     * Receives the Stormpath credentials and attempts to do an early bind to verify credentials.
     * 
     * @param stormpathAccessId accessId provided by Stormpath, for the user with the created API key.
     * @param stormpathSecretKey secret key provided by Stormpath, for the user with the created API key.
     * @param applicationId This is application id configured on Stormpath whose login source will be used to authenticate users.
     * 
     *  @throws RuntimeException If credentials cannot be verified by Stormpath.
     */
    public StormpathBasicAuthenticationHandler(final String stormpathAccessId, final String stormpathSecretKey, final String applicationId) {
        super();

        try {
            this.accessId = stormpathAccessId;
            this.secretKey = stormpathSecretKey;
            this.applicationId = applicationId;

            verifyStormpathCredentials();
        } catch (final Exception e) {
            this.log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Builder buildRestWebResource(final String resourceName) {
        final Client c = getRestClient();
        final String resourceUrl = String.format("%s/%s", getHost(), resourceName);
        final WebResource r = c.resource(resourceUrl);

        return r.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE);
    }

    private String getAccessId() {
        return this.accessId;
    }

    private String getApplicationId() {
        return this.applicationId;
    }

    private String getHost() {
        return "https://api.stormpath.com/v1";
    }

    private Client getRestClient() {
        final Client c = Client.create();
        c.addFilter(new HTTPBasicAuthFilter(getAccessId(), getSecretKey()));
        return c;
    }

    private String getSecretKey() {
        return this.secretKey;
    }

    private void verifyStormpathCredentials() throws Exception {
        this.log.info("Initilizing Stormpath authentication engine for [{}]...", getAccessId());
        buildRestWebResource("tenants/current").get(String.class);
        this.log.info("Initilized Stormpath authentication engine");
    }

    @Override
    protected boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials cred) throws AuthenticationException {
        try {
            this.log.debug("Attempting to authenticate user {}", cred.getUsername());

            final byte[] bytes = String.format("%s:%s", cred.getUsername(), cred.getPassword()).getBytes();
            final String encodedCredentials = Base64.encodeToString(bytes);

            final String resourceName = String.format("applications/%s/loginAttempts", getApplicationId());

            final Map<String, String> requestMap = new HashMap<String, String>(2);
            requestMap.put("type", "basic");
            requestMap.put("value", encodedCredentials);

            final ObjectMapper mapper = new ObjectMapper();
            final String jsonRequest = mapper.writeValueAsString(requestMap);

            buildRestWebResource(resourceName).post(jsonRequest);

            this.log.info("Authenticated user [{}] successfully.", cred.getUsername());

            return true;
        } catch (final Exception e) {
            this.log.error(e.getMessage(), e);
            throw new BadCredentialsAuthenticationException(e);
        }
    }
}
