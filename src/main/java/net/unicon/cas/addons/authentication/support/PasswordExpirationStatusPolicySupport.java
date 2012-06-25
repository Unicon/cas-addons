package net.unicon.cas.addons.authentication.support;

import net.unicon.cas.addons.authentication.PasswordExpirationStatusPolicy;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

import java.util.Map;

/**
 * Skeletal convenience implementation of <code>PasswordExpirationStatusPolicy</code> to make implementations easier e.g. methods could be implemented
 * selectively.
 *
 * All methods throw <code>UnsupportedOperationException</code> and so subclasses should override (and implement) the methods of interest.

 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.5
 */
public class PasswordExpirationStatusPolicySupport implements PasswordExpirationStatusPolicy {

	@Override
	public PasswordStatus computePasswordExpirationStatus(Principal principal) throws RuntimeException {
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	@Override
	public PasswordStatus computePasswordExpirationStatus(Map<String, Object> principalAttributes) throws RuntimeException {
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	@Override
	public PasswordStatus computePasswordExpirationStatus(UsernamePasswordCredentials credentials) throws RuntimeException {
		throw new UnsupportedOperationException("This method is not implemented.");
	}
}
