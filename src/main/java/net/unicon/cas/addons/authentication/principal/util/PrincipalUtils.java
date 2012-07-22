package net.unicon.cas.addons.authentication.principal.util;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.principal.Principal;

/**
 * Utility class that does various operations related to a {@link Principal} object.

 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, inc.
 * @since 0.6
 */
public final class PrincipalUtils {

    /**
     * Parses the <code>username</code> and transforms it back if the value is recognized as en email address.
     * Otherwise, returns the original value.
     * 
     * @param username username or email address of the principal passed. A valid email address would be in the format of
     * "user@domain.edu"
     * @return Transformed <code>username</code> or <code>null</code> if <code>username</code> is <code>null</code> or blank.
     */
    public static String parseNamePartFromEmailAddressIfNecessary(final String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }

        final int idx = username.indexOf('@');
        if (idx == -1) {
            return username;
        }
        return username.substring(0, idx);
    }

}
