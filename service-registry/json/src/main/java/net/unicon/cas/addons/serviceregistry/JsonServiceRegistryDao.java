package net.unicon.cas.addons.serviceregistry;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.unicon.cas.addons.support.GuardedBy;
import net.unicon.cas.addons.support.ResourceChangeDetectingEventNotifier;
import net.unicon.cas.addons.support.ThreadSafe;

import org.jasig.cas.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Implementation of <code>ServiceRegistryDao</code> that reads services definition from JSON configuration file at the Spring Application Context
 * initialization time. After un-marshaling services from JSON blob, delegates the storage and retrieval to <code>InMemoryServiceRegistryDaoImpl</code>
 * <p/>
 * This class implements ${link ApplicationListener<ResourceChangeDetectingEventNotifier.ResourceChangedEvent>} to reload services definitions in real-time.
 * This class is thread safe.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.8
 */
@ThreadSafe
public class JsonServiceRegistryDao implements ServiceRegistryDao,
		ApplicationListener<ResourceChangeDetectingEventNotifier.ResourceChangedEvent> {

	@GuardedBy("mutexMonitor")
	private final InMemoryServiceRegistryDaoImpl delegateServiceRegistryDao = new InMemoryServiceRegistryDaoImpl();

	protected final ObjectMapper objectMapper = new ObjectMapper();

	protected final Resource servicesConfigFile;

	private ReloadableServicesManager servicesManager;

	private final Object mutexMonitor = new Object();

	private static final String REGEX_PREFIX = "^";

	protected static final String SERVICES_KEY = "services";

	private static final String SERVICES_ID_KEY = "serviceId";

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public JsonServiceRegistryDao(final Resource servicesConfigFile) {
		this.servicesConfigFile = servicesConfigFile;
	}

	public final void setServicesManager(final ReloadableServicesManager servicesManager) {
		this.servicesManager = servicesManager;
	}

	@Override
	public final RegisteredService save(RegisteredService registeredService) {
		synchronized (this.mutexMonitor) {
			return saveInternal(registeredService);
		}
	}

	@Override
	public final boolean delete(RegisteredService registeredService) {
		synchronized (this.mutexMonitor) {
			return deleteInternal(registeredService);
		}
	}

	@Override
	public final RegisteredService findServiceById(long id) {
		synchronized (this.mutexMonitor) {
			return this.delegateServiceRegistryDao.findServiceById(id);
		}
	}

	@Override
	public final List<RegisteredService> load() {
		synchronized (this.mutexMonitor) {
            return this.delegateServiceRegistryDao.load();
		}
	}

    protected RegisteredService saveInternal(final RegisteredService registeredService) {
        return this.delegateServiceRegistryDao.save(registeredService);
    }

    protected boolean deleteInternal(final RegisteredService registeredService) {
        return this.delegateServiceRegistryDao.delete(registeredService);
    }
	/**
	 * This method is used as a Spring bean loadServices-method
	 * as well as the reloading method when the change in the services definition resource is detected at runtime
	 */
    @SuppressWarnings("unchecked")
    public final List<RegisteredService> loadServices() {
        logger.info("Loading Registered Services from: [ {} ]...", this.servicesConfigFile);
        final List<RegisteredService> resolvedServices = new ArrayList<RegisteredService>();
        
        try {
            
            final Map<String, List> m = unmarshalServicesRegistryResourceIntoMap();
            
            if (m != null) {
              final Iterator<Map> i = m.get(SERVICES_KEY).iterator();
              while (i.hasNext()) {
                  final Map<?, ?> record = i.next();
                  final String svcId = ((String) record.get(SERVICES_ID_KEY));
                  final RegisteredService svc = getRegisteredServiceInstance(svcId);
                  if (svc != null) {
                      resolvedServices.add(this.objectMapper.convertValue(record, svc.getClass()));
                      logger.debug("Unmarshaled {}: {}", svc.getClass().getSimpleName(), record);
                  }
              }
  
              synchronized (this.mutexMonitor) {
                  this.delegateServiceRegistryDao.setRegisteredServices(resolvedServices);
              }
            }
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
        return resolvedServices;
    }

    private Map<String, List> unmarshalServicesRegistryResourceIntoMap() throws IOException {
        try {
          final InputStream stream = this.servicesConfigFile.getInputStream();
          
          if (stream != null) {
              return this.objectMapper.readValue(stream, Map.class);
          }  
        } catch (final FileNotFoundException e) {
            logger.warn("Resource [{}] does not exist or has no service definitions.", this.servicesConfigFile);
        }
        
        return null;
    }
        
    private boolean isValidRegexPattern(final String pattern) {
        boolean valid = false;
        try {
            if (pattern.startsWith(REGEX_PREFIX)) {
                Pattern.compile(pattern);
                valid = true;
            }
        } catch (final PatternSyntaxException e) {
            logger.debug("Failed to identify [{}] as a regular expression", pattern);
        }
        return valid;
    }

    /**
     * Constructs an instance of {@link RegisteredServiceWithAttributes} based on the
     * syntax of the pattern defined. If the pattern is considered a valid regular expression,
     * an instance of {@link RegexRegisteredServiceWithAttributes} is created. Otherwise,
     * {@link RegisteredServiceWithAttributesImpl}.
     * @see #isValidRegexPattern(String)
     * @param pattern the pattern of the service definition
     * @return  an instance of {@link RegisteredServiceWithAttributes}
     */
    private RegisteredService getRegisteredServiceInstance(final String pattern) {
        if (isValidRegexPattern(pattern)) {
            return new RegexRegisteredServiceWithAttributes();
        }

        return new RegisteredServiceWithAttributesImpl();
    }

    @Override
	public final void onApplicationEvent(final ResourceChangeDetectingEventNotifier.ResourceChangedEvent resourceChangedEvent) {
		try {
			if (!resourceChangedEvent.getResourceUri().equals(this.servicesConfigFile.getURI())) {
				//Not our resource. Just get out of here.
				return;
			}
		}
		catch (final Throwable e) {
			logger.error("An exception is caught while trying to access JSON resource: ", e);
			return;
		}
		logger.debug("Received change event for JSON resource {}. Reloading services...", resourceChangedEvent.getResourceUri());
		synchronized (this.mutexMonitor) {
			loadServices();
			this.servicesManager.reload();
		}
	}

	/**
	 * Spring infrastructure class to support circular references DI of ReloadableServicesManager and JsonServiceRegistryDao
	 * required to make real-time reloading behavior work with disabling the default CAS periodic polling
	 * reloading behavior which avoids unnecessary additional reloading.
	 */
	@Component
	public static class ServicesManagerInjectableBeanPostProcessor implements BeanFactoryPostProcessor {
		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
			ReloadableServicesManager servicesManager;
			JsonServiceRegistryDao serviceRegistryDao;
			try {
				servicesManager = beanFactory.getBean(ReloadableServicesManager.class);
				serviceRegistryDao = beanFactory.getBean(JsonServiceRegistryDao.class);
			}
			catch (NoSuchBeanDefinitionException e) {
				return;
			}
			serviceRegistryDao.setServicesManager(servicesManager);
		}
	}
}
