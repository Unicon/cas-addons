package net.unicon.cas.addons.authentication.handler;

import net.unicon.cas.addons.authentication.principal.util.PrincipalUtils;

import org.jasig.cas.authentication.handler.PrincipalNameTransformer;

/**
 * An implementation of the {@link PrincipalNameTransformer} that accepts an email address
 * and transforms it back to the user id.
 * 
 * <p>Note: this API is only intended to be called by CAS server code e.g. any custom CAS server overlay extension, etc.</p>
 * 
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 */
public class EmailAddressToPrincipalNameTransformer implements PrincipalNameTransformer {

    @Override
    public String transform(final String arg0) {
        return PrincipalUtils.getTransformedUserId(arg0);
    }
}
