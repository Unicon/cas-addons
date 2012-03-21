package net.unicon.cas.addons.serviceregistry;

import org.jasig.cas.services.RegisteredService;

import java.util.Map;

/**
 * An extention to <code>RegisteredService</code> with extra arbitrary attributes
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.1
 */
public interface RegisteredServiceWithAttributes extends RegisteredService {

    Map<String, Object> getExtraAttributes();

}
