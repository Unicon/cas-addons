package net.unicon.cas.addons.serviceregistry.services.authorization;

/**
 * An authorization strategy interface for vending service tickets based on {@link net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes} authorization attributes.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.5
 */
public interface RegisteredServiceAuthorizer {

    /**
     * Determine if CAS is authorized to vend service ticket for the given service by comparing configured registered service authorization attributes
     * with the actual resolved attributes of an authenticated principal.
     *
     * @param serviceAttributes
     * @param authenticatedPrincipalAttributes
     *
     * @return true if authorized, false otherwise
     */
    boolean authorized(Object serviceAttributes, Object authenticatedPrincipalAttributes);
}
