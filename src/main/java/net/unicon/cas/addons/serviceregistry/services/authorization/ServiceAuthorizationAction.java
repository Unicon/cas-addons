package net.unicon.cas.addons.serviceregistry.services.authorization;

import net.unicon.cas.addons.authentication.AuthenticationSupport;
import net.unicon.cas.addons.authentication.internal.DefaultAuthenticationSupport;
import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * An action state to be executed for the authorization check based on registered service attributes before vending a service ticket.
 * <p/>
 * It is to be inserted in the <i>on-entry</i> execution block of the <code>generateServiceTicket</code> action state in the login web flow definition.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.5
 */
public class ServiceAuthorizationAction extends AbstractAction {

    private final ServicesManager servicesManager;

    private final RegisteredServiceAuthorizer authorizer;

    private final AuthenticationSupport authenticationSupport;

    private static final String AUTHZ_ATTRS_KEY = "authzAttributes";

    private static final String AUTHZ_FAIL_REDIRECT_URL_KEY = "authorizationFailureRedirectUrl";

    private static final String ATTR_URL_KEY = "unauthorizedRedirectUrl";

    private static final Logger logger = LoggerFactory.getLogger(ServiceAuthorizationAction.class);


    public ServiceAuthorizationAction(ServicesManager servicesManager, TicketRegistry ticketRegistry, RegisteredServiceAuthorizer registeredServiceAuthorizer) {
        this.servicesManager = servicesManager;
        this.authorizer = registeredServiceAuthorizer;
        this.authenticationSupport = new DefaultAuthenticationSupport(ticketRegistry);
    }

    @Override
    protected Event doExecute(RequestContext requestContext) throws Exception {
        final Principal principal = this.authenticationSupport.getAuthenticatedPrincipalFrom(WebUtils.getTicketGrantingTicketId(requestContext));
        //Guard against expired SSO sessions resulting in NPE. The exception will be handled by the global transition in the login flow
        if (principal == null) {
            logger.info("The SSO session is no longer valid. Restarting the login process...");
            throw new TgtDoesNotExistException();
        }
        final Object principalAttributes = principal.getAttributes();
        final String principalId = principal.getId();
        final Service service = WebUtils.getService(requestContext);

        //Now do the actual RBAC authorization comparing the principal's attributes and registered service's defined attributes
        RegisteredServiceWithAttributes registeredService = (RegisteredServiceWithAttributes) this.servicesManager.findServiceBy(service);
        Object serviceAttributes = registeredService.getExtraAttributes().get(AUTHZ_ATTRS_KEY);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("SERVICE [%s] ATTRIBUTES: %s | PRINCIPAL [%s] ATTRIBUTES: %s",
                    registeredService.getServiceId(), serviceAttributes, principalId, principalAttributes));
        }
        if (!this.authorizer.authorized(serviceAttributes, principalAttributes)) {
            logger.info("Principal [{}] is not authorized to use service [{}]", principalId, service.getId());
            requestContext.getRequestScope().put(AUTHZ_FAIL_REDIRECT_URL_KEY, registeredService.getExtraAttributes().get(ATTR_URL_KEY));
            //Should be handled in the global transition handler to do the actual external redirect to a specific service's URL
            throw new RoleBasedServiceAuthorizationException();
        }
        logger.info("Principal [{}] is authorized to use service [{}]", principalId, service.getId());

        //Everything is fine. Continue with the main service ticket generation action state execution. No need to signal any event to SWF at this stage.
        return null;
    }

    /**
     * Simple runtime exception to indicate a expiration of the SSO session and avoid NPE at runtime
     * Should be handled by the global transition in the login flow to restart the login process.
     */
    public static class TgtDoesNotExistException extends RuntimeException {

    }
}
