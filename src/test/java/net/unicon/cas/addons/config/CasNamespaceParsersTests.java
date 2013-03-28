package net.unicon.cas.addons.config;

import static junit.framework.Assert.*;

import com.github.inspektr.audit.support.Slf4jLoggingAuditTrailManager;
import net.unicon.cas.addons.serviceregistry.JsonServiceRegistryDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CasNamespaceParsersTests {

	@Autowired
	ApplicationContext applicationContext;

	@Test
	public void slf4jAuditTrailManagerBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.containsBean("auditTrailManager"));
		assertTrue(applicationContext.getBeansOfType(Slf4jLoggingAuditTrailManager.class).size() == 1);
	}

	@Test
	public void jsonServiceRegistryDaoBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.containsBean("serviceRegistryDao"));
		assertTrue(applicationContext.getBeansOfType(JsonServiceRegistryDao.class).size() == 1);
	}
}
