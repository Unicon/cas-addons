package net.unicon.cas.addons.serviceregistry

import org.jasig.cas.services.ServiceRegistryDao
import org.jasig.cas.services.RegisteredService
import org.jasig.cas.services.InMemoryServiceRegistryDaoImpl
import org.springframework.core.io.Resource
import org.codehaus.jackson.map.ObjectMapper
import org.jasig.cas.services.RegisteredServiceImpl

/**
 * In-memory implementation of <code>ServiceRegistryDao</code> that reads services definition from JSON configuration file at the Spring Application Context
 * initialization time. After un-marshaling services from JSON blob, delegates the storage and retrieval to <code>InMemoryServiceRegistryDaoImpl</code>
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.1
 */
class JsonServiceRegistryDao implements ServiceRegistryDao {

    InMemoryServiceRegistryDaoImpl delegateServiceRegistryDao = new InMemoryServiceRegistryDaoImpl()

    //org.codehaus.jackson.map.ObjectMapper is thread-safe once configured.
    // We don't need any fancy configuration here, so it's just fine to instantiate it here
    ObjectMapper objectMapper = new ObjectMapper()

    /**
     * A configuration file containing JSON representation of the Services attributes. REQUIRED.
     */
    Resource servicesConfigFile;

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
        this.delegateServiceRegistryDao.registeredServices = servicesCollection.services
    }

    //Here to provide a top level 'container' for Jackson mapper
    static class RegisteredServicesCollection {
        List<RegisteredServiceWithAttributesImpl> services
    }

}
