package net.unicon.cas.addons.authentication.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.shiro.codec.Base64;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 *  An authentication handler for <a href="http://www.stormpath.com">Stormpath</a>. Further documentation
 *  on how the Stormpath REST API operates can be found <a href="https://www.stormpath.com/docs/api/applications#loginAttempts">here</a>.
 *
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, inc.
 * @since 0.8
 */
public class StormpathBasicAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    private static class StormpathAutenticationRequest {

        private String value = null;

        public StormpathAutenticationRequest(final String encodedCredentials) {
            this.value = encodedCredentials;
        }

        @JsonProperty
        private String getType() {
            return "basic";
        }

        @JsonProperty
        private String getValue() {
            return this.value;
        }
    }

    private String username      = null;
    private String password      = null;
    private String applicationId = null;

    public StormpathBasicAuthenticationHandler(final String user, final String password, final String applicationId) {
        super();

        try {
            this.username = user;
            this.password = password;
            this.applicationId = applicationId;

            verifyStormpathCredentials();
        } catch (final Exception e) {
            this.log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    private String getApiVersion() {
        return "v1";
    }

    private String getApplicationId() {
        return this.applicationId;
    }

    private String getHost() {
        return "https://api.stormpath.com";
    }

    private String getPassword() {
        return this.password;
    }

    private String getUsername() {
        return this.username;
    }

    @Override
    protected boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials cred) throws AuthenticationException {
        try {
            this.log.debug("Attempting to authenticate user {}", cred.getUsername());

            final byte[] bytes = String.format("%s:%s", cred.getUsername(), cred.getPassword()).getBytes("UTF-8");
            final String encodedCredentials = Base64.encodeToString(bytes);
            final StormpathAutenticationRequest request = new StormpathAutenticationRequest(encodedCredentials);

            final RestTemplate restTemplate = getRestTemplate();
            final List<HttpMessageConverter<?>> convertersList = new ArrayList<HttpMessageConverter<?>>();
            convertersList.add(new MappingJacksonHttpMessageConverter());
            restTemplate.setMessageConverters(convertersList);

            restTemplate.postForLocation("{host}/{version}/applications/{appId}/loginAttempts", request, getHost(), getApiVersion(),
                    getApplicationId());

            this.log.debug("Authenticated user {} successfully.", cred.getUsername());

            return true;
        } catch (final Exception e) {
            throw new BadCredentialsAuthenticationException(e);
        }
    }

    protected RestTemplate getRestTemplate() {
        final HttpHost targetHost = new HttpHost(getHost());
        final AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
        final Credentials cred = new org.apache.http.auth.UsernamePasswordCredentials(getUsername(), getPassword());

        final DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getCredentialsProvider().setCredentials(authScope, cred);

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpclient);

        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

    protected void verifyStormpathCredentials() {
        final RestTemplate restTemplate = getRestTemplate();

        this.log.info("Initilizing Stormpath authentication engine for [{}]...", getUsername());

        final ResponseEntity<String> response = restTemplate.getForEntity("{host}/{version}/tenants/current", String.class, getHost(),
                getApiVersion());

        this.log.info("Initilized Stormpath authentication engine {}.", response.getStatusCode().getReasonPhrase());
    }
}
