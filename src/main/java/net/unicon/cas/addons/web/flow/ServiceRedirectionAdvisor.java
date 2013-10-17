/**
 * 
 */
package net.unicon.cas.addons.web.flow;


import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;

import org.springframework.webflow.execution.RequestContext;

/**
 * Strategy interface to define whether a service request first requires a url redirection.
 * @author Misagh Moayyed (<a href="mailto:mmoayyed@unicon.net">mmoayyed@unicon.net</a>)
 * @since 1.9
 * @see InMemoryServiceRedirectionByClientIpAddressAdvisor
 */
public interface ServiceRedirectionAdvisor {
    boolean shouldRedirectServiceRequest(final RequestContext context, final RegisteredServiceWithAttributes service, final String redirectToUrl);
}
