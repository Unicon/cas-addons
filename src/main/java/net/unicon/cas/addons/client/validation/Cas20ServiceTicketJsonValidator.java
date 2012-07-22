package net.unicon.cas.addons.client.validation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import net.unicon.cas.addons.response.ServiceValidateSuccessJsonView;
import net.unicon.cas.addons.response.TicketValidationJsonResponse;

import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An implementation of the {@link Cas20ServiceTicketValidator} that expects the ticket validation response to be
 * a JSON string, represented by {@link TicketValidationJsonResponse} that is rendered by {@link ServiceValidateSuccessJsonView}.
 * On success, it returns an instance of {@link Assertion} object that is populated with ticket data and attributes, if any.
 * 
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon, inc.
 * @since 0.6
 *
 */
public class Cas20ServiceTicketJsonValidator extends Cas20ServiceTicketValidator {

    private ObjectMapper jacksonObjectMapper = null;

    public Cas20ServiceTicketJsonValidator(final String casServerUrlPrefix) {
        super(casServerUrlPrefix);
        this.jacksonObjectMapper = new ObjectMapper();
    }

    @Override
    public Assertion validate(final String ticket, final String service) throws TicketValidationException {
        final String validationUrl = super.constructValidationUrl(ticket, service);
        if (this.log.isDebugEnabled()) {
            this.log.debug("Constructing validation url: " + validationUrl);
        }

        try {
            this.log.debug("Retrieving response from server.");
            final String serverResponse = super.retrieveResponseFromServer(new URL(validationUrl), ticket);

            if (serverResponse == null) {
                throw new TicketValidationException("The CAS server returned no response.");
            }

            if (this.log.isDebugEnabled()) {
                this.log.debug("Server response: " + serverResponse);
            }

            return parseJsonResponseFromServer(serverResponse);
        } catch (final MalformedURLException e) {
            throw new TicketValidationException(e);
        }
    }

    protected Assertion parseJsonResponseFromServer(final String response) throws TicketValidationException {
        Assertion assertion = null;
        try {

            final String error = XmlUtils.getTextForElement(response, "authenticationFailure");

            if (CommonUtils.isNotBlank(error)) {
                throw new TicketValidationException(error);
            }

            final TicketValidationJsonResponse jsonResponse = this.jacksonObjectMapper.readValue(response,
                    TicketValidationJsonResponse.class);

            if (CommonUtils.isEmpty(jsonResponse.getUser())) {
                throw new TicketValidationException(
                        "No principal was found in the response from the CAS server.");
            }

            final Map<String, Object> attributes = jsonResponse.getAttributes();
            assertion = new AssertionImpl(new AttributePrincipalImpl(jsonResponse.getUser(), attributes),
                    attributes);
        } catch (final IOException e) {
            this.log.error(e.getMessage(), e);
            throw new TicketValidationException("An error occurred while parsing the json response: "
                    + response, e);
        }

        return assertion;
    }
}
