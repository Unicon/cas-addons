package net.unicon.cas.addons.serviceregistry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

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

    /**
     * This Aspect has no effect by default and needs to be explicitly <i>switched on</i> by setting this property to <code>true</code>
     */
    private boolean on;

    public void setOn(boolean on) {
        this.on = on;
    }

    @Pointcut("execution(public void org.jasig.cas.services.ReloadableServicesManager+.reload(..))")
    public void reloadMethodExecution() {
    }

    @Pointcut("cflowbelow(execution(* net.unicon.cas.addons.serviceregistry.JsonServiceRegistryDao.*(..)))")
    public void calledByCasAddonsCode() {
    }

    @Around("reloadMethodExecution() && !calledByCasAddonsCode()")
    public void suppressDefaultCasReloadCall(ProceedingJoinPoint target) throws Throwable {
        if (on) {
            logger.debug("Suppressing default reloading behavior of [{}]...", target.getSignature().toString());
            return;
        }
        target.proceed();
    }
}
