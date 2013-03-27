package net.unicon.cas.addons.config;

import com.github.inspektr.audit.support.Slf4jLoggingAuditTrailManager;
import net.unicon.cas.addons.serviceregistry.JsonServiceRegistryDao;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * TODO: DOCUMENT ME!
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.3
 */
public class CasNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("inspektr-log-files-audit-manager", new InspektrSlf4jAuditTrailManagerBeanDefinitionParser());
		registerBeanDefinitionParser("json-services-registry", new JsonServicesRegistryDaoBeanDefinitionParser());
	}

	/**
	 * Parses <pre><inspektr-log-files-audit-manager/></pre> elements into bean definitions of type {@link com.github.inspektr.audit.support.Slf4jLoggingAuditTrailManager}
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
}
