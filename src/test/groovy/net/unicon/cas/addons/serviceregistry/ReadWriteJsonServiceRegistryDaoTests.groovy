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
        registeredServices = buildServices()
    }

    def "Test service registry delete operations"() {
        when: "Loading services should correctly reflect the status of the removed service"
        saveServices(registeredServices)

        then:
        dao.delete(registeredServices.get(0))

        and:
        dao.loadServices().size() == registeredServices.size() - 1

        where:
        registeredServices = buildServices()
    }

    def saveServices(registeredServices) {
        resource.file.delete()
        for (s in registeredServices) {
            dao.save(s)
        }
    }

    def buildServices() {
        def registeredServices = new ArrayList<RegisteredServiceWithAttributesImpl>()
        for (i in 0..2) {
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
            registeredServices.add(reg)
        }
        return registeredServices
    }
}
