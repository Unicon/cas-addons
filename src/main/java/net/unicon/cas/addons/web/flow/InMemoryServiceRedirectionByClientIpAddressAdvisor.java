/**
 * 
 */
package net.unicon.cas.addons.web.flow;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.unicon.cas.addons.serviceregistry.RegisteredServiceWithAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.RequestContext;

/**
 * An extension of the {@link ServiceRedirectionAdvisor} that bases the calculation of url redirection off
 * of the client remote address and port. If the remote address, port, service or the redirect url change, then
 * this component would indicate that the interruption is required. Otherwise, proceeds as normal.
 * 
 * <p>State data is kept in memory only.</p>
 * 
 * @author Misagh Moayyed (<a href="mailto:mmoayyed@unicon.net">mmoayyed@unicon.net</a>)
 * @since 1.9
 */
public final class InMemoryServiceRedirectionByClientIpAddressAdvisor implements ServiceRedirectionAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryServiceRedirectionByClientIpAddressAdvisor.class);
    
    private final Set<String> repository = new HashSet<String>();
  
    @Override
    public boolean shouldRedirectServiceRequest(final RequestContext context,
                                                final RegisteredServiceWithAttributes service,
                                                final String redirectUrl) {
        
        final String key = buildKey(context, service, redirectUrl);
        
        if (this.repository.contains(key)) {
            logger.info("Request [{}] has fulfilled redirection requirements for service id [{}]", key, service.getServiceId());
            return false;
        }
        
        logger.info("Before granting authentication request for [{}], request must be redirected to [{}].", key, redirectUrl);
        return this.repository.add(key);
    }

    private String buildKey(final RequestContext context, final RegisteredServiceWithAttributes service, final String redirectUrl) {
        final HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getNativeRequest();
        return request.getRemoteAddr() + "-" + request.getRemotePort() + "-" + service.hashCode() + "-" + redirectUrl;
    }

}
