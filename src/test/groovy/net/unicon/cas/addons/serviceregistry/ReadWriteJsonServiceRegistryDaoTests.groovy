package net.unicon.cas.addons.serviceregistry

import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import org.springframework.core.io.FileSystemResource
import spock.lang.Specification
/**
 * @author @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
@RunWith(Sputnik)
class ReadWriteJsonServiceRegistryDaoTests extends Specification {
    def TEMP_DIR = (String) System.getProperties().get("java.io.tmpdir")
    def resource = new FileSystemResource(new File(TEMP_DIR, "svc.tmp"))
    def dao = new ReadWriteJsonServiceRegistryDao(resource)

    def "Test service registry write operations"() {
        when: "Number of loaded services should equal the number of saved services"
        saveServices(registeredServices)

        then:
        dao.loadServices().size() == registeredServices.size()

        where:
        registeredServices = buildServices(0..2)
    }

    def "Test service registry update operations"() {
        given: "When adding services to the list that have the same identifier"
        when: "Services with the same id should be updated in the list of loaded services"
        saveServices(registeredServicesOne) && saveServices(registeredServicesTwo)

        then:
        dao.loadServices().size() == 3

        where:
        registeredServicesOne = buildServices(0..2)
        registeredServicesTwo = buildServices(1..3)
    }

    def "Test saving a registered service without an explicit identifier"() {
        given: "When adding services to the list with no ids"
        when: "The service registry should auto-assign identifiers to services"
        saveServices(registeredService)

        then:
        dao.loadServices().size() == 1

        and:
        dao.loadServices().get(0).getId() != -1

        where:
           registeredService = Collections.singletonList(buildService(-1))
    }

    def "Test service registry delete operations"() {
        when: "Loading services should correctly reflect the status of the removed service"
        saveServices(registeredServices)

        then:
        dao.delete(registeredServices.get(0))

        and:
        dao.loadServices().size() == registeredServices.size() - 1

        where:
        registeredServices = buildServices(0..2)
    }

    def saveServices(registeredServices) {
        resource.file.delete()
        for (s in registeredServices) {
            dao.save(s)
        }
    }

    def buildServices(range) {
        def registeredServices = new ArrayList<RegisteredServiceWithAttributesImpl>()
        for (i in range) {
            def reg = buildService(i)
            registeredServices.add(reg)
        }
        return registeredServices
    }

    def buildService(def i) {
        def reg = new RegisteredServiceWithAttributesImpl()
        reg.serviceId = "^http://www.serviceid" + i + ".edu"
        reg.id = i
        reg.name = "The name" + i
        reg.allowedAttributes = ["attr1, attr2, attr3"]
        reg.theme = "The theme"
        reg.description = "A very good description"
        reg.evaluationOrder = i
        reg.allowedToProxy = false
        reg.enabled = true
        reg.ignoreAttributes = true

        def extraAttr = new HashMap()
        extraAttr.put("test", "test1")

        reg.extraAttributes = extraAttr
        return reg
    }
}
