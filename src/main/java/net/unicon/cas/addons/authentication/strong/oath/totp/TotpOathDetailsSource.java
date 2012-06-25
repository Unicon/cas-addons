package net.unicon.cas.addons.authentication.strong.oath.totp;

import net.unicon.cas.addons.authentication.strong.AdditionalAuthenticationFactorPolicy;

import java.util.Map;

/**
 * A strategy API to define operations pertaining to TOTP authentication information retrieval such as TOTP attributes for principals.
 * <p/>
 * <strong>Concurrency semantics: implementations must be thread safe</strong>
 *
 * @author Dmitriy kopylenko
 * @author Unicon, inc.
 * @since 0.5
 */
public interface TotpOathDetailsSource extends AdditionalAuthenticationFactorPolicy {

    /**
     * Map keys for otp attributes
     */
    enum OTP {
        SECRET_KEY("otp.secret.key"),

        INTERVAL("otp.interval"),

        INTERVAL_WINDOW("otp.interval.window");

        private String attributeKey;

        private OTP(String attributeKey) {
            this.attributeKey = attributeKey;
        }

        @Override
        public String toString() {
            return this.attributeKey;
        }
    }

    /**
     * Retrieve OTP configuration attributes for a principal
     *
     * @param principalId
     * @return a map of OTP attributes or <strong>null</strong> attributes are not configured for given principal.
     * @throws PrincipalNotFoundException if no such principal exists in the OTP configuration store.
     */
    Map<OTP, Object> getOtpAttributesForPrincipal(String principalId) throws PrincipalNotFoundException;
}
