package net.unicon.cas.addons.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.web.view.AbstractCasView;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


/**
 * An alternative lightweight CAS validation response view representing a service ticket validation failure
 * that marshals failure code and description as a JSON String.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.5
 */
public class ServiceValidateFailureJsonView extends AbstractView {

    /**
     * Once the instance is constructed, it is thread-safe
     */
    private final ObjectMapper jacksonObjectMapper = new ObjectMapper();

    @Override
    protected void renderMergedOutputModel(final Map<String, Object> model, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        final Map<String, String> jsonResponse = new HashMap<String, String>(2);
        jsonResponse.put("code", String.class.cast(model.get("code")));
        jsonResponse.put("description", String.class.cast(model.get("description")));
        this.jacksonObjectMapper.writeValue(response.getWriter(), jsonResponse);
    }
}
