package net.unicon.cas.addons.authentication.strong;

/**
 * A strategy API to determine requirements for an additional factor authentication  (e.g. OTP, etc.) for provided principals.
 * <p/>
 * The type of an authentication factor will be determined by implementors of this strategy.
 * <p/>
 * <strong>Concurrency semantics: implementations must be thread safe</strong>
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.5
 */
public interface AdditionalAuthenticationFactorPolicy {

	/**
	 * A runtime exception indicating that a principal identified by principalId passed to any of this service's
	 * operations is not found in the back end configuration store.
	 */
	static class PrincipalNotFoundException extends RuntimeException {

		public PrincipalNotFoundException(String message) {
			super(message);
		}
	}

	/**
	 * Determine if a given principal requires to authenticate with an additional factor.
	 *
	 * @param principalId for which to check the requirement for an additional authentication factor
	 * @return true if additional factor authentication is required, false otherwise.
	 * @throws PrincipalNotFoundException if no such principal exists in the backing store that this strategy uses to look them up.
	 */
	boolean requiresAdditionalAuthenticationFactor(String principalId) throws PrincipalNotFoundException;

	/**
	 * Get a String representation of authentication method this additional factor represents.
	 *
	 * @return String representation of an authentication method.
	 */
	String getAdditionalFactorAuthenticationMethod();
}
