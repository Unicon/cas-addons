package net.unicon.cas.addons.authentication.handler;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.DefaultApiKey;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;
import net.unicon.cas.addons.support.Immutable;
import net.unicon.cas.addons.support.ThreadSafe;
import org.apache.shiro.codec.Base64;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.beans.factory.BeanCreationException;


/**
 * An authentication handler for <a href="http://www.stormpath.com">Stormpath</a>.
 * <p/>
 * This implementation uses Stormpath's <a href="https://github.com/stormpath/stormpath-sdk-java/wiki">Java SDK</a>
 *
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.0
 */
@Immutable
public class StormpathAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

	private final Application application;

	/**
	 * Receives the Stormpath admin credentials and applicationId and sets up and instance of a Stormpath's Application resource
	 * which will be used to authenticate users.
	 *
	 * @param stormpathAccessId  accessId provided by Stormpath, for the admin user with the created API key.
	 * @param stormpathSecretKey secret key provided by Stormpath, for the admin user with the created API key.
	 * @param applicationId      This is application id configured on Stormpath whose login source will be used to authenticate users.
	 * @throws BeanCreationException If credentials cannot be verified by Stormpath.
	 */
	public StormpathAuthenticationHandler(final String stormpathAccessId, final String stormpathSecretKey, final String applicationId) throws BeanCreationException {
		final Client client = new Client(new DefaultApiKey(stormpathAccessId, stormpathSecretKey));
		try {
			this.application = client.getDataStore().getResource(String.format("/applications/%s", applicationId), Application.class);
		}
		catch (Throwable e) {
			throw new BeanCreationException("An exception is caught trying to access Stormpath cloud. " +
					"Please verify that your provided Stormpath <accessId>, " +
					"<secretKey>, and <applicationId> are correct. Original Stormpath error: " + e.getMessage());
		}
	}

	@Override
	protected boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials credentials) throws AuthenticationException {
		try {
			this.log.debug("Attempting to authenticate user [{}] against application [{}] in Stormpath cloud...", credentials.getUsername(), this.application.getName());
			this.application.authenticateAccount(new UsernamePasswordRequest(credentials.getUsername(), credentials.getPassword()));
			this.log.debug("Successfuly authenticated user [{}]", credentials.getUsername());
			return true;
		}
		catch (ResourceException e) {
			this.log.error(e.getMessage(), e);
			throw new BadCredentialsAuthenticationException();
		}
	}
}
