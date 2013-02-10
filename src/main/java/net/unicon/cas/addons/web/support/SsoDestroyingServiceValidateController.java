package net.unicon.cas.addons.web.support;

import net.unicon.cas.addons.authentication.support.Assertions;
import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;
import net.unicon.cas.addons.serviceregistry.services.RegisteredServicesPolicies;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.validation.Assertion;
import org.jasig.cas.web.ServiceValidateController;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An extention of a <code>ServiceValidateController</code> that destroys server-side TGT upon successful Service Ticket validation.
 * <p/>
 * Useful for services that are configured not to initiate WebSSO sessions altogether.
 *
 * @author Dmitriy Kopylenko,
 * @author Unicon, inc.
 * @since 1.2
 */
public class SsoDestroyingServiceValidateController extends ServiceValidateController implements InitializingBean {

	private TicketRegistry ticketRegistry;

	private CentralAuthenticationService cas;

	private ServicesManager servicesManager;

	private ArgumentExtractor argExtractor;

	private RegisteredServicesPolicies registeredServicesPolicies;

	private static final ThreadLocal<TicketGrantingTicket> tgtHolder = new ThreadLocal<TicketGrantingTicket>();

	public void setTicketRegistry(TicketRegistry ticketRegistry) {
		this.ticketRegistry = ticketRegistry;
	}

	public void setCas(CentralAuthenticationService cas) {
		this.cas = cas;
		super.setCentralAuthenticationService(cas);
	}

	public void setServicesManager(ServicesManager servicesManager) {
		this.servicesManager = servicesManager;
	}

	public void setArgExtractor(ArgumentExtractor argExtractor) {
		this.argExtractor = argExtractor;
		super.setArgumentExtractor(argExtractor);
	}

	public void setRegisteredServicesPolicies(RegisteredServicesPolicies registeredServicesPolicies) {
		this.registeredServicesPolicies = registeredServicesPolicies;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.ticketRegistry, "ticketRegistry is required");
		Assert.notNull(this.cas, "cas is required");
		Assert.notNull(this.servicesManager, "servicesManager is required");
		Assert.notNull(this.argExtractor, "argExtractor is required");
		Assert.notNull(this.registeredServicesPolicies, "registeredServicesPolicies is required");
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		final WebApplicationService service = this.argExtractor.extractService(request);
		if (service == null) {
			return super.handleRequest(request, response);
		}
		Ticket st = this.ticketRegistry.getTicket(service.getArtifactId());
		if (st == null) {
			return super.handleRequest(request, response);
		}
		//Make the tgt available in ThreadLocal for access in "onSuccessfulValidation()"
		tgtHolder.set(st.getGrantingTicket());
		return super.handleRequest(request, response);
	}

	@Override
	protected void onSuccessfulValidation(String serviceTicketId, Assertion assertion) {
		try {
			RegisteredServiceWithAttributes registeredService =
					RegisteredServiceWithAttributes.class.cast(this.servicesManager.findServiceBy(assertion.getService()));

			if (!this.registeredServicesPolicies.ssoSessionInitiating(registeredService)) {
				this.cas.destroyTicketGrantingTicket(tgtHolder.get().getId());
			}
		}
		finally {
			tgtHolder.remove();
		}
	}
}
