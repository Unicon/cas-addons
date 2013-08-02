package net.unicon.cas.addons.serviceregistry

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
}
