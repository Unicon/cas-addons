package net.unicon.cas.addons.authentication.support;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.validation.Assertion;

import java.util.List;

/**
 * Convenience utility class containing static methods that operate on CAS server's <code>Assertion</code> instances for ease of retrieving
 * authenticated <code>Principal</code>s as well as chains of <code>Authentication</code> objects.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.1
 */
public final class Assertions {

	/**
	 * Non-instantiable
	 */
	private Assertions() {
	}

	/**
	 * Extracts an authenticated ${@link Principal} from a provided ${@link Assertion} instance.
	 * <p/>
	 * It is a caller's of this method responsibility to ensure that provided ${@link Assertion} instance is not null
	 * to avoid NPE at runtime.
	 *
	 * @param assertion instance to retrieve ${@link Principal} from
	 * @return Authenticated Principal
	 * @throws NullPointerException if provided Assertion is <i>null</i>
	 */
	public static Principal getAuthenticatedPrincipalFrom(Assertion assertion) {
		final List<Authentication> chain = assertion.getChainedAuthentications();
		final Principal principal = chain.get(chain.size() - 1).getPrincipal();
		return principal;
	}

	/**
	 * Extracts a list of proxy ${@link Authentication}s MINUS the original authenticated ${@link Principal} f
	 * from a provided ${@link Assertion} instance.
	 * <p/>
	 * It is a caller's of this method responsibility to ensure that provided ${@link Assertion} instance is not null
	 * to avoid NPE at runtime.
	 *
	 * @param assertion instance to retrieve a list of proxy ${@link Authentication}s from
	 * @return A list of proxy authentications
	 * @throws NullPointerException if provided Assertion is <i>null</i>
	 */
	public static List<Authentication> getProxyAuthenticationsFrom(Assertion assertion) {
		final List<Authentication> chain = assertion.getChainedAuthentications();
		final List<Authentication> proxyAuthentications = chain.subList(0, chain.size()-1);
		return proxyAuthentications;
	}
}
