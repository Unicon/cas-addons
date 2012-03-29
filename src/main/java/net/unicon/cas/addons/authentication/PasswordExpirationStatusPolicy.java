package net.unicon.cas.addons.authentication;

import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

import java.util.Map;

/**
 * A strategy interface for determining a password expiration status based on the provided credentials, principal or principal's attributes.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
public interface PasswordExpirationStatusPolicy {

    /**
     * Returns password status
     * @param principal
     * @return PasswordStatus based on provided authenticated principal
     * @throws RuntimeException
     */
    PasswordStatus computePasswordExpirationStatus(Principal principal) throws RuntimeException;

    /**
     * Returns password status
     * @param principalAttributes
     * @return PasswordStatus based on provided authenticated principal's attributes
     * @throws RuntimeException
     */
    PasswordStatus computePasswordExpirationStatus(Map<String, Object> principalAttributes) throws RuntimeException;

    /**
     * Returns password status
     * @param credentials
     * @return PasswordStatus based on provided user's credentials
     * @throws RuntimeException
     */
    PasswordStatus computePasswordExpirationStatus(UsernamePasswordCredentials credentials) throws RuntimeException;

    enum PasswordStatus {
        VALID,
        ABOUT_TO_EXPIRE,
        EXPIRED,
        CHANGED_BY_ADMINISTRATOR,
        OTHER,
        UNDETERMINED;

        private int daysBeforeExpiration;

        public int getDaysBeforeExpiration() {
            return this.daysBeforeExpiration;
        }

        public PasswordStatus withDaysBeforeExpiration(int daysBeforeExpiration) {
            this.daysBeforeExpiration = daysBeforeExpiration;
            return this;
        }
    };
}
