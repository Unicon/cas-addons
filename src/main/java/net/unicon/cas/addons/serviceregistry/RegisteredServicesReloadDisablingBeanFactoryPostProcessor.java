package net.unicon.cas.addons.serviceregistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * <code>BeanFactoryPostProcessor</code> to remove 2 quartz beans responsible for reloading the default services registry's registered services.
 * <p/>
 * Useful in cases where other facilities are responsible for reloading in-memory services cache, for example on-demand reloading
 * of JSON services registry, etc.
 * <p/>
 * This bean just needs to be declared in CAS' application context and upon bootstrap Spring will call back into it and
 * 2 scheduling quartz beans dedicated for services registry reloading thread will be removed from the final application context
 * effectively disabling the default reloading behavior.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.8
 */
public class RegisteredServicesReloadDisablingBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static final String JOB_DETAIL_BEAN_NAME = "serviceRegistryReloaderJobDetail";

    private static final String JOB_TRIGGER_BEAN_NAME = "periodicServiceRegistryReloaderTrigger";

    private static final Logger logger = LoggerFactory.getLogger(RegisteredServicesReloadDisablingBeanFactoryPostProcessor.class);

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.debug("Removing [{}] bean definition from the application context...", JOB_DETAIL_BEAN_NAME);
        BeanDefinitionRegistry.class.cast(beanFactory).removeBeanDefinition(JOB_DETAIL_BEAN_NAME);
        logger.debug("Removing [{}] bean definition from the application context...", JOB_TRIGGER_BEAN_NAME);
        BeanDefinitionRegistry.class.cast(beanFactory).removeBeanDefinition(JOB_TRIGGER_BEAN_NAME);
    }
}
