package net.unicon.cas.addons.config;

import net.unicon.cas.addons.serviceregistry.JsonServiceRegistryDao;
import net.unicon.cas.addons.serviceregistry.ReadWriteJsonServiceRegistryDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CasNamespaceReadWriteJsonServiceRegistryDaoParserTests {

    @Autowired
    ApplicationContext applicationContext;

    private static final String SERVICE_REGISTRY_DAO_BEAN_NAME = "serviceRegistryDao";

    @Test
    public void readWriteJsonServiceRegistryDaoBeanDefinitionCorrectlyParsed() {
        assertTrue(applicationContext.containsBean(SERVICE_REGISTRY_DAO_BEAN_NAME));
        assertTrue(applicationContext.getBeansOfType(ReadWriteJsonServiceRegistryDao.class).size() == 1);
    }
}
