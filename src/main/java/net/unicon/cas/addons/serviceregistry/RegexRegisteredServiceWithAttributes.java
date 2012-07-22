package net.unicon.cas.addons.serviceregistry;

import java.util.HashMap;
import java.util.Map;

import org.jasig.cas.services.RegexRegisteredService;

/**
 * An extension to <code>RegexRegisteredService</code> with extra arbitrary attributes
 *
 * @author Eric Pierce (epierce@usf.edu)
 * @since 0.5
 */

public class RegexRegisteredServiceWithAttributes extends RegexRegisteredService implements RegisteredServiceWithAttributes {
    private static final long   serialVersionUID = 1L;

    private Map<String, Object> extraAttributes = new HashMap<String, Object>();

    @Override
    public Map<String, Object> getExtraAttributes() {
        return this.extraAttributes;
    }

    public void setExtraAttributes(final Map<String, Object> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }
}