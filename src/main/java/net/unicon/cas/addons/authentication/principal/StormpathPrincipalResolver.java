package net.unicon.cas.addons.authentication.principal;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.resource.ResourceException;
import net.unicon.cas.addons.authentication.handler.StormpathAuthenticationHandler;
import net.unicon.cas.addons.support.ThreadSafe;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver}
 * that resolves instances of {@link StormpathPrincipal} from provided {@link org.jasig.cas.authentication.principal.UsernamePasswordCredentials}
 * <p/>
 * Note that this implementation makes a remote HTTP call to Stormapth cloud to authenticate the credential and therefore
 * retrieve an instance of an {@link com.stormpath.sdk.account.Account}. Thus, 2 Stormpath remote authentication calls are made -
 * one is during authentication by {@link net.unicon.cas.addons.authentication.handler.StormpathAuthenticationHandler} and
 * one is during <code>Account</code> retrieval by this class.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.4
 */
@ThreadSafe
public class StormpathPrincipalResolver implements CredentialsToPrincipalResolver {

    private final StormpathAuthenticationHandler stormpathAuthenticationHandler;

    private static final Logger logger = LoggerFactory.getLogger(StormpathPrincipalResolver.class);

    public StormpathPrincipalResolver(StormpathAuthenticationHandler stormpathAuthenticationHandler) {
        this.stormpathAuthenticationHandler = stormpathAuthenticationHandler;
    }

    @Override
    public Principal resolvePrincipal(final Credentials credentials) {
        final UsernamePasswordCredentials usernamePasswordCredentials = UsernamePasswordCredentials.class.cast(credentials);
        try {
            final Account account = this.stormpathAuthenticationHandler.authenticateAccount(usernamePasswordCredentials);
            final Principal principal = new StormpathPrincipal(account);
            logger.debug("Successfully resolved {}", principal);
            return principal;
        }
        catch (Throwable e) {
            logger.error("An exception is caught trying to access Stormpath cloud while resolving StormpathPrincipal: {} ", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials != null && UsernamePasswordCredentials.class.isAssignableFrom(credentials.getClass());
    }
}
