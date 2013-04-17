package net.unicon.cas.addons.config;

import com.github.inspektr.audit.support.Slf4jLoggingAuditTrailManager;
import net.unicon.cas.addons.authentication.handler.StormpathAuthenticationHandler;
import net.unicon.cas.addons.authentication.internal.DefaultAuthenticationSupport;
import net.unicon.cas.addons.info.events.CentralAuthenticationServiceEventsPublishingAspect;
import net.unicon.cas.addons.persondir.JsonBackedComplexStubPersonAttributeDao;
import net.unicon.cas.addons.serviceregistry.JsonServiceRegistryDao;
import net.unicon.cas.addons.serviceregistry.services.internal.DefaultRegisteredServicesPolicies;
import net.unicon.cas.addons.support.ResourceChangeDetectingEventNotifier;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.AuthenticationManagerImpl;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.handler.support.HttpBasedServiceCredentialsAuthenticationHandler;
import org.jasig.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.HttpBasedServiceCredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentialsToPrincipalResolver;
import org.jasig.cas.monitor.HealthCheckMonitor;
import org.jasig.cas.monitor.MemoryMonitor;
import org.jasig.cas.monitor.Monitor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.*;
import org.w3c.dom.Element;

import java.util.Arrays;

/**
 * {@link NamespaceHandler} for convenient CAS configuration namespace.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.3
 */
public class CasNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("inspektr-log-files-audit-manager", new InspektrSlf4jAuditTrailManagerBeanDefinitionParser());
        registerBeanDefinitionParser("json-services-registry", new JsonServicesRegistryDaoBeanDefinitionParser());
        registerBeanDefinitionParser("resource-change-detector", new ResourceChangeDetectingEventNotifierBeanDefinitionParser());
        registerBeanDefinitionParser("default-authentication-support", new DefaultAuthenticationSupportBeanDefinitionParser());
        registerBeanDefinitionParser("default-events-publisher", new DefaultEventsPublisherBeanDefinitionParser());
        registerBeanDefinitionParser("default-registered-services-policies", new DefaultRegisteredServicesPoliciesBeanDefinitionParser());
        registerBeanDefinitionParser("default-health-check-monitor", new DefaultHealthCheckMonitorBeanDefinitionParser());
        registerBeanDefinitionParser("default-test-authentication-manager", new DefaultTestAuthenticationManagerBeanDefinitionParser());
        registerBeanDefinitionParser("json-attribute-repository", new JsonAttributesRepositoryBeanDefinitionParser());
        registerBeanDefinitionParser("stormpath-authentication-handler", new StormpathAuthenticationHandlerBeanDefinitionParser());
    }

    /**
     * Parses <pre>inspektr-log-files-audit-manager</pre> elements into bean definitions of type {@link com.github.inspektr.audit.support.Slf4jLoggingAuditTrailManager}
     */
    private static class InspektrSlf4jAuditTrailManagerBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return Slf4jLoggingAuditTrailManager.class;
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "auditTrailManager";
        }
    }

    /**
     * Parses <pre>json-services-registry</pre> elements into bean definitions of type {@link JsonServiceRegistryDao}
     */
    private static class JsonServicesRegistryDaoBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return JsonServiceRegistryDao.class;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.addConstructorArgValue(element.getAttribute("config-file"));
            builder.setInitMethodName("loadServices");
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "serviceRegistryDao";
        }
    }

    /**
     * Parses <pre>json-attribute-repository</pre> elements into bean definitions of type {@link net.unicon.cas.addons.persondir.JsonBackedComplexStubPersonAttributeDao}
     */
    private static class JsonAttributesRepositoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return JsonBackedComplexStubPersonAttributeDao.class;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.addConstructorArgValue(element.getAttribute("config-file"));
            builder.setInitMethodName("init");
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "attributeRepository";
        }
    }

    /**
     * Parses <pre>resource-change-detector</pre> elements into bean definitions of type {@link ResourceChangeDetectingEventNotifier}
     */
    private static class ResourceChangeDetectingEventNotifierBeanDefinitionParser extends
            AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return ResourceChangeDetectingEventNotifier.class;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.addConstructorArgValue(element.getAttribute("watched-resource"));
        }
    }

    /**
     * Parses <pre>default-authentication-support</pre> elements into bean definitions of type {@link DefaultAuthenticationSupport}
     */
    private static class DefaultAuthenticationSupportBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return DefaultAuthenticationSupport.class;
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "authenticationSupport";
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.addConstructorArgReference("ticketRegistry");
        }
    }

    /**
     * Parses <pre>default-events-publisher</pre> elements into bean definitions of type {@link net.unicon.cas.addons.info.events.CentralAuthenticationServiceEventsPublishingAspect}
     */
    private static class DefaultEventsPublisherBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return CentralAuthenticationServiceEventsPublishingAspect.class;
        }

        @Override
        protected boolean shouldGenerateId() {
            return true;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.addConstructorArgReference("authenticationSupport");
        }
    }

    /**
     * Parses <pre>default-registered-services-policies</pre> elements into bean definitions of type {@link net.unicon.cas.addons.serviceregistry.services.internal.DefaultRegisteredServicesPolicies}
     */
    private static class DefaultRegisteredServicesPoliciesBeanDefinitionParser extends
            AbstractSimpleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return DefaultRegisteredServicesPolicies.class;
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "registeredServicesPolicies";
        }
    }

    /**
     * Parses <pre>default-health-check-monitor</pre> elements into bean definitions of type {@link org.jasig.cas.monitor.HealthCheckMonitor}
     */
    @SuppressWarnings("unchecked")
    private static class DefaultHealthCheckMonitorBeanDefinitionParser extends AbstractBeanDefinitionParser {

        @Override
        protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
            AbstractBeanDefinition memoryMonitorBd = BeanDefinitionBuilder.genericBeanDefinition(MemoryMonitor.class)
                    .addPropertyValue("freeMemoryWarnThreshold", 10).getBeanDefinition();

            ManagedList monitorsList = new ManagedList(1);
            monitorsList.add(memoryMonitorBd);

            return BeanDefinitionBuilder.genericBeanDefinition(HealthCheckMonitor.class)
                    .addPropertyValue("monitors", monitorsList)
                    .getBeanDefinition();
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "healthCheckMonitor";
        }
    }

    /**
     * Parses <pre>default-test-authentication-manager</pre> elements into bean definitions of type {@link org.jasig.cas.authentication.AuthenticationManagerImpl}
     */
    @SuppressWarnings("unchecked")
    private static class DefaultTestAuthenticationManagerBeanDefinitionParser extends AbstractBeanDefinitionParser {

        @Override
        protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
            //CredentialsToPrincipalResolvers list construction
            AbstractBeanDefinition usernamePasswdPrincipalResolverBd = BeanDefinitionBuilder.genericBeanDefinition(UsernamePasswordCredentialsToPrincipalResolver.class)
                    .addPropertyReference("attributeRepository", "attributeRepository")
                    .getBeanDefinition();

            AbstractBeanDefinition httpBasedPrincipalResolverBd = BeanDefinitionBuilder.genericBeanDefinition(HttpBasedServiceCredentialsToPrincipalResolver.class)
                    .getBeanDefinition();

            ManagedList principalResolversList = new ManagedList(2);
            principalResolversList.addAll(Arrays.asList(usernamePasswdPrincipalResolverBd, httpBasedPrincipalResolverBd));

            //AuthenticationHandlers list construction
            AbstractBeanDefinition httpBasedAuthnHandlerBd = BeanDefinitionBuilder.genericBeanDefinition(HttpBasedServiceCredentialsAuthenticationHandler.class)
                    .addPropertyReference("httpClient", "httpClient")
                    .getBeanDefinition();

            AbstractBeanDefinition simpleTestAuthnHandlerBd = BeanDefinitionBuilder.genericBeanDefinition(SimpleTestUsernamePasswordAuthenticationHandler.class)
                    .getBeanDefinition();

            ManagedList authnHandlersList = new ManagedList(2);
            authnHandlersList.addAll(Arrays.asList(httpBasedAuthnHandlerBd, simpleTestAuthnHandlerBd));

            return BeanDefinitionBuilder.genericBeanDefinition(AuthenticationManagerImpl.class)
                    .addPropertyValue("credentialsToPrincipalResolvers", principalResolversList)
                    .addPropertyValue("authenticationHandlers", authnHandlersList)
                    .getBeanDefinition();
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "authenticationManager";
        }
    }

    /**
     * Parses <pre>stormpath-authentication-handler</pre> elements into bean definitions of type {@link StormpathAuthenticationHandler}
     * with bean id of <strong>stormpathAuthenticationHandler</strong>
     */
    private static class StormpathAuthenticationHandlerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return StormpathAuthenticationHandler.class;
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "stormpathAuthenticationHandler";
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.addConstructorArgValue(element.getAttribute("access-id"))
                    .addConstructorArgValue(element.getAttribute("secret-key"))
                    .addConstructorArgValue(element.getAttribute("application-id"));
        }
    }
}
