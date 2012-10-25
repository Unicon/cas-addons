package net.unicon.cas.addons.info;

import net.unicon.cas.addons.support.ThreadSafe;
import org.jasig.cas.CasVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * TODO: DOCUMENT ME!!!
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 *
 * @since 1.0.1
 */
@ThreadSafe
@Component
public final class CasServerVersionInfoEmitter implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger logger = LoggerFactory.getLogger(CasServerVersionInfoEmitter.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Package casPackage = CasVersion.class.getPackage();
		logger.info("CAS version [{}] is ready to serve!", CasVersion.getVersion());
	}
}
