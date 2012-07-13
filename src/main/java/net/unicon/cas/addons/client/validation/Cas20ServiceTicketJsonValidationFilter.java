package net.unicon.cas.addons.client.validation;

import javax.servlet.FilterConfig;

import org.jasig.cas.client.validation.AbstractTicketValidationFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.TicketValidator;

/**
 * An implementation of the {@link AbstractTicketValidationFilter} that designates the ticket validator object 
 * to be an instance of {@link Cas20ServiceTicketJsonValidator}
 * 
 * <br><br>The configuration of this filter is similar to {@link Cas20ProxyReceivingTicketValidationFilter}
 * 
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, inc.
 * @since 0.6
 *
 */
public class Cas20ServiceTicketJsonValidationFilter extends AbstractTicketValidationFilter {

    @Override
    protected TicketValidator getTicketValidator(final FilterConfig filterConfig) {
        final String casServerUrlPrefix = getPropertyFromInitParams(filterConfig, "casServerUrlPrefix", null);
        final Cas20ServiceTicketJsonValidator validator = new Cas20ServiceTicketJsonValidator(
                casServerUrlPrefix);
        validator.setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
        validator.setEncoding(getPropertyFromInitParams(filterConfig, "encoding", "UTF-8"));
        validator.setHostnameVerifier(getHostnameVerifier(filterConfig));
        return validator;
    }
}
