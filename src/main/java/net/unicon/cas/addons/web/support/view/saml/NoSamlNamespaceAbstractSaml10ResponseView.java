package net.unicon.cas.addons.web.support.view.saml;

import org.jasig.cas.authentication.principal.SamlService;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.util.CasHTTPSOAP11Encoder;
import org.jasig.cas.web.support.SamlArgumentExtractor;
import org.jasig.cas.web.view.AbstractCasView;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.impl.SecureRandomIdentifierGenerator;
import org.opensaml.saml1.binding.encoding.HTTPSOAP11Encoder;
import org.opensaml.saml1.core.Response;
import org.opensaml.saml1.core.Status;
import org.opensaml.saml1.core.StatusCode;
import org.opensaml.saml1.core.StatusMessage;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.ConfigurationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * This is an extension of the {@link AbstractCasView} that disables SAML namespaces
 * from the ultimate assertion. The SAML assertion syntax is compliant with CAS server 3.4.x behavior
 * in response to <code>/samlValidate</code> requests, presumably
 * for compatibility with CAS-using applications that depended upon details of
 * the CAS 3.4-style <code>/samlValidate</code> formatting such that they can't cope with the CAS 3.5-style
 * <code>/samlValidate</code> responses. The namespace parameter is set to {@link XMLConstants#DEFAULT_NS_PREFIX}.
 *
 * <p>The implementation closely mimics that of {@link org.jasig.cas.web.view.AbstractSaml10ResponseView} with
 * small changes to the {@link #newSamlObject(Class)} method. Given the way {@link org.jasig.cas.web.view.AbstractSaml10ResponseView}
 * is implemented and the finality of {@link org.jasig.cas.web.view.AbstractSaml10ResponseView#newSamlObject(Class)},
 * the entire class structure was ported over and tweaks made to SAML object creation.</p>
 * @see #newSamlObject(Class)
 * @see Saml10SuccessResponseView
 * @author Misagh Moayyed
 * @since 1.7
 */
public abstract class NoSamlNamespaceAbstractSaml10ResponseView extends AbstractCasView {

    private static final String DEFAULT_ELEMENT_NAME_FIELD = "DEFAULT_ELEMENT_NAME";

    private static final String DEFAULT_ENCODING = "UTF-8";

    private final SamlArgumentExtractor samlArgumentExtractor = new SamlArgumentExtractor();

    private final HTTPSOAP11Encoder encoder = new CasHTTPSOAP11Encoder();

    private final SecureRandomIdentifierGenerator idGenerator;

    @NotNull
    private String encoding = DEFAULT_ENCODING;

    /**
     * Sets the character encoding in the HTTP response.
     *
     * @param encoding Response character encoding.
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    static {
        try {
            // Initialize OpenSAML default configuration
            // (only needed once per classloader)
            DefaultBootstrap.bootstrap();
        } catch (final ConfigurationException e) {
            throw new IllegalStateException("Error initializing OpenSAML library.", e);
        }
    }

    protected NoSamlNamespaceAbstractSaml10ResponseView() {
        try {
            this.idGenerator = new SecureRandomIdentifierGenerator();
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot create secure random ID generator for SAML message IDs.");
        }
    }

    protected void renderMergedOutputModel(
            final Map<String, Object> model, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        response.setCharacterEncoding(this.encoding);

        final WebApplicationService service = this.samlArgumentExtractor.extractService(request);
        final String serviceId = service != null ? service.getId() : "UNKNOWN";

        try {
            final Response samlResponse = newSamlObject(Response.class);
            samlResponse.setID(generateId());
            samlResponse.setIssueInstant(new DateTime());
            samlResponse.setVersion(SAMLVersion.VERSION_11);
            samlResponse.setRecipient(serviceId);
            if (service instanceof SamlService) {
                final SamlService samlService = (SamlService) service;

                if (samlService.getRequestID() != null) {
                    samlResponse.setInResponseTo(samlService.getRequestID());
                }
            }
            prepareResponse(samlResponse, model);

            final BasicSAMLMessageContext messageContext = new BasicSAMLMessageContext();
            messageContext.setOutboundMessageTransport(new HttpServletResponseAdapter(response, request.isSecure()));
            messageContext.setOutboundSAMLMessage(samlResponse);
            this.encoder.encode(messageContext);
        } catch (final Exception e) {
            this.log.error("Error generating SAML response for service {}.", serviceId);
            throw e;
        }
    }

    /**
     * Subclasses must implement this method by adding child elements (status, assertion, etc) to
     * the given empty SAML 1 response message.  Impelmenters need not be concerned with error handling.
     *
     * @param response SAML 1 response message to be filled.
     * @param model Spring MVC model map containing data needed to prepare response.
     */
    protected abstract void prepareResponse(Response response, Map<String, Object> model);


    protected final String generateId() {
        return this.idGenerator.generateIdentifier();
    }

    protected final <T extends SAMLObject> T newSamlObject(final Class<T> objectType) {
        final QName qName;
        try {
            final Field f = objectType.getField(DEFAULT_ELEMENT_NAME_FIELD);

            final QName tempQName = (QName) f.get(null);
            qName = new QName(tempQName.getNamespaceURI(), tempQName.getLocalPart(), XMLConstants.DEFAULT_NS_PREFIX);
        } catch (final NoSuchFieldException e) {
            throw new IllegalStateException("Cannot find field " + objectType.getName() + "."
                    + DEFAULT_ELEMENT_NAME_FIELD);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Cannot access field " + objectType.getName() + "."
                    + DEFAULT_ELEMENT_NAME_FIELD);
        }

        final SAMLObjectBuilder<T> builder = (SAMLObjectBuilder<T>) Configuration.getBuilderFactory().getBuilder(qName);
        if (builder == null) {
            throw new IllegalStateException("No SAMLObjectBuilder registered for class " + objectType.getName());
        }
        return objectType.cast(builder.buildObject(qName));
    }

    protected final Status newStatus(final QName codeValue, final String statusMessage) {
        final Status status = newSamlObject(Status.class);
        final StatusCode code = newSamlObject(StatusCode.class);
        code.setValue(codeValue);
        status.setStatusCode(code);
        if (statusMessage != null) {
            final StatusMessage message = newSamlObject(StatusMessage.class);
            message.setMessage(statusMessage);
            status.setStatusMessage(message);
        }
        return status;
    }

}

