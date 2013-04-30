package net.unicon.cas.addons.config;

import static org.junit.Assert.*;

import com.github.inspektr.audit.support.Slf4jLoggingAuditTrailManager;
import net.unicon.cas.addons.authentication.internal.DefaultAuthenticationSupport;
import net.unicon.cas.addons.info.events.CentralAuthenticationServiceEventsPublishingAspect;
import net.unicon.cas.addons.persondir.JsonBackedComplexStubPersonAttributeDao;
import net.unicon.cas.addons.serviceregistry.JsonServiceRegistryDao;
import net.unicon.cas.addons.serviceregistry.services.authorization.ServiceAuthorizationAction;
import net.unicon.cas.addons.serviceregistry.services.internal.DefaultRegisteredServicesPolicies;
import net.unicon.cas.addons.support.ResourceChangeDetectingEventNotifier;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.monitor.HealthCheckMonitor;
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

	private static final String AUDIT_TRAIL_MANAGER_BEAN_NAME = "auditTrailManager";

	private static final String SERVICE_REGISTRY_DAO_BEAN_NAME = "serviceRegistryDao";

	private static final String RESOURCE_CHANGE_DETECTOR_BEAN_NAME = "testChangeDetector";

	private static final String AUTHENTICATION_SUPPORT_BEAN_NAME = "authenticationSupport";

	private static final String REGISTERED_SERVICES_POLICIES_BEAN_NAME = "registeredServicesPolicies";

	private static final String HEALTH_CHECK_MONITOR_BEAN_NAME = "healthCheckMonitor";

	private static final String AUTHENTICATION_MANAGER_BEAN_NAME = "authenticationManager";

	private static final String ATTRIBUTE_REPOSITORY_BEAN_NAME = "attributeRepository";

    private static final String SERVICE_AUTHORIZATION_ACTION_BEAN_NAME = "serviceAuthorizationAction";

	@Test
	public void slf4jAuditTrailManagerBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.containsBean(AUDIT_TRAIL_MANAGER_BEAN_NAME));
		assertTrue(applicationContext.getBeansOfType(Slf4jLoggingAuditTrailManager.class).size() == 1);
	}

	@Test
	public void jsonServiceRegistryDaoBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.containsBean(SERVICE_REGISTRY_DAO_BEAN_NAME));
		assertTrue(applicationContext.getBeansOfType(JsonServiceRegistryDao.class).size() == 1);
	}

	@Test
	public void jsonAttributeRepositoryBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.containsBean(ATTRIBUTE_REPOSITORY_BEAN_NAME));
		assertTrue(applicationContext.getBeansOfType(JsonBackedComplexStubPersonAttributeDao.class).size() == 1);
	}

	@Test
	public void resourceChangeDetectingEventNotifierBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.containsBean(RESOURCE_CHANGE_DETECTOR_BEAN_NAME));
		assertTrue(applicationContext.getBeansOfType(ResourceChangeDetectingEventNotifier.class).size() == 1);
	}

	@Test
	public void defaultAuthenticationSupportBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.containsBean(AUTHENTICATION_SUPPORT_BEAN_NAME));
		assertTrue(applicationContext.getBeansOfType(DefaultAuthenticationSupport.class).size() == 1);
	}

	@Test
	public void defaultEventsPublisherBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.getBeansOfType(CentralAuthenticationServiceEventsPublishingAspect.class).size() == 1);
	}

	@Test
	public void defaultRegisteredServicesPoliciesBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.containsBean(REGISTERED_SERVICES_POLICIES_BEAN_NAME));
		assertTrue(applicationContext.getBeansOfType(DefaultRegisteredServicesPolicies.class).size() == 1);
	}

	@Test
	public void defaultHealthCheckMonitorBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.containsBean(HEALTH_CHECK_MONITOR_BEAN_NAME));
		assertTrue(applicationContext.getBeansOfType(HealthCheckMonitor.class).size() == 1);
	}

	@Test
	public void defaultTestAuthenticationManagerBeanDefinitionCorrectlyParsed() {
		assertTrue(applicationContext.containsBean(AUTHENTICATION_MANAGER_BEAN_NAME));
		assertTrue(applicationContext.getBeansOfType(AuthenticationManager.class).size() == 1);
	}

    @Test
    public void serviceAuthorizationActionBeanDefinitionCorrectlyParsed() {
        assertTrue(applicationContext.containsBean(SERVICE_AUTHORIZATION_ACTION_BEAN_NAME));
        assertTrue(applicationContext.getBeansOfType(ServiceAuthorizationAction.class).size() == 1);
    }
}
