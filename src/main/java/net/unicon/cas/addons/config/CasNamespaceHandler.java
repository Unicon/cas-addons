package net.unicon.cas.addons.config;

import com.github.inspektr.audit.support.Slf4jLoggingAuditTrailManager;
import net.unicon.cas.addons.authentication.handler.StormpathAuthenticationHandler;
import net.unicon.cas.addons.authentication.internal.DefaultAuthenticationSupport;
import net.unicon.cas.addons.authentication.principal.StormpathPrincipalResolver;
import net.unicon.cas.addons.authentication.strong.yubikey.YubiKeyAuthenticationHandler;
import net.unicon.cas.addons.info.events.CentralAuthenticationServiceEventsPublishingAspect;
import net.unicon.cas.addons.persondir.JsonBackedComplexStubPersonAttributeDao;
import net.unicon.cas.addons.serviceregistry.JsonServiceRegistryDao;
import net.unicon.cas.addons.serviceregistry.ReadWriteJsonServiceRegistryDao;
import net.unicon.cas.addons.serviceregistry.ReloadableServicesManagerSuppressionAspect;
import net.unicon.cas.addons.serviceregistry.services.authorization.DefaultRegisteredServiceAuthorizer;
import net.unicon.cas.addons.serviceregistry.services.authorization.ServiceAuthorizationAction;
import net.unicon.cas.addons.serviceregistry.services.internal.DefaultRegisteredServicesPolicies;
import net.unicon.cas.addons.support.ResourceChangeDetectingEventNotifier;
import org.jasig.cas.adaptors.generic.AcceptUsersAuthenticationHandler;
import org.jasig.cas.adaptors.ldap.BindLdapAuthenticationHandler;
import org.jasig.cas.authentication.AuthenticationManagerImpl;
import org.jasig.cas.authentication.handler.support.HttpBasedServiceCredentialsAuthenticationHandler;
import org.jasig.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.HttpBasedServiceCredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentialsToPrincipalResolver;
import org.jasig.cas.monitor.HealthCheckMonitor;
import org.jasig.cas.monitor.MemoryMonitor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.*;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.List;

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
        registerBeanDefinitionParser("authentication-manager-with-stormpath-handler", new AuthenticationManagerWithStormpathHandlerBeanDefinitionParser());
        registerBeanDefinitionParser("service-authorization-action", new ServiceAuthorizationActionBeanDefinitionParser());
        registerBeanDefinitionParser("disable-default-registered-services-reloading", new ReloadableServicesManagerSuppressionAspectBeanDefinitionParser());
        registerBeanDefinitionParser("yubikey-authentication-handler", new YubikeyAuthenticationHandlerBeanDefinitionParser());
        registerBeanDefinitionParser("accept-users-authentication-handler", new AcceptUsersAuthenticationHandlerBeanDefinitionParser());
        registerBeanDefinitionParser("bind-ldap-authentication-handler", new BindLdapAuthenticationHandlerBeanDefinitionParser());
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
            return Boolean.valueOf(element.getAttribute("read-write")) ? ReadWriteJsonServiceRegistryDao.class : JsonServiceRegistryDao.class;
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

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AuthenticationManagerImpl.class)
                    .addPropertyValue("credentialsToPrincipalResolvers", principalResolversList)
                    .addPropertyValue("authenticationHandlers", authnHandlersList);

            final String metadataPopulatorsRef = element.getAttribute("metadata-populators");
            if (StringUtils.hasText(metadataPopulatorsRef)) {
                builder.addPropertyReference("authenticationMetaDataPopulators", metadataPopulatorsRef);
            }
            return builder.getBeanDefinition();
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

    /**
     * Parses <pre>authentication-manager-with-stormpath-handler</pre> elements into bean definitions of type {@link org.jasig.cas.authentication.AuthenticationManagerImpl}
     */
    @SuppressWarnings("unchecked")
    private static class AuthenticationManagerWithStormpathHandlerBeanDefinitionParser extends
            AbstractBeanDefinitionParser {

        @Override
        protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
            //Create authentication handlers
            AbstractBeanDefinition stormpathAuthnHandlerBd = BeanDefinitionBuilder.genericBeanDefinition(StormpathAuthenticationHandler.class)
                    .addConstructorArgValue(element.getAttribute("access-id"))
                    .addConstructorArgValue(element.getAttribute("secret-key"))
                    .addConstructorArgValue(element.getAttribute("application-id"))
                    .getBeanDefinition();

            AbstractBeanDefinition httpBasedAuthnHandlerBd = BeanDefinitionBuilder.genericBeanDefinition(HttpBasedServiceCredentialsAuthenticationHandler.class)
                    .addPropertyReference("httpClient", "httpClient")
                    .getBeanDefinition();

            //Create principal resolvers
            AbstractBeanDefinition stormpathPrincipalResolverBd = BeanDefinitionBuilder.genericBeanDefinition(StormpathPrincipalResolver.class)
                    .addConstructorArgValue(stormpathAuthnHandlerBd)
                    .getBeanDefinition();

            AbstractBeanDefinition httpBasedPrincipalResolverBd = BeanDefinitionBuilder.genericBeanDefinition(HttpBasedServiceCredentialsToPrincipalResolver.class)
                    .getBeanDefinition();

            //CredentialsToPrincipalResolvers list construction
            ManagedList principalResolversList = new ManagedList(2);
            principalResolversList.addAll(Arrays.asList(stormpathPrincipalResolverBd, httpBasedPrincipalResolverBd));

            //AuthenticationHandlers list construction
            ManagedList authnHandlersList = new ManagedList(2);
            authnHandlersList.addAll(Arrays.asList(httpBasedAuthnHandlerBd, stormpathAuthnHandlerBd));

            //Main authenticationManager bean
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
     * Parses <pre>service-authorization-action</pre> elements into bean definitions of type {@link net.unicon.cas.addons.serviceregistry.services.authorization.ServiceAuthorizationAction}
     */
    @SuppressWarnings("unchecked")
    private static class ServiceAuthorizationActionBeanDefinitionParser extends AbstractBeanDefinitionParser {

        @Override
        protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
            final String authorizerRef = element.getAttribute("authorizer");
            final BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(ServiceAuthorizationAction.class)
                    .addConstructorArgReference("servicesManager")
                    .addConstructorArgReference("ticketRegistry");

            if (StringUtils.hasText(authorizerRef)) {
                bdb.addConstructorArgReference(authorizerRef);
            }
            else {
                bdb.addConstructorArgValue(BeanDefinitionBuilder.genericBeanDefinition(DefaultRegisteredServiceAuthorizer.class).getBeanDefinition());
            }
            return bdb.getBeanDefinition();
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "serviceAuthorizationAction";
        }
    }

    /**
     * Parses <pre>disable-default-registered-services-reloading</pre> elements into bean definitions of type {@link ReloadableServicesManagerSuppressionAspect}
     */
    private static class ReloadableServicesManagerSuppressionAspectBeanDefinitionParser extends
            AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return ReloadableServicesManagerSuppressionAspect.class;
        }

        @Override
        protected boolean shouldGenerateId() {
            return true;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.setFactoryMethod("aspectOf").addPropertyValue("on", true);
        }
    }

    /**
     * Parses <pre>yubikey-authentication-handler</pre> elements into bean definitions of type {@link net.unicon.cas.addons.authentication.strong.yubikey.YubiKeyAuthenticationHandler}
     * with bean id of <strong>yubikeyAuthenticationHandler</strong>
     */
    private static class YubikeyAuthenticationHandlerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return YubiKeyAuthenticationHandler.class;
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "yubikeyAuthenticationHandler";
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.addConstructorArgValue(element.getAttribute("client-id"))
                    .addConstructorArgValue(element.getAttribute("secret-key"));

            final String accountRegistryRef = element.getAttribute("account-registry");
            if (StringUtils.hasText(accountRegistryRef)) {
                builder.addConstructorArgReference(accountRegistryRef);
            }
        }
    }

    /**
     * Parses <pre>accept-users-authentication-handler</pre> elements into bean definitions of type {@link org.jasig.cas.adaptors.generic.AcceptUsersAuthenticationHandler}
     */
    private static class AcceptUsersAuthenticationHandlerBeanDefinitionParser extends
            AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return AcceptUsersAuthenticationHandler.class;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            final List<Element> userElements = DomUtils.getChildElementsByTagName(element, "user");
            final ManagedMap<String, String> usersMap = new ManagedMap<String, String>(userElements.size());
            for (Element e : userElements) {
                usersMap.put(e.getAttribute("name"), e.getAttribute("password"));
            }
            builder.addPropertyValue("users", usersMap);
        }

        @Override
        protected boolean shouldGenerateIdAsFallback() {
            return true;
        }
    }

    /**
     * Parses <pre>bind-ldap-authentication-handler</pre> elements into bean definitions of type {@link org.jasig.cas.adaptors.ldap.BindLdapAuthenticationHandler}
     */
    private static class BindLdapAuthenticationHandlerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return BindLdapAuthenticationHandler.class;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            //Create and configure LdapContextSource bean
            final BeanDefinitionBuilder contextSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(LdapContextSource.class);
            contextSourceBuilder.addPropertyValue("userDn", element.getAttribute("user-dn"));
            contextSourceBuilder.addPropertyValue("password", element.getAttribute("password"));
            contextSourceBuilder.addPropertyValue("urls", StringUtils.commaDelimitedListToSet(element.getAttribute("urls")));
            contextSourceBuilder.addPropertyValue("pooled", element.getAttribute("is-pooled"));
            final Element ldapPropsElem = DomUtils.getChildElementByTagName(element, "ldap-properties");
            if (ldapPropsElem != null) {
                parseLdapProps(ldapPropsElem, contextSourceBuilder);
            }

            //Configure the main bean - BindLdapAuthenticationHandler
            builder.addPropertyValue("filter", element.getAttribute("filter"));
            builder.addPropertyValue("searchBase", element.getAttribute("search-base"));
            builder.addPropertyValue("ignorePartialResultException", element.getAttribute("ignore-partial-result-exception"));
            builder.addPropertyValue("contextSource", contextSourceBuilder.getBeanDefinition());
        }

        @Override
        protected boolean shouldGenerateIdAsFallback() {
            return true;
        }

        private static void parseLdapProps(Element element, BeanDefinitionBuilder contextSourceBuilder) {
            final List<Element> propElements = DomUtils.getChildElementsByTagName(element, "ldap-prop");
            final ManagedMap<String, String> propsMap = new ManagedMap<String, String>(propElements.size());
            for (Element e : propElements) {
                propsMap.put(e.getAttribute("key"), e.getAttribute("value"));
            }
            contextSourceBuilder.addPropertyValue("baseEnvironmentProperties", propsMap);
        }
    }
}
