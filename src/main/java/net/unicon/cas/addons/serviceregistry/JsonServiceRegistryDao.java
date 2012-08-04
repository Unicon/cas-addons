package net.unicon.cas.addons.serviceregistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.cas.services.InMemoryServiceRegistryDaoImpl;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ReloadableServicesManager;
import org.jasig.cas.services.ServiceRegistryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of <code>ServiceRegistryDao</code> that reads services definition from JSON configuration file at the Spring Application Context
 * initialization time. After un-marshaling services from JSON blob, delegates the storage and retrieval to <code>InMemoryServiceRegistryDaoImpl</code>
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.8
 */
public final class JsonServiceRegistryDao implements ServiceRegistryDao {

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
		return this.delegateServiceRegistryDao.save(registeredService);
	}

	@Override
	public boolean delete(RegisteredService registeredService) {
		return this.delegateServiceRegistryDao.delete(registeredService);
	}

	@Override
	public RegisteredService findServiceById(long id) {
		return this.delegateServiceRegistryDao.findServiceById(id);
	}


	/**
	 * Also use this method as a Spring bean init-method
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RegisteredService> load() {
		logger.info("Loading Registered Services from: [ {} ]...", this.servicesConfigFile);
		try {
			final List<RegisteredService> resolvedServices = new ArrayList<RegisteredService>();
			final Map<String, List> m = this.objectMapper.readValue(this.servicesConfigFile.getFile(), Map.class);
			final Iterator<Map> i = m.get(SERVICES_KEY).iterator();
			while (i.hasNext()) {
				Map record = i.next();
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
				return resolvedServices;
			}
		}
		catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
