package net.unicon.cas.addons.info.events;

import net.unicon.cas.addons.authentication.AuthenticationSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Aspect implementing a mechanism by which to intercept core CAS runtime events and publish them as Spring <code>ApplicationEvent</code>s
 * to <code>ApplicationContext</code> in which CAS server is deployed for further consumption by any number of registered <code>ApplicationListener</code>s.
 * <p/>
 * The events published by this aspect are {@link CasSsoSessionEstablishedEvent}, {@link CasSsoSessionDestroyedEvent}, {@link CasServiceTicketGrantedEvent}, {@link CasServiceTicketValidatedEvent}
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.1
 */
@Aspect
public class CentralAuthenticationServiceEventsPublishingAspect implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher eventPublisher;

	private final AuthenticationSupport authenticationSupport;

	public CentralAuthenticationServiceEventsPublishingAspect(AuthenticationSupport authenticationSupport) {
		this.authenticationSupport = authenticationSupport;
	}

	private final Logger logger = LoggerFactory.getLogger(CentralAuthenticationServiceEventsPublishingAspect.class);

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

	//CentralAuthenticationService API pointcuts which pick out joinpoints that should be advised and turned into ApplicationEvents by this Aspect
	//--------------------------------------------------------------------------------------------------------------------------------------------
	@Pointcut("execution(public * org.jasig.cas.CentralAuthenticationService+.createTicketGrantingTicket(..))")
	private void createTicketGrantingTicketMethodExecution() {
	}

	@Pointcut("execution(public * org.jasig.cas.CentralAuthenticationService+.destroyTicketGrantingTicket(..))")
	private void destroyTicketGrantingTicketMethodExecution() {
	}

	@Pointcut("execution(public * org.jasig.cas.CentralAuthenticationService+.grantServiceTicket(String, org.jasig.cas.authentication.principal.Service))")
	private void grantServiceTicketMethodExecution() {
	}

	@Pointcut("execution(public * org.jasig.cas.CentralAuthenticationService+.validateServiceTicket(..))")
	private void validateServiceTicketMethodExecution() {
	}


	//Advice implementations that turn captured advised CAS API joinpoints context into Spring's ApplicationEvents and publish them
	//-----------------------------------------------------------------------------------------------------------------------------
	@AfterReturning(pointcut = "createTicketGrantingTicketMethodExecution()", returning = "ticketGrantingTicketId")
	public void publishCasSsoSessionEstablishedEvent(final JoinPoint jp, final String ticketGrantingTicketId) {
		doPublish(new CasSsoSessionEstablishedEvent(jp.getTarget(), this.authenticationSupport.getAuthenticationFrom(ticketGrantingTicketId), ticketGrantingTicketId));
	}

	@Around("destroyTicketGrantingTicketMethodExecution() && args(ticketGrantingTicketId)")
	public void publishCasSsoSessionDestroyedEvent(final ProceedingJoinPoint jp, final String ticketGrantingTicketId) throws Throwable {
		final Authentication authToBeDestroyed = this.authenticationSupport.getAuthenticationFrom(ticketGrantingTicketId);
		final ApplicationEvent e = new CasSsoSessionDestroyedEvent(jp.getTarget(), authToBeDestroyed, ticketGrantingTicketId);
		jp.proceed(new Object[] {ticketGrantingTicketId});
		doPublish(e);
	}

	@AfterReturning(pointcut = "grantServiceTicketMethodExecution() && args(ticketGrantingTicketId, service)", returning = "serviceTicketId")
	public void publishCasServiceTicketGrantedEvent(final JoinPoint jp, final String ticketGrantingTicketId, Service service, String serviceTicketId) {
		doPublish(new CasServiceTicketGrantedEvent(jp.getTarget(), serviceTicketId, service, this.authenticationSupport.getAuthenticationFrom(ticketGrantingTicketId)));
	}

	@AfterReturning(pointcut = "validateServiceTicketMethodExecution() && args(serviceTicketId, service)", returning = "assertion")
	public void publishCasServiceTicketValidatedEvent(final JoinPoint jp, final String serviceTicketId, final Service service, final Assertion assertion) {
		doPublish(new CasServiceTicketValidatedEvent(jp.getTarget(), serviceTicketId, service, assertion));
	}

	private void doPublish(ApplicationEvent e) {
		logger.info("Publishing {}", e);
		this.eventPublisher.publishEvent(e);
	}
}
