package net.unicon.cas.addons.web.flow;

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
import org.springframework.webflow.test.MockRequestContext;

import javax.validation.constraints.NotNull;

/**
 * Performs a basic check if an authentication request for a provided service is authorized to proceed
 * based on the registered services registry configuration (or lack thereof).
 * <p/>
 * Adds an additional support for a custom <i>unauthorizedUrl</i> attribute in case of a registered service is
 * not enabled.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.0.2
 */
public final class ServiceAuthorizationCheckWithCustomView extends AbstractAction {

	@NotNull
	private final ServicesManager servicesManager;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String DISABLED_SERVICE_URL_ATTRIBUTE = "disabledServiceUrl";

	public ServiceAuthorizationCheckWithCustomView(final ServicesManager servicesManager) {
		this.servicesManager = servicesManager;
	}

	@Override
	protected Event doExecute(final RequestContext context) throws Exception {
		final Service service = WebUtils.getService(context);
		//No service == plain /login request. Return success indicating transition to the login form
		if (service == null) {
			return success();
		}
		final RegisteredService registeredService = this.servicesManager.findServiceBy(service);

		if (registeredService == null) {
			logger.warn("Unauthorized Service Access for Service: [ {} ] - service is not defined in the service registry.", service.getId());
			throw new UnauthorizedServiceException();
		}
		else if (!registeredService.isEnabled()) {
			logger.warn("Unauthorized Service Access for Service: [ {} ] - service is not enabled in the service registry.", service.getId());
			if (registeredService instanceof RegisteredServiceWithAttributes) {
				String disabledServiceUrl = (String) RegisteredServiceWithAttributes.class.cast(registeredService).getExtraAttributes().get(DISABLED_SERVICE_URL_ATTRIBUTE);
				if (disabledServiceUrl != null) {
					context.getRequestScope().put(DISABLED_SERVICE_URL_ATTRIBUTE, disabledServiceUrl);
					return no();
				}
			}
			throw new UnauthorizedServiceException();
		}
		return success();
	}
}