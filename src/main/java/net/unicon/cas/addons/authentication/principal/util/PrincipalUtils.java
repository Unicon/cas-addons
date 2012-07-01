package net.unicon.cas.addons.authentication.principal.util;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.principal.Principal;

/**
 * Utility class that does various operations related to a {@link Principal} object.

 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 */
public final class PrincipalUtils {

    /**
     * Parses the <code>userId</code> and transforms it back if the value is recognized as en email address.
     * Otherwise, returns the original value.
     * 
     * @param userId userId or email address of the principal passed. A valid email address would in the format of
     * "user@domain.edu"
     * @return Transformed <code>userId</code> or <code>null</code> if <code>userId</code> is <code>null</code> or blank.
     */
    public static String getTransformedUserId(final String userId) {
        if (StringUtils.isBlank(userId))
            return null;

        final int idx = userId.indexOf('@');
        if (idx == -1)
            return userId;
        return userId.substring(0, idx);
    }

}
