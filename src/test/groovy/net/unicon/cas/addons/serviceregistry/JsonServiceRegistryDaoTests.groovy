package net.unicon.cas.addons.serviceregistry

import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.UrlResource
import spock.lang.Specification
/**
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
@RunWith(Sputnik)
class JsonServiceRegistryDaoTests extends Specification {

    def "Regex, ANT and normal service definitions must all be loaded successfully"() {
        when:
            def resource = new ClassPathResource("net/unicon/cas/addons/config/servicesRegistry.conf")
            def services = loadServices(resource)
        then:
            services.size() == 3
    }
    
    def "Loading a non-existing resource url"() {
        when:
            def resource = new FileSystemResource("http://localhost:8080/servicesRegistry.conf")
            def services = loadServices(resource)
        then:
            services.size() == 0
    }
    
    def "Loading a non-existing resource file"() {
        when:
            def resource = new FileSystemResource("NonExistingJsonFile.conf")
            def services = loadServices(resource)
        then:
            services.size() == 0
    }
    
    def "Service definitions are loaded from a URL"() {
        when:
            def resource = new UrlResource("https://raw.github.com/Unicon/cas-addons/master/src/test/resources/net/unicon/cas/addons/config/servicesRegistry.conf")
            def services = loadServices(resource)
        then:
            services.size() == 3
    }
    
    def loadServices(resource) {
        def dao = new JsonServiceRegistryDao(resource)
        return dao.loadServices();
    }
}
