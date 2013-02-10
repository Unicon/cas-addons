package net.unicon.cas.addons.serviceregistry.services.internal;

import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;
import net.unicon.cas.addons.serviceregistry.services.RegisteredServicesPolicies;

/**
 * Default implementation of <code>RegisteredServicesPolicies</code>
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.2
 */
public class DefaultRegisteredServicesPolicies implements RegisteredServicesPolicies {

	public static final String SSO_INITIATION_ATTRIBUTE_KEY = "initiateSSO";

	@Override
	public boolean ssoSessionInitiating(RegisteredServiceWithAttributes registeredService) {
		return registeredService.getExtraAttributes().containsKey(SSO_INITIATION_ATTRIBUTE_KEY)
				&& Boolean.class.cast(registeredService.getExtraAttributes().get(SSO_INITIATION_ATTRIBUTE_KEY));
	}
}
