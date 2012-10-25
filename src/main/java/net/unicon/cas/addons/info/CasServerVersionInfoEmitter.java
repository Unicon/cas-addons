package net.unicon.cas.addons.info;

import net.unicon.cas.addons.support.ThreadSafe;
import org.jasig.cas.CasVersion;
import org.jasig.cas.CentralAuthenticationServiceImpl;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.AuthenticationManagerImpl;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.services.ServiceRegistryDao;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.ticket.support.MultiTimeUseOrTimeoutExpirationPolicy;
import org.jasig.cas.ticket.support.TicketGrantingTicketExpirationPolicy;
import org.jasig.cas.web.StatisticsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Outputs CAS version number to the configurred logger at the Spring Apllication Context refresh time.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.0.1
 */
@ThreadSafe
@Component
public final class CasServerVersionInfoEmitter implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger logger = LoggerFactory.getLogger(CasServerVersionInfoEmitter.class);

	/**
	 * The ContextRefreshEvent could happen several times in the application context. We are only interested to emit the version info
	 * during the first refresh
	 */
	private AtomicInteger numberOfRefreshes = new AtomicInteger(0);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (this.numberOfRefreshes.compareAndSet(0, 1)) {
			logger.info("=======| WELCOME TO CAS VERSION [{}] |=======", CasVersion.getVersion());
		}
	}
}
