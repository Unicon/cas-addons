package net.unicon.cas.addons.serviceregistry

import org.jasig.cas.services.RegisteredService
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import org.springframework.core.io.ClassPathResource
import spock.lang.Specification
/**
 * @author @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
@RunWith(Sputnik)
class JsonServiceRegistryDaoTests extends Specification {

    def resource = new ClassPathResource("net/unicon/cas/addons/config/servicesRegistry.conf")
    def dao = new ReadWriteJsonServiceRegistryDao(resource)

    def "Regex, ANT and normal service definitions must all be loaded successfully"() {
        when:
            def services = dao.loadServices();
        then:
            services.size() == 3
    }

    def buildServices() {
        def registeredServices = new ArrayList<RegisteredService>()

        registeredServices.add(buildService(new RegisteredServiceWithAttributesImpl(), "^http://www.serviceid1.edu\$", 1))
        registeredServices.add(buildService(new RegisteredServiceWithAttributesImpl(), "http://www.serviceid?.edu/**", 10))
        registeredServices.add(buildService(new RegisteredServiceWithAttributesImpl(), "http://www.serviceid1.edu", 1))

        return registeredServices
    }

    def RegisteredService buildService(svc, id, serviceId) {
        svc.serviceId = serviceId
        svc.id = id
        svc.name = "The name" + id
        svc.allowedAttributes = ["attr1, attr2, attr3"]
        svc.theme = "The theme"
        svc.description = "A very good description"
        svc.evaluationOrder = id
        svc.allowedToProxy = false
        svc.enabled = true
        svc.ignoreAttributes = true
        svc.extraAttributes =  [test:"test1"]

        return svc
    }
}
