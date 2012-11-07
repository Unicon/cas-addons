package net.unicon.cas.addons.info;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.unicon.cas.addons.ticket.BulkRetrievalOfTicketsNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * RESTful HTTP resource to expose <code>SingleSignOnSessionsReport</code> as <i>application/json</i> media type.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.0.3
 */
@Component
@Path("/")
public class SingleSignOnSessionsReportResource {

	private final SingleSignOnSessionsReport singleSignOnSessionsReport;

	private final ObjectMapper jsonMapper = new ObjectMapper();

	private static final String ROOT_REPORT_ACTIVE_SESSIONS_KEY = "activeSsoSessions";

	private static final String ROOT_REPORT_NA_KEY = "notAvailable";

	private static final Logger logger = LoggerFactory.getLogger(SingleSignOnSessionsReportResource.class);

	@Autowired
	public SingleSignOnSessionsReportResource(SingleSignOnSessionsReport singleSignOnSessionsReport) {
		this.singleSignOnSessionsReport = singleSignOnSessionsReport;
		//Configure mapper strategies
		this.jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
		this.jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response showActiveSsoSessions() {
		Map<String, Object> sessionsMap = new HashMap<String, Object>(1);
		Collection<Map<String, Object>> activeSessions = null;
		String jsonRepresentation = null;

		try {
			activeSessions = this.singleSignOnSessionsReport.getActiveSsoSessions();
			sessionsMap.put(ROOT_REPORT_ACTIVE_SESSIONS_KEY, activeSessions);
		}
		catch (BulkRetrievalOfTicketsNotSupportedException e) {
			logger.warn(e.getMessage(), e.getCause());
			sessionsMap.put(ROOT_REPORT_NA_KEY, e.getMessage());
		}

		try {
			jsonRepresentation = this.jsonMapper.writeValueAsString(sessionsMap);
		}
		catch (JsonProcessingException e) {
			logger.error("An exception has been caught during an attempt to serialize <active sso sessions report>", e);
			//HTTP 500
			return Response.serverError().build();
		}
		//HTTP 200
		return Response.ok(jsonRepresentation).build();
	}
}
