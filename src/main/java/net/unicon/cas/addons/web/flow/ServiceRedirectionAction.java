package net.unicon.cas.addons.web.flow;

import javax.validation.constraints.NotNull;

import net.unicon.cas.addons.serviceregistry.JsonServiceRegistryDao;
import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.services.UnauthorizedServiceException;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * A spring webflow action that attempts to determine whether access to a {@link Service}
 * is required first to be redirected to an external url, defined by the metadata in the
 * service registry. This functionality is dependent upon the usage of the {@link JsonServiceRegistryDao}
 * where the redirection url is provided as an extra attribute via {@link #REDIRECT_TO_URL_ATTRIBUTE}.
 * 
 * The strategy of evaluating whether a redirection is required is determined by
 * {@link #setRedirectionAdvisor(ServiceRedirectionAdvisor)}. By default, no redirection
 * is required.
 * 
 * @author Misagh Moayyed (<a href="mailto:mmoayyed@unicon.net">mmoayyed@unicon.net</a>)
 * @since 1.9
 * @see InMemoryServiceRedirectionByClientIpAddressAdvisor
 */
public final class ServiceRedirectionAction extends AbstractAction {

    @NotNull
    private final ServicesManager servicesManager;

    private static final Logger logger = LoggerFactory.getLogger(ServiceRedirectionAction.class);

    private static final String REDIRECT_TO_URL_ATTRIBUTE = "redirectToUrl";

    private ServiceRedirectionAdvisor redirectionAdvisor;

    public ServiceRedirectionAction(@NotNull final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    public void setRedirectionAdvisor(@NotNull final ServiceRedirectionAdvisor advisor) {
        this.redirectionAdvisor = advisor;
    }

    @Override
    protected Event doExecute(final RequestContext context) throws Exception {
        final Service service = WebUtils.getService(context);

        if (this.redirectionAdvisor == null) {
            logger.debug("No service redirection strategy/advisor is configured, so resuming normally.");
            return success();
        }
        
        if (service == null) {
            logger.debug("No service found in the request context, so resuming normally.");
            return success();
        }

        final RegisteredService registeredService = this.servicesManager.findServiceBy(service);

        if (registeredService == null) {
            logger.warn("Unauthorized Service Access for Service: [{}] - service is not defined in the service registry.", service.getId());
            throw new UnauthorizedServiceException();
        }

        if (!registeredService.isEnabled()) {
            logger.warn("Unauthorized Service Access for Service: [{}] - service is not enabled in the service registry.", service.getId());
            throw new UnauthorizedServiceException();
        }

        if (registeredService instanceof RegisteredServiceWithAttributes) {
            final RegisteredServiceWithAttributes regSvcWithAttr = RegisteredServiceWithAttributes.class.cast(registeredService);

            final String redirectToUrl = (String) regSvcWithAttr.getExtraAttributes().get(REDIRECT_TO_URL_ATTRIBUTE);
            if (redirectToUrl != null && this.redirectionAdvisor.shouldRedirectServiceRequest(context, regSvcWithAttr, redirectToUrl)) {
                logger.info("Redirecting to url [{}] for service [{}]", redirectToUrl, service.getId());
                context.getRequestScope().put(REDIRECT_TO_URL_ATTRIBUTE, redirectToUrl);
                return yes();
            } 
        }

        logger.debug("No redirect url is configured, or redirection for service [{}] is not needed", service.getId());
        return success();
    }
}
