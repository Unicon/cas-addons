package net.unicon.cas.addons.serviceregistry

import org.jasig.cas.services.InMemoryServiceRegistryDaoImpl
import org.jasig.cas.services.RegisteredService
import org.jasig.cas.services.ServiceRegistryDao
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource

import com.fasterxml.jackson.databind.ObjectMapper


/**
 * In-memory implementation of <code>ServiceRegistryDao</code> that reads services definition from JSON configuration file at the Spring Application Context
 * initialization time. After un-marshaling services from JSON blob, delegates the storage and retrieval to <code>InMemoryServiceRegistryDaoImpl</code>
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.1
 */
class JsonServiceRegistryDao implements ServiceRegistryDao {
    private def logger = LoggerFactory.getLogger(JsonServiceRegistryDao.class)

    InMemoryServiceRegistryDaoImpl delegateServiceRegistryDao = new InMemoryServiceRegistryDaoImpl()

    //ObjectMapper is thread-safe once configured.
    // We don't need any fancy configuration here, so it's just fine to instantiate it here
    ObjectMapper objectMapper = new ObjectMapper()

    /**
     * A configuration file containing JSON representation of the Services attributes. REQUIRED.
     */
    Resource servicesConfigFile

    final def registeredServicesMutexMonitor = new Object()

    @Override
    RegisteredService save(RegisteredService registeredService) {
        return this.delegateServiceRegistryDao.save(registeredService)
    }

    @Override
    boolean delete(RegisteredService registeredService) {
        return this.delegateServiceRegistryDao.delete(registeredService)
    }

    @Override
    List<RegisteredService> load() {
        synchronized (this.registeredServicesMutexMonitor) {
            init()
            return this.delegateServiceRegistryDao.registeredServices
        }
    }

    @Override
    RegisteredService findServiceById(long id) {
        return this.delegateServiceRegistryDao.findServiceById(id)
    }

    void init() {
        def servicesCollection = this.objectMapper.readValue(servicesConfigFile.file, RegisteredServicesCollection.class)
        logger.debug("Reading JSON ServiceRegistry from ${servicesConfigFile.file}")

        //Cast the services to correct types
        def resolvedServices = []
        servicesCollection.services.each { i ->
                if(i.serviceId.startsWith("^")){
                    logger.debug("Detected Regex-based matching serviceId")
                    resolvedServices.add(i as RegexRegisteredServiceWithAttributes)
                } else {
                    logger.debug("Using standard ant-based serviceId")
                    resolvedServices.add(i as RegisteredServiceWithAttributesImpl)
                }
        }
        this.delegateServiceRegistryDao.registeredServices = resolvedServices
    }

    //Here to provide a top level 'container' for Jackson mapper
    static class RegisteredServicesCollection {
        List<Object> services
    }

}
