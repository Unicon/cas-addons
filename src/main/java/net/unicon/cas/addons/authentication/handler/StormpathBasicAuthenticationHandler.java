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
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * An authentication handler for <a href="http://www.stormpath.com">Stormpath</a>. Further documentation
 * on how the Stormpath REST API operates can be found <a href="https://www.stormpath.com/docs/api/applications#loginAttempts">here</a>.
 *
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, inc.
 * @since 0.8
 */
public class StormpathBasicAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

	private final String accessId;

	private final String secretKey;

	private final String applicationId;

	private final static String STORMPATH_API_BASE_URI = "https://api.stormpath.com/v1";

	private final static String TENANTS_CURRENT_URI = "/tenants/current";

	private final static String LOGIN_ATTEMPTS_URI = "/applications/%s/loginAttempts";

	/**
	 * Representation of the Stormpath tenants/current REST resource
	 */
	private final Builder tennantsCurrentWebResource;

	/**
	 * Representation of the Stormpath login attempts REST resource
	 */
	private final Builder loginAttemptsWebResource;

	/**
	 * The instance is thread-safe
	 */
	private final ObjectMapper jacksonMapper = new ObjectMapper();

	/**
	 * Receives the Stormpath credentials and attempts to do an early bind to verify credentials.
	 *
	 * @param stormpathAccessId  accessId provided by Stormpath, for the user with the created API key.
	 * @param stormpathSecretKey secret key provided by Stormpath, for the user with the created API key.
	 * @param applicationId      This is application id configured on Stormpath whose login source will be used to authenticate users.
	 * @throws RuntimeException If credentials cannot be verified by Stormpath.
	 */
	public StormpathBasicAuthenticationHandler(final String stormpathAccessId, final String stormpathSecretKey, final String applicationId) {
		super();

		this.accessId = stormpathAccessId;
		this.secretKey = stormpathSecretKey;
		this.applicationId = applicationId;

		final Client restClient = Client.create();
		//HTTP Basic Auth
		restClient.addFilter(new HTTPBasicAuthFilter(this.accessId, this.secretKey));

		//Create 2 Stormpath REST resources. Once configured, the instances are thread-safe
		this.tennantsCurrentWebResource = restClient.resource(STORMPATH_API_BASE_URI + TENANTS_CURRENT_URI)
				.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE);

		this.loginAttemptsWebResource = restClient.resource(STORMPATH_API_BASE_URI + String.format(LOGIN_ATTEMPTS_URI, this.applicationId))
				.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE);
		try {
			verifyStormpathCredentials();
		}
		catch (final Exception e) {
			this.log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void verifyStormpathCredentials() throws Exception {
		this.log.info("Initilizing Stormpath authentication engine for [{}]...", this.accessId);
		this.tennantsCurrentWebResource.get(String.class);
		this.log.info("Initilized Stormpath authentication engine");
	}

	@Override
	protected boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials cred) throws AuthenticationException {
		try {
			this.log.debug("Attempting to authenticate user {}", cred.getUsername());

			final byte[] bytes = String.format("%s:%s", cred.getUsername(), cred.getPassword()).getBytes();
			final String encodedCredentials = Base64.encodeToString(bytes);

			//Build resource entity body payload
			final Map<String, String> requestMap = new HashMap<String, String>(2);
			requestMap.put("type", "basic");
			requestMap.put("value", encodedCredentials);
			final String jsonRequest = this.jacksonMapper.writeValueAsString(requestMap);

			this.loginAttemptsWebResource.post(jsonRequest);
			this.log.info("Authenticated user [{}] successfully.", cred.getUsername());

			return true;
		}
		catch (final Exception e) {
			this.log.error(e.getMessage(), e);
			throw new BadCredentialsAuthenticationException(e);
		}
	}
}
