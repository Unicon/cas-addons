package net.unicon.cas.addons.web.support;

import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;
import net.unicon.cas.addons.serviceregistry.services.RegisteredServicesPolicies;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Specialization of <code>CookieRetrievingCookieGenerator</code> that decides whether to generate or not CAS TGC
 * based on a particular service's configuration setting for web SSO initiation.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.2
 */
public class ServiceInitiatingWebSsoAwareCookieGenerator extends CookieRetrievingCookieGenerator implements
		InitializingBean {

	private ServicesManager servicesManager;

	private List<ArgumentExtractor> argumentExtractors;

	private RegisteredServicesPolicies registeredServicesPolicies;

	public void setServicesManager(ServicesManager servicesManager) {
		this.servicesManager = servicesManager;
	}

	public void setArgumentExtractors(List<ArgumentExtractor> argumentExtractors) {
		this.argumentExtractors = argumentExtractors;
	}

	public void setRegisteredServicesPolicies(RegisteredServicesPolicies registeredServicesPolicies) {
		this.registeredServicesPolicies = registeredServicesPolicies;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.servicesManager, "servicesManager is required.");
		Assert.notNull(this.argumentExtractors, "argumentsExtractors are required.");
		Assert.notNull(this.registeredServicesPolicies, "registeredServicesPolicies is required.");
	}

	@Override
	public void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieValue) {
		RegisteredServiceWithAttributes registeredService  =
				RegisteredServiceWithAttributes.class.cast(this.servicesManager.findServiceBy(WebUtils.getService(this.argumentExtractors, request)));

		if (this.registeredServicesPolicies.ssoSessionInitiating(registeredService)) {
			super.addCookie(request, response, cookieValue);
		}
	}
}
