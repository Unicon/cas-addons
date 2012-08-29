package net.unicon.cas.addons.serviceregistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.unicon.cas.addons.support.GuardedBy;
import net.unicon.cas.addons.support.ResourceChangeDetectingEventNotifier;
import net.unicon.cas.addons.support.ThreadSafe;
import org.jasig.cas.services.InMemoryServiceRegistryDaoImpl;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServiceRegistryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public final class JsonServiceRegistryDao implements ServiceRegistryDao,
		ApplicationListener<ResourceChangeDetectingEventNotifier.ResourceChangedEvent> {

	@GuardedBy("mutexMonitor")
	private final InMemoryServiceRegistryDaoImpl delegateServiceRegistryDao = new InMemoryServiceRegistryDaoImpl();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Resource servicesConfigFile;

	private final Object mutexMonitor = new Object();

	private static final String REGEX_PREFIX = "^";

	private static final String SERVICES_KEY = "services";

	private static final String SERVICES_ID_KEY = "serviceId";

	private static final Logger logger = LoggerFactory.getLogger(JsonServiceRegistryDao.class);

	public JsonServiceRegistryDao(Resource servicesConfigFile) {
		this.servicesConfigFile = servicesConfigFile;
	}

	@Override
	public RegisteredService save(RegisteredService registeredService) {
		synchronized (this.mutexMonitor) {
			return this.delegateServiceRegistryDao.save(registeredService);
		}
	}

	@Override
	public boolean delete(RegisteredService registeredService) {
		synchronized (this.mutexMonitor) {
			return this.delegateServiceRegistryDao.delete(registeredService);
		}
	}

	@Override
	public RegisteredService findServiceById(long id) {
		synchronized (this.mutexMonitor) {
			return this.delegateServiceRegistryDao.findServiceById(id);
		}
	}

	@Override
	public List<RegisteredService> load() {
		synchronized (this.mutexMonitor) {
			return this.delegateServiceRegistryDao.load();
		}

	}

	/**
	 * This method is used as a Spring bean loadServices-method
	 * as well as the reloading method when the change in the services definition resource is detected at runtime
	 */
	@SuppressWarnings("unchecked")
	public void loadServices() {
		logger.info("Loading Registered Services from: [ {} ]...", this.servicesConfigFile);
		try {
			final List<RegisteredService> resolvedServices = new ArrayList<RegisteredService>();
			final Map<String, List> m = this.objectMapper.readValue(this.servicesConfigFile.getFile(), Map.class);
			final Iterator<Map> i = m.get(SERVICES_KEY).iterator();
			while (i.hasNext()) {
				Map<?, ?> record = i.next();
				if (((String) record.get(SERVICES_ID_KEY)).startsWith(REGEX_PREFIX)) {
					resolvedServices.add(this.objectMapper.convertValue(record, RegexRegisteredServiceWithAttributes.class));
					logger.debug("Unmarshaled RegexRegisteredServiceWithAttributes: {}", record);
				}
				else {
					resolvedServices.add(this.objectMapper.convertValue(record, RegisteredServiceWithAttributesImpl.class));
					logger.debug("Unmarshaled RegisteredServiceWithAttributes: {}", record);
				}
			}
			synchronized (this.mutexMonitor) {
				this.delegateServiceRegistryDao.setRegisteredServices(resolvedServices);
			}
		}
		catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onApplicationEvent(ResourceChangeDetectingEventNotifier.ResourceChangedEvent resourceChangedEvent) {
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
		loadServices();
	}
}

