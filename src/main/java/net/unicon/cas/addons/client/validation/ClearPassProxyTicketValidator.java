package net.unicon.cas.addons.client.validation;

import java.util.List;

import org.jasig.cas.client.util.XmlUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.InvalidProxyChainTicketValidationException;
import org.jasig.cas.client.validation.TicketValidationException;

/**
 * An implementation of the {@link Cas20ProxyTicketValidator} that disables the use of empty proxy chains.
 * This will prevent clearPass from rendering credentials on the page as the result of a direct
 * service validation. Via this class, the only access method to user credentials via clearPass is through
 * proxy authentication and thereby, acquiring a PGT. 
 * 
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, Inc.
 */
public final class ClearPassProxyTicketValidator extends Cas20ProxyTicketValidator {
  public ClearPassProxyTicketValidator(final String casServerUrlPrefix) {
    super(casServerUrlPrefix);
  }

  @Override
  protected void customParseResponse(final String response, final Assertion assertion) throws TicketValidationException {
    final List<String> proxies = XmlUtils.getTextForElements(response, "proxy");
    final String[] proxiedList = proxies.toArray(new String[proxies.size()]);

    log.debug("Validating clearPass proxy response where the number of proxies received is " + proxies.size());

    if (this.isAcceptAnyProxy()) {
      log.debug("The configuration accepts any proxy. Returning assertion...");
      return;

    }

    if (this.getAllowedProxyChains().contains(proxiedList)) {
      log.debug("Found valid proxy in the configured proxy chain. Returning assertion...");
      return;
    }

    throw new InvalidProxyChainTicketValidationException("Invalid proxy chain: " + proxies.toString());
  }
}