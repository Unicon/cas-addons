package net.unicon.cas.addons.persondir;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.ComplexStubPersonAttributeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A convenient wrapper around <code>ComplexStubPersonAttributeDao</code> that reads the configuration for its <i>backingMap</i>
 * property from an external JSON configuration resource. This class supports periodic reloading of the backingMap from the external
 * JSON resource. The scheduled poll of JSON files is done outside of this class.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.7
 */
public class JsonBackedComplexStubPersonAttributeDao extends ComplexStubPersonAttributeDao {

	/**
	 * A configuration file containing JSON representation of the stub person attributes. REQUIRED.
	 */
	final Resource personAttributesConfigFile;

	final ObjectMapper jacksonObjectMapper = new ObjectMapper();

	final static Logger logger = LoggerFactory.getLogger(JsonBackedComplexStubPersonAttributeDao.class);

	final Object synchronizationMonitor = new Object();

	public JsonBackedComplexStubPersonAttributeDao(Resource personAttributesConfigFile) {
		this.personAttributesConfigFile = personAttributesConfigFile;
	}

	/**
	 * Init method un-marshals JSON representation of the person attributes. This method could also be used
	 * by an external periodic poller to reload the attributes.
	 */
	@SuppressWarnings("unchecked")
	public void init() {
		Map<String, Map<String, List<Object>>> backingMap;
		try {
			logger.info("Un-marshaling person attributes from the config file [{}] ...", this.personAttributesConfigFile.getFile());
			backingMap = this.jacksonObjectMapper.readValue(this.personAttributesConfigFile.getFile(), Map.class);
			logger.debug("Person attributes have been successfully read into a Map<String, Map<String, List<Object>>>: {}", backingMap);
		}
		catch (IOException ex) {
			logger.warn("An exception is caught while trying to de-serialize JSON configuration file", ex);
			return;
		}
		synchronized (this.synchronizationMonitor) {
			super.setBackingMap(backingMap);
		}
	}

}
