package net.unicon.cas.addons.authentication.handler.yubikey;

/**
 * @author Misagh Moayyed mmoayyed@unicon.net
 * @since 1.5
 */
public interface YubiKeyAccountRegistry {

    boolean isYubiKeyRegisteredFor(final String uid, final String yubikeyPublicId);
}
