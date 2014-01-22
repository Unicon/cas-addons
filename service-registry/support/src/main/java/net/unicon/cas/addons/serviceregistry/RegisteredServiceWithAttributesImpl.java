package net.unicon.cas.addons.serviceregistry;

import java.util.HashMap;
import java.util.Map;

import org.jasig.cas.services.RegisteredServiceImpl;

/**
 * An extention to <code>RegisteredServiceImpl</code> with extra arbitrary attributes
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.1
 */
public class RegisteredServiceWithAttributesImpl extends RegisteredServiceImpl implements RegisteredServiceWithAttributes {

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
