package net.unicon.cas.addons.client.springsecurity;

import org.apache.commons.logging.Log;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * An extension of <code>CasAuthenticationProvider</code> which retrieves principal's password via CAS server's clearpass endpoint
 * and packs it into a <code>UserDetails</code> instance, which is then returned to Spring Security for further processing and session storage.
 * <p/>
 * This implementation creates a new ${@link User} instance and copies all the properties from the originally retrieved ${@link UserDetails}
 * instance in addition to a newly obtained password via clearpass.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.1
 */
public class ClearpassRetrievingCasAuthenticationProvider extends CasAuthenticationProvider {

	private String clearPassEndpointUrl;

	private final static Logger logger = LoggerFactory.getLogger(ClearpassRetrievingCasAuthenticationProvider.class);

	public void setClearPassEndpointUrl(String clearPassEndpointUrl) {
		this.clearPassEndpointUrl = clearPassEndpointUrl;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(this.clearPassEndpointUrl, "A clearPassEndpointUrl must be set");
	}

	@Override
	protected UserDetails loadUserByAssertion(Assertion assertion) {
		UserDetails userDetails = super.loadUserByAssertion(assertion);
		try {
			final String password = retrieveClearPassFrom(assertion);
			return new User(userDetails.getUsername(), password, userDetails.isEnabled(), userDetails.isAccountNonExpired(), userDetails.isCredentialsNonExpired(), userDetails.isAccountNonLocked(), userDetails.getAuthorities());
		}
		catch (Throwable e) {
			logger.error("Failed to retrieve clearpass from CAS server. Returning the original UserDetails object: ", e);
			return userDetails;
		}
	}

	private String retrieveClearPassFrom(Assertion assertion) throws IOException {
		//Clearpass dance
		final String clearpassProxyTicket = assertion.getPrincipal().getProxyTicketFor(this.clearPassEndpointUrl);
		final String clearpassUrl = this.clearPassEndpointUrl + "?ticket=" + URLEncoder.encode(clearpassProxyTicket, "UTF-8");
		return XmlUtils.getTextForElement(CommonUtils.getResponseFromServer(clearpassUrl, "UTF-8"), "credentials");
	}
}
