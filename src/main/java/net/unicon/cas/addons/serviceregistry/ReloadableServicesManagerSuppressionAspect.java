package net.unicon.cas.addons.serviceregistry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An aspect to suppress <code>reload()</code> calls on the default CAS' implementation of <code>ReloadableServicesManager</code> instance
 * to enable external resource notification reloading behavior e.g. ${link JsonServiceRegistryDao} without interference from the default
 * polling reloading behavior of CAS.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.9.5
 */
@Aspect
public final class ReloadableServicesManagerSuppressionAspect {

	private static final Logger logger = LoggerFactory.getLogger(ReloadableServicesManagerSuppressionAspect.class);

	@Pointcut("execution(public void org.jasig.cas.services.ReloadableServicesManager+.reload(..))")
	public void reloadMethodExecution(){}

	@Pointcut("cflowbelow(execution(* net.unicon.cas.addons.serviceregistry.JsonServiceRegistryDao.*(..)))")
	public void calledByCasAddonsCode(){}

	@Around("reloadMethodExecution() && !calledByCasAddonsCode()")
	public void suppressDefaultCasReloadCall(ProceedingJoinPoint target) {
		logger.debug("Suppressing default reloading behavior of [{}]...", target.getSignature().toString());
	}
}
