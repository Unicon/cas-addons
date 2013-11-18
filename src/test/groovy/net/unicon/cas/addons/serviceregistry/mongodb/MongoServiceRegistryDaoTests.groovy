package net.unicon.cas.addons.serviceregistry.mongodb

import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributesImpl

import org.jasig.cas.services.ServiceRegistryDao
import org.junit.runner.RunWith;
import org.spockframework.runtime.Sputnik;
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.core.io.ClassPathResource
import org.springframework.web.context.support.XmlWebApplicationContext;

import spock.lang.Ignore;
import spock.lang.Specification;

@RunWith(Sputnik)
class MongoServiceRegistryDaoTests extends Specification {
    def context = new ClassPathXmlApplicationContext("mongo-context.xml");
    
    @Ignore
    def "Test saving services into mongodb database"() {
        when:
            final ServiceRegistryDao reg = context.getBean("serviceRegistryDao")  
            for (i in 1..3) {
                def id = i % 2 == 0 ? i : -1
                reg.save(buildService(id))
            }    
        then:
            reg.load().size() == 2
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
