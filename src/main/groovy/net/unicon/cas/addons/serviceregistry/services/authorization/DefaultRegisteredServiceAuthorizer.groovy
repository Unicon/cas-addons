package net.unicon.cas.addons.serviceregistry.services.authorization

/**
 * A default implementation of <code>RegisteredServiceAuthorizer</code>
 *
 * This implementation uses a simple intersection of both sets of provided attributes (Map keys)
 * and authorizes further processing if any of the values from this flatten intersection of values match
 * for any given Map key that intersect.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.5
 */
class DefaultRegisteredServiceAuthorizer implements RegisteredServiceAuthorizer {

    /**
     * We always know in this case that attributes are instances of Map<String, Object>
     */
    @Override
    boolean authorized(Object serviceAttributes, Object authenticatedPrincipalAttributes) {
        //Groovy JDK APIs!
        serviceAttributes.keySet().intersect(authenticatedPrincipalAttributes.keySet()).any
                { serviceAttributes[it].intersect([authenticatedPrincipalAttributes[it]].flatten()) }
    }
}
