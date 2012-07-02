package net.unicon.cas.addons.authentication.principal;

import net.unicon.cas.addons.authentication.principal.util.PrincipalUtils;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.principal.AbstractPersonDirectoryCredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

/**
 * An implementation of the {@link AbstractPersonDirectoryCredentialsToPrincipalResolver} that accepts an email address
 * as the {@link UsernamePasswordCredentials}'s username and resolves it back to the user id.
 * <p>Note: this API is only intended to be called by CAS server code e.g. any custom CAS server overlay extension, etc.</p>
 * 
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, inc.
 * @since 0.6
 */
public class EmailAddressPasswordCredentialsToPrincipalResolver extends AbstractPersonDirectoryCredentialsToPrincipalResolver {

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials != null && UsernamePasswordCredentials.class.isAssignableFrom(credentials.getClass());
    }

    @Override
    protected String extractPrincipalId(final Credentials credentials) {
        if (credentials == null) {
            return null;
		}

        final UsernamePasswordCredentials usernamePasswordCredentials = (UsernamePasswordCredentials) credentials;

        if (StringUtils.isBlank(usernamePasswordCredentials.getUsername())) {
            return null;
		}

        return PrincipalUtils.parseNamePartFromEmailAddressIfNecessary(usernamePasswordCredentials.getUsername());
    }
}