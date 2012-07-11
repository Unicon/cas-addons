package net.unicon.cas.addons.persondir;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.ComplexStubPersonAttributeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
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
	private final Resource personAttributesConfigFile;

	private final ObjectMapper jacksonObjectMapper = new ObjectMapper();

	private final static Logger logger = LoggerFactory.getLogger(JsonBackedComplexStubPersonAttributeDao.class);

	private final Object synchronizationMonitor = new Object();

	public JsonBackedComplexStubPersonAttributeDao(Resource personAttributesConfigFile) {
		this.personAttributesConfigFile = personAttributesConfigFile;
	}

	/**
	 * Init method un-marshals JSON representation of the person attributes.
	 */
	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		Map<String, Map<String, List<Object>>> backingMap;

		logger.info("Un-marshaling person attributes from the config file [{}] ...", this.personAttributesConfigFile.getFile());
		backingMap = this.jacksonObjectMapper.readValue(this.personAttributesConfigFile.getFile(), Map.class);
		logger.debug("Person attributes have been successfully read into a Map<String, Map<String, List<Object>>>: {}", backingMap);

		synchronized (this.synchronizationMonitor) {
			try {
				super.setBackingMap(backingMap);
			}
			//If we get to this point, the JSON file is well-formed, but its structure does not map into PersonDir backingMap generic type - fail fast.
			catch (ClassCastException ex) {
				throw new BeanCreationException(String.format("The semantic structure of the person attributes JSON config is not correct: %s. Please fix it in this resource: [%s]",
						backingMap, this.personAttributesConfigFile.getURI()));
			}
		}
	}
}
