package net.unicon.cas.addons.serviceregistry.services.authorization;

import net.unicon.cas.addons.authentication.AuthenticationSupport;
import net.unicon.cas.addons.authentication.internal.DefaultAuthenticationSupport;
import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.web.support.WebUtils;
import org.jasig.cas.services.UnauthorizedServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * An action state to be executed for the authorization check based on registered service attributes before vending a service ticket.
 * <p/>
 * It is expected that this action is to be inserted as the first action of the <code>generateServiceTicket</code> action state in the login web flow definition.
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

    public ServiceAuthorizationAction(final ServicesManager servicesManager, final TicketRegistry ticketRegistry, final RegisteredServiceAuthorizer registeredServiceAuthorizer) {
        this.servicesManager = servicesManager;
        this.authorizer = registeredServiceAuthorizer;
        this.authenticationSupport = new DefaultAuthenticationSupport(ticketRegistry);
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) throws Exception {
        final Principal principal = this.authenticationSupport.getAuthenticatedPrincipalFrom(WebUtils.getTicketGrantingTicketId(requestContext));
        //Guard against expired SSO sessions. 'error' event should trigger the transition to the 'generateLoginTicket' state
        if (principal == null) {
            logger.warn("The SSO session is no longer valid. Restarting the login process...");
            return error();
        }
        final Object principalAttributes = principal.getAttributes();
        final String principalId = principal.getId();
        final Service service = WebUtils.getService(requestContext);
        final String serviceId = service.getId();

        //Find this service in the service registry
        final RegisteredService registeredService = this.servicesManager.findServiceBy(service);

        if (registeredService == null) {
            logger.warn("Unauthorized Service Access for Service: [ {} ] - service is not defined in the service registry.", serviceId);
            throw new UnauthorizedServiceException();
        }
        else if (!registeredService.isEnabled()) {
            logger.warn("Unauthorized Service Access for Service: [ {} ] - service is not enabled in the service registry.", serviceId);
            throw new UnauthorizedServiceException();
        }

        if (!(registeredService instanceof RegisteredServiceWithAttributes)) {
            logger.info("Service [{}] is not configured for role-based authorization", registeredService);
            return null;
        }

        final RegisteredServiceWithAttributes registeredServiceWithAttributes = (RegisteredServiceWithAttributes) registeredService;
        //Check to see if RBAC rules have been added to this service's configuration
        Object serviceAttributes = registeredServiceWithAttributes.getExtraAttributes().get(AUTHZ_ATTRS_KEY);
        if (serviceAttributes == null) {
            logger.info("Service [{}] is not configured for role-based authorization", registeredServiceWithAttributes.getServiceId());
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("SERVICE [%s] ATTRIBUTES: %s | PRINCIPAL [%s] ATTRIBUTES: %s",
                        registeredServiceWithAttributes.getServiceId(), serviceAttributes, principalId, principalAttributes));
            }
            //Now do the actual RBAC authorization comparing the principal's attributes and registered service's defined attributes
            if (!this.authorizer.authorized(serviceAttributes, principalAttributes)) {
                logger.info("Principal [{}] is not authorized to use service [{}]", principalId, serviceId);
                requestContext.getRequestScope().put(AUTHZ_FAIL_REDIRECT_URL_KEY, registeredServiceWithAttributes.getExtraAttributes().get(ATTR_URL_KEY));
                //Should be handled in the global transition handler to do the actual external redirect to a specific service's URL
                throw new RoleBasedServiceAuthorizationException();
            }
            logger.info("Principal [{}] is authorized to use service [{}]", principalId, serviceId);
        }

        //Everything is fine. Continue with the main service ticket generation action state execution. null will signal to SWF to try execution of the next action in the chain
        // which should be 'GenerateServiceTicketAction'
        return null;
    }
}
