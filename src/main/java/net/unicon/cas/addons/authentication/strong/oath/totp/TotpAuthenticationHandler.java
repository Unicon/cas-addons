package net.unicon.cas.addons.authentication.strong.oath.totp;

import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * AuthenticationHandler to authenticate provided credentials in a form of time-based one-tme passwords (TOTP)
 *
 * Re-uses standard CAS' <code>UsernamePasswordCredentials</code> and assumes that one time password tokens
 * are appropriately un-marshaled into UsernamePasswordCredentials.password field by layers above it.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 *
 * @since 0.5
 */
public class TotpAuthenticationHandler implements AuthenticationHandler {

    private TotpOathDetailsSource totpOathDetailsSource;

    private static Logger logger = LoggerFactory.getLogger(TotpAuthenticationHandler.class);

    public TotpAuthenticationHandler(TotpOathDetailsSource totpOathDetailsSource) {
        this.totpOathDetailsSource = totpOathDetailsSource;
    }

    @Override
    public boolean authenticate(Credentials credentials) throws AuthenticationException {
        UsernamePasswordCredentials totpCredentials = (UsernamePasswordCredentials) credentials;
        logger.info("Authenticating one time password for {}", totpCredentials);
        //PrincipalNotFoundException may result in this call. It should be caught by higher layer and dealt with accordingly.
        Map<TotpOathDetailsSource.OTP, Object> attrs = this.totpOathDetailsSource.getOtpAttributesForPrincipal(totpCredentials.getUsername());
        String seceretKey = (String) attrs.get(TotpOathDetailsSource.OTP.SECRET_KEY);
        Integer interval = (Integer) attrs.get(TotpOathDetailsSource.OTP.INTERVAL);
        Integer intervalWindow = (Integer) attrs.get(TotpOathDetailsSource.OTP.INTERVAL_WINDOW);

        try {
            return TOTPUtils.checkCode(seceretKey, Long.valueOf(totpCredentials.getPassword()), interval, intervalWindow);
        } catch (NoSuchAlgorithmException ex) {
            logger.error(ex.getMessage());
            return false;
        } catch (InvalidKeyException ex) {
            logger.error(ex.getMessage());
            return false;
        }

    }

    @Override
    public boolean supports(Credentials credentials) {
        return credentials != null
                && (UsernamePasswordCredentials.class.equals(credentials.getClass()));
    }
}
