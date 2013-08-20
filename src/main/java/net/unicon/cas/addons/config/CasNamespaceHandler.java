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
import net.unicon.cas.addons.serviceregistry.RegisteredServicesReloadDisablingBeanFactoryPostProcessor;
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
import org.springframework.beans.factory.config.BeanDefinition;
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
        registerBeanDefinitionParser("disable-default-registered-services-reloading", new RegisteredServicesReloadDisablingBFPPBeanDefinitionParser());
        registerBeanDefinitionParser("yubikey-authentication-handler", new YubikeyAuthenticationHandlerBeanDefinitionParser());
        registerBeanDefinitionParser("accept-users-authentication-handler", new AcceptUsersAuthenticationHandlerBeanDefinitionParser());
        registerBeanDefinitionParser("bind-ldap-authentication-handler", new BindLdapAuthenticationHandlerBeanDefinitionParser());
        registerBeanDefinitionParser("authentication-manager-with-accept-users-handler", new AuthenticationManagerWithAcceptUsersHandlerBeanDefinitionParser());
        registerBeanDefinitionParser("authentication-manager-with-bind-ldap-handler", new AuthenticationManagerWithBindLdapHandlerBeanDefinitionParser());
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
    private static class DefaultTestAuthenticationManagerBeanDefinitionParser extends
            AbstractDefaultAuthenticationManagerBeanDefinitionParser {

        @Override
        protected AbstractBeanDefinition createAuthenticatonManagerBeanDefinition(Element element, ParserContext parserContext, BeanDefinitionBuilder builder, ManagedList authenticationHandlersList) {
            authenticationHandlersList.add(BeanDefinitionBuilder.genericBeanDefinition(SimpleTestUsernamePasswordAuthenticationHandler.class)
                    .getBeanDefinition());

            final String metadataPopulatorsRef = element.getAttribute("metadata-populators");
            if (StringUtils.hasText(metadataPopulatorsRef)) {
                builder.addPropertyReference("authenticationMetaDataPopulators", metadataPopulatorsRef);
            }
            return builder.getBeanDefinition();
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
    private static class AuthenticationManagerWithStormpathHandlerBeanDefinitionParser extends AbstractDefaultAuthenticationManagerBeanDefinitionParser {

        @Override
        @SuppressWarnings("unchecked")
        protected AbstractBeanDefinition createAuthenticatonManagerBeanDefinition(Element element, ParserContext parserContext, BeanDefinitionBuilder authenticationManagerBuilder, ManagedList authenticationHandlersList) {
            BeanDefinition authnHandler = parserContext.getRegistry().getBeanDefinition("stormpathAuthenticationHandler");
            authenticationHandlersList.add(authnHandler);
            return authenticationManagerBuilder.getBeanDefinition();
        }

        @Override
        protected AbstractBeanDefinition getCustomPrincipalResolver(Element element, ParserContext parserContext) {
            AbstractBeanDefinition stormpathAuthnHandlerBd = BeanDefinitionBuilder.genericBeanDefinition(StormpathAuthenticationHandler.class)
                    .addConstructorArgValue(element.getAttribute("access-id"))
                    .addConstructorArgValue(element.getAttribute("secret-key"))
                    .addConstructorArgValue(element.getAttribute("application-id"))
                    .getBeanDefinition();

            AbstractBeanDefinition stormpathPrincipalResolverBd = BeanDefinitionBuilder.genericBeanDefinition(StormpathPrincipalResolver.class)
                    .addConstructorArgValue(stormpathAuthnHandlerBd)
                    .getBeanDefinition();

            parserContext.getRegistry().registerBeanDefinition("stormpathAuthenticationHandler", stormpathAuthnHandlerBd);
            return stormpathPrincipalResolverBd;
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
     * Parses <pre>disable-default-registered-services-reloading</pre> elements into bean definitions of type {@link RegisteredServicesReloadDisablingBeanFactoryPostProcessor}
     */
    private static class RegisteredServicesReloadDisablingBFPPBeanDefinitionParser extends
            AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return RegisteredServicesReloadDisablingBeanFactoryPostProcessor.class;
        }

        @Override
        protected boolean shouldGenerateId() {
            return true;
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
            builder.addPropertyValue("users", buildUsersMap(element));
        }

        @Override
        protected boolean shouldGenerateIdAsFallback() {
            return true;
        }
    }

    /**
     * Parses <pre>authentication-manager-with-accept-users-handler</pre> elements into bean definitions of type {@link org.jasig.cas.authentication.AuthenticationManagerImpl}
     */
    private static class AuthenticationManagerWithAcceptUsersHandlerBeanDefinitionParser extends
            AbstractDefaultAuthenticationManagerBeanDefinitionParser {

        @Override
        @SuppressWarnings("unchecked")
        protected AbstractBeanDefinition createAuthenticatonManagerBeanDefinition(Element element, ParserContext parserContext, BeanDefinitionBuilder authenticationManagerBuilder, ManagedList authenticationHandlersList) {
            BeanDefinitionBuilder authnHandlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(AcceptUsersAuthenticationHandler.class);
            authnHandlerBuilder.addPropertyValue("users", buildUsersMap(element));
            authenticationHandlersList.add(authnHandlerBuilder.getBeanDefinition());
            return authenticationManagerBuilder.getBeanDefinition();
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
            //Configure LdapContextSource and main BindLdapAuthenticationHandler beans
            final BeanDefinitionBuilder contextSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(LdapContextSource.class);
            parseBindLdapAuthenticationHandlerBeanDefinition(element, contextSourceBuilder, builder);
        }

        @Override
        protected boolean shouldGenerateIdAsFallback() {
            return true;
        }
    }

    /**
     * Parses <pre>authentication-manager-with-bind-ldap-handler</pre> elements into bean definitions of type {@link org.jasig.cas.authentication.AuthenticationManagerImpl}
     */
    private static class AuthenticationManagerWithBindLdapHandlerBeanDefinitionParser extends
            AbstractDefaultAuthenticationManagerBeanDefinitionParser {

        @Override
        @SuppressWarnings("unchecked")
        protected AbstractBeanDefinition createAuthenticatonManagerBeanDefinition(Element element, ParserContext parserContext, BeanDefinitionBuilder authenticationManagerBuilder, ManagedList authenticationHandlersList) {
            BeanDefinitionBuilder contextSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(LdapContextSource.class);
            BeanDefinitionBuilder bindLdapAuthnHandlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(BindLdapAuthenticationHandler.class);
            parseBindLdapAuthenticationHandlerBeanDefinition(element, contextSourceBuilder, bindLdapAuthnHandlerBuilder);
            authenticationHandlersList.add(bindLdapAuthnHandlerBuilder.getBeanDefinition());
            return authenticationManagerBuilder.getBeanDefinition();
        }
    }

    protected static abstract class AbstractDefaultAuthenticationManagerBeanDefinitionParser extends
            AbstractBeanDefinitionParser {

        @Override
        @SuppressWarnings("unchecked")
        protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
            final AbstractBeanDefinition httpBasedPrincipalResolverBd = BeanDefinitionBuilder.genericBeanDefinition(HttpBasedServiceCredentialsToPrincipalResolver.class)
                    .getBeanDefinition();

            final AbstractBeanDefinition httpBasedAuthnHandlerBd = BeanDefinitionBuilder.genericBeanDefinition(HttpBasedServiceCredentialsAuthenticationHandler.class)
                    .addPropertyReference("httpClient", "httpClient")
                    .getBeanDefinition();

            final ManagedList principalResolversList = new ManagedList();
            principalResolversList.add(httpBasedPrincipalResolverBd);
            AbstractBeanDefinition principalResolverBd = getCustomPrincipalResolver(element, parserContext);
            if (principalResolverBd == null) {
                principalResolverBd = BeanDefinitionBuilder.genericBeanDefinition(UsernamePasswordCredentialsToPrincipalResolver.class)
                        .addPropertyReference("attributeRepository", "attributeRepository")
                        .getBeanDefinition();
            }
            principalResolversList.add(principalResolverBd);

            final ManagedList authnHandlersList = new ManagedList();
            authnHandlersList.addAll(Arrays.asList(httpBasedAuthnHandlerBd));

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AuthenticationManagerImpl.class)
                    .addPropertyValue("credentialsToPrincipalResolvers", principalResolversList)
                    .addPropertyValue("authenticationHandlers", authnHandlersList);

            return createAuthenticatonManagerBeanDefinition(element, parserContext, builder, authnHandlersList);
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
            return "authenticationManager";
        }

        protected AbstractBeanDefinition getCustomPrincipalResolver(Element element, ParserContext parserContext) {
            return null;
        }

        protected abstract AbstractBeanDefinition createAuthenticatonManagerBeanDefinition(Element element, ParserContext parserContext, BeanDefinitionBuilder authenticationManagerBuilder,
                                                                                           ManagedList authenticationHandlersList);
    }

    private static ManagedMap<String, String> buildUsersMap(Element element) {
        final List<Element> userElements = DomUtils.getChildElementsByTagName(element, "user");
        final ManagedMap<String, String> usersMap = new ManagedMap<String, String>(userElements.size());
        for (Element e : userElements) {
            usersMap.put(e.getAttribute("name"), e.getAttribute("password"));
        }
        return usersMap;
    }

    private static void parseBindLdapAuthenticationHandlerBeanDefinition(Element element, BeanDefinitionBuilder contextSourceBuilder, BeanDefinitionBuilder bindLdapAuthnHandlerBuilder) {
        contextSourceBuilder.addPropertyValue("userDn", element.getAttribute("user-dn"));
        contextSourceBuilder.addPropertyValue("password", element.getAttribute("password"));
        contextSourceBuilder.addPropertyValue("urls", StringUtils.commaDelimitedListToSet(element.getAttribute("urls")));
        contextSourceBuilder.addPropertyValue("pooled", element.getAttribute("is-pooled"));

        final Element ldapPropsElem = DomUtils.getChildElementByTagName(element, "ldap-properties");
        if (ldapPropsElem != null) {
            final List<Element> propElements = DomUtils.getChildElementsByTagName(element, "ldap-prop");
            final ManagedMap<String, String> propsMap = new ManagedMap<String, String>(propElements.size());
            for (Element e : propElements) {
                propsMap.put(e.getAttribute("key"), e.getAttribute("value"));
            }
            contextSourceBuilder.addPropertyValue("baseEnvironmentProperties", propsMap);
        }

        bindLdapAuthnHandlerBuilder.addPropertyValue("filter", element.getAttribute("filter"));
        bindLdapAuthnHandlerBuilder.addPropertyValue("searchBase", element.getAttribute("search-base"));
        bindLdapAuthnHandlerBuilder.addPropertyValue("ignorePartialResultException", element.getAttribute("ignore-partial-result-exception"));
        bindLdapAuthnHandlerBuilder.addPropertyValue("contextSource", contextSourceBuilder.getBeanDefinition());
    }
}
