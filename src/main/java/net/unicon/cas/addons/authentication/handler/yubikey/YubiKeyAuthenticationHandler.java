package net.unicon.cas.addons.authentication.handler.yubikey;

import com.yubico.client.v2.YubicoClient;
import com.yubico.client.v2.YubicoResponse;
import com.yubico.client.v2.YubicoResponseStatus;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadUsernameOrPasswordAuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

/**
 * @author Misagh Moayyed mmoayyed@unicon.net
 * @since 1.5
 */
public class YubiKeyAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    private YubiKeyAccountRegistry registry = new WhitelistYubiKeyAccountRegistry();

    private YubicoClient client;

    public YubiKeyAuthenticationHandler(final Integer clientId, final String secretKey) {
        this.client = YubicoClient.getClient(clientId);
        this.client.setKey(secretKey);
    }

    public YubiKeyAuthenticationHandler(final Integer clientId, final String secretKey, final YubiKeyAccountRegistry registry) {
        this(clientId, secretKey);
        this.registry = registry;
    }

    @Override
    protected boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials usernamePasswordCredentials) throws AuthenticationException {
        try {
            final String uid = usernamePasswordCredentials.getUsername();
            final String otp = usernamePasswordCredentials.getPassword();
            
            if (YubicoClient.isValidOTPFormat(otp)) {

                final String publicId = YubicoClient.getPublicId(otp);

                if (this.registry.isYubiKeyRegisteredFor(uid, publicId)) {
                    final YubicoResponse response = client.verify(otp);
                    log.debug("YubiKey response status {} at {}", response.getStatus(), response.getTimestamp());
                    return (response.getStatus() == YubicoResponseStatus.OK);

                } else {
                    log.debug("YubiKey public id [{}] is not registered for user [{}]", publicId, uid);
                }
            } else{
                log.debug("Invalid OTP format [{}]", otp);
            }
            return false;
        } catch (final Exception e) {
            throw new BadUsernameOrPasswordAuthenticationException(e);
        }

    }

    private class WhitelistYubiKeyAccountRegistry implements YubiKeyAccountRegistry {

        @Override
        public boolean isYubiKeyRegisteredFor(final String uid, final String yubikeyPublicId) {
            return true;
        }
    }
}
