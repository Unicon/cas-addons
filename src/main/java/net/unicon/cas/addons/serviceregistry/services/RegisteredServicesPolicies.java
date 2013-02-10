package net.unicon.cas.addons.serviceregistry.services;

import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;

/**
 * Strategy API representing various policies abstractions calculated by means of examining <code>RegisteredServiceWithAttributes</code>
 * configurations.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.2
 */
public interface RegisteredServicesPolicies {

	/**
	 * Policy method governing WebSSO session initiation based on the provided configuration attributes of a given
	 * <code>RegisteredServiceWithAttributes</code>
	 *
	 * @param registeredService for which to determine whether to initiate an SSO session or not
	 * @return true if a provided service is eligible for an SSO session initiation, false otherwise
	 */
	boolean ssoSessionInitiating(RegisteredServiceWithAttributes registeredService);
}
