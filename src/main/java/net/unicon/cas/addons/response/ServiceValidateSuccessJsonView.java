package net.unicon.cas.addons.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.web.view.AbstractCasView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * An alternative lightweight CAS validation response view that marshals the authenticated principal's attributes as a JSON String.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 0.6
 */
public class ServiceValidateSuccessJsonView extends AbstractCasView {

	/**
	 * Once the instance is constructed, it is thread-safe
	 */
	private final ObjectMapper jacksonObjectMapper = new ObjectMapper();

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Authentication authentication = getAssertionFrom(model).getChainedAuthentications().get(0);
		Principal principal = authentication.getPrincipal();

		final Map<String, Object> attributes = new LinkedHashMap<String, Object>(principal.getAttributes());
		final Map<String, Object> jsonResponsePayload = new LinkedHashMap<String, Object>();

		jsonResponsePayload.put("user", principal.getId());
		jsonResponsePayload.put("authenticationTime", authentication.getAuthenticatedDate());
		jsonResponsePayload.put("attributes", attributes);

		response.getWriter().print(jacksonObjectMapper.writeValueAsString(jsonResponsePayload));
	}
}
