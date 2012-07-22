package net.unicon.cas.addons.persondir;

import java.util.List;
import java.util.Map;

import net.unicon.cas.addons.support.ResourceChangeDetectingEventNotifier;

import org.jasig.services.persondir.support.ComplexStubPersonAttributeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A convenient wrapper around <code>ComplexStubPersonAttributeDao</code> that reads the configuration for its <i>backingMap</i>
 * property from an external JSON configuration resource. This class supports periodic reloading of the backingMap from the external
 * JSON resource by listening for <code>ResourceChangedEvent</code>s and reacting to them.
 * <p/>
 * The polling of the JSON resource and detecting changes is done outside of this class.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.7
 * @see ResourceChangeDetectingEventNotifier
 */
public class JsonBackedComplexStubPersonAttributeDao extends ComplexStubPersonAttributeDao implements
ApplicationListener<ResourceChangeDetectingEventNotifier.ResourceChangedEvent> {

    /**
     * A configuration file containing JSON representation of the stub person attributes. REQUIRED.
     */
    private final Resource personAttributesConfigFile;

    private final ObjectMapper jacksonObjectMapper = new ObjectMapper();

    private final static Logger logger = LoggerFactory.getLogger(JsonBackedComplexStubPersonAttributeDao.class);

    private final Object synchronizationMonitor = new Object();

    public JsonBackedComplexStubPersonAttributeDao(final Resource personAttributesConfigFile) {
        this.personAttributesConfigFile = personAttributesConfigFile;
    }

    /**
     * Init method un-marshals JSON representation of the person attributes.
     */
    public void init() throws Exception {
        try {
            unmarshalAndSetBackingMap();
        }
        //If we get to this point, the JSON file is well-formed, but its structure does not map into PersonDir backingMap generic type - fail fast.
        catch (final ClassCastException ex) {
            throw new BeanCreationException(String.format("The semantic structure of the person attributes JSON config is not correct. Please fix it in this resource: [%s]",
                    this.personAttributesConfigFile.getURI()));
        }
    }

    /**
     * Reload person attributes when JSON config is changed. In case of un-marshaling errors,
     * or any other errors, do not disturb running CAS server by propagating the exceptions,
     * but instead log the error and leave the previously cached person attributes state alone.
     *
     * @param resourceChangedEvent event representing the resource change. Might not actually correspond
     *                             to this JSON config file, so the URI of it needs to be checked first.
     */
    @Override
    public void onApplicationEvent(final ResourceChangeDetectingEventNotifier.ResourceChangedEvent resourceChangedEvent) {
        try {
            if (!resourceChangedEvent.getResourceUri().equals(this.personAttributesConfigFile.getURI())) {
                //Not our resource. Just get out of here.
                return;
            }
        }
        catch (final Throwable e) {
            logger.error("An exception is caught while trying to access JSON resource: ", e);
            return;
        }

        final Map<String, Map<String, List<Object>>> savedBackingMap;
        synchronized (this.synchronizationMonitor) {
            //Save the current state here in order to restore it, should the error occur.
            savedBackingMap = super.getBackingMap();
        }
        try {
            unmarshalAndSetBackingMap();
        }
        catch (final Throwable ex) {
            logger.error("An exception is caught during reloading of the JSON configuration:", ex);
            //Restore the old state. If the error occurs at this stage, well nothing we could do here. Just propagate the exception.
            synchronized (this.synchronizationMonitor) {
                super.setBackingMap(savedBackingMap);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void unmarshalAndSetBackingMap() throws Exception {
        logger.info("Un-marshaling person attributes from the config file [{}] ...", this.personAttributesConfigFile.getFile());
        final Map<String, Map<String, List<Object>>> backingMap = this.jacksonObjectMapper.readValue(
                this.personAttributesConfigFile.getFile(), Map.class);
        logger.debug("Person attributes have been successfully read into a Map<String, Map<String, List<Object>>>: {}", backingMap);
        synchronized (this.synchronizationMonitor) {
            super.setBackingMap(backingMap);
        }
    }

}

