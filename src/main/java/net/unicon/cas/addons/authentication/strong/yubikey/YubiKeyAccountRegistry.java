package net.unicon.cas.addons.authentication.strong.yubikey;

/**
 * General contract that allows one to determine whether a particular YubiKey account
 * is allowed to participate in the authentication. Accounts are noted by the username
 * and the public id of the YubiKey device.
 *
 * @author Misagh Moayyed mmoayyed@unicon.net
 * @since 1.5
 * @see  YubiKeyAuthenticationHandler
 */
public interface YubiKeyAccountRegistry {

    /**
     * Determines whether the registyered YubiKey public id is allowed for the <code>uid</code> received.
     * @param uid
     * @param yubikeyPublicId
     * @return true if the public id is allowed and registered for the uid.
     */
    boolean isYubiKeyRegisteredFor(final String uid, final String yubikeyPublicId);
}
