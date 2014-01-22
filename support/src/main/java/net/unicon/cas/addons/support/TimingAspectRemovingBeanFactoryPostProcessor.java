package net.unicon.cas.addons.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * <code>BeanFactoryPostProcessor</code> to remove default perf4j <code>TimingAspect</code> bean definition
 * from CAS' core <i>applicationContext</i>.
 * <p/>
 * Useful in cases where perf4j facility is not used and all the perf4j library dependencies are removed from CAS server.
 * <p/>
 * This bean just needs to be declared in CAS' application context and upon bootstrap Spring will call back into it and
 * <code>TimingAspect</code> bean definition will be removed from the final application context.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.8
 */
public class TimingAspectRemovingBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static final String TIMING_ASPECT_BEAN_NAME = "timingAspect";

    private static final Logger logger = LoggerFactory.getLogger(TimingAspectRemovingBeanFactoryPostProcessor.class);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.debug("Removing [{}] bean definition from the application context...", TIMING_ASPECT_BEAN_NAME);
        BeanDefinitionRegistry.class.cast(beanFactory).removeBeanDefinition(TIMING_ASPECT_BEAN_NAME);
    }
}
