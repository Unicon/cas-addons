package net.unicon.cas.addons.serviceregistry;

import org.jasig.cas.services.RegexRegisteredService;
import java.util.HashMap;
import java.util.Map;

/**
 * An extension to <code>RegexRegisteredService</code> with extra arbitrary attributes
 *
 * @author Eric Pierce (epierce@usf.edu)
 * @since 0.5
 */

public class RegexRegisteredServiceWithAttributes extends RegexRegisteredService implements RegisteredServiceWithAttributes {

    private Map<String, Object> extraAttributes = new HashMap<String, Object>();

    @Override
    public Map<String, Object> getExtraAttributes() {
        return this.extraAttributes;
    }

    public void setExtraAttributes(Map<String, Object> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }
}