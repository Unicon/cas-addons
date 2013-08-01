package net.unicon.cas.addons.web.support.view.saml;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.SamlAuthenticationMetaDataPopulator;
import org.jasig.cas.authentication.principal.RememberMeCredentials;
import org.jasig.cas.authentication.principal.Service;
import org.joda.time.DateTime;
import org.opensaml.saml1.core.Assertion;
import org.opensaml.saml1.core.Attribute;
import org.opensaml.saml1.core.AttributeStatement;
import org.opensaml.saml1.core.AttributeValue;
import org.opensaml.saml1.core.Audience;
import org.opensaml.saml1.core.AudienceRestrictionCondition;
import org.opensaml.saml1.core.AuthenticationStatement;
import org.opensaml.saml1.core.Conditions;
import org.opensaml.saml1.core.ConfirmationMethod;
import org.opensaml.saml1.core.NameIdentifier;
import org.opensaml.saml1.core.Response;
import org.opensaml.saml1.core.StatusCode;
import org.opensaml.saml1.core.Subject;
import org.opensaml.saml1.core.SubjectConfirmation;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.impl.XSAnyBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A replacement implementation for the {@link org.jasig.cas.web.view.Saml10SuccessResponseView}
 * that inheits its configuration from {@link NoSamlNamespaceAbstractSaml10ResponseView} so as to override
 * the behavior that generates SAML namespaces for a successful SAML assertion.
 * @author Misagh Moayyed
 * @since 1.7
 */
public final class Saml10SuccessResponseView extends NoSamlNamespaceAbstractSaml10ResponseView {

    /** Namespace for custom attributes. */
    private static final String NAMESPACE = "http://www.ja-sig.org/products/cas/";

    private static final String REMEMBER_ME_ATTRIBUTE_NAME = "longTermAuthenticationRequestTokenUsed";

    private static final String REMEMBER_ME_ATTRIBUTE_VALUE = "true";

    private static final String CONFIRMATION_METHOD = "urn:oasis:names:tc:SAML:1.0:cm:artifact";

    private final XSAnyBuilder attrValueBuilder = (XSAnyBuilder) Configuration.getBuilderFactory().getBuilder(
            XSAny.TYPE_NAME);

    /** The issuer, generally the hostname. */
    @NotNull
    private String issuer;

    /** The amount of time in milliseconds this is valid for. */
    @Min(1000)
    private long issueLength = 30000;

    @NotNull
    private String rememberMeAttributeName = REMEMBER_ME_ATTRIBUTE_NAME;

    public Saml10SuccessResponseView() {
        super();
    }

    public Saml10SuccessResponseView(final String issuer, final int issueLength) {
        super();
        setIssuer(issuer);
        setIssueLength(issueLength);
    }

    @Override
    protected void prepareResponse(final Response response, final Map<String, Object> model) {
        final Authentication authentication = getAssertionFrom(model).getChainedAuthentications().get(0);
        final DateTime issuedAt = response.getIssueInstant();
        final Service service = getAssertionFrom(model).getService();

        final Object o = authentication.getAttributes().get(RememberMeCredentials.AUTHENTICATION_ATTRIBUTE_REMEMBER_ME);
        final boolean isRemembered = o == Boolean.TRUE && !getAssertionFrom(model).isFromNewLogin();

        // Build up the SAML assertion containing AuthenticationStatement and AttributeStatement
        final Assertion assertion = newSamlObject(Assertion.class);
        assertion.setID(generateId());
        assertion.setIssueInstant(issuedAt);
        assertion.setIssuer(this.issuer);
        assertion.setConditions(newConditions(issuedAt, service.getId()));
        final AuthenticationStatement authnStatement = newAuthenticationStatement(authentication);
        assertion.getAuthenticationStatements().add(authnStatement);
        final Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
        if (!attributes.isEmpty() || isRemembered) {
            assertion.getAttributeStatements().add(
                    newAttributeStatement(newSubject(authentication.getPrincipal().getId()), attributes, isRemembered));
        }
        response.setStatus(newStatus(StatusCode.SUCCESS, null));
        response.getAssertions().add(assertion);
    }

    private Conditions newConditions(final DateTime issuedAt, final String serviceId) {
        final Conditions conditions = newSamlObject(Conditions.class);
        conditions.setNotBefore(issuedAt);
        conditions.setNotOnOrAfter(issuedAt.plus(this.issueLength));
        final AudienceRestrictionCondition audienceRestriction = newSamlObject(AudienceRestrictionCondition.class);
        final Audience audience = newSamlObject(Audience.class);
        audience.setUri(serviceId);
        audienceRestriction.getAudiences().add(audience);
        conditions.getAudienceRestrictionConditions().add(audienceRestriction);
        return conditions;
    }

    private Subject newSubject(final String identifier) {
        final SubjectConfirmation confirmation = newSamlObject(SubjectConfirmation.class);
        final ConfirmationMethod method = newSamlObject(ConfirmationMethod.class);
        method.setConfirmationMethod(CONFIRMATION_METHOD);
        confirmation.getConfirmationMethods().add(method);
        final NameIdentifier nameIdentifier = newSamlObject(NameIdentifier.class);
        nameIdentifier.setNameIdentifier(identifier);
        final Subject subject = newSamlObject(Subject.class);
        subject.setNameIdentifier(nameIdentifier);
        subject.setSubjectConfirmation(confirmation);
        return subject;
    }

    private AuthenticationStatement newAuthenticationStatement(final Authentication authentication) {
        final String authenticationMethod = (String) authentication.getAttributes().get(
                SamlAuthenticationMetaDataPopulator.ATTRIBUTE_AUTHENTICATION_METHOD);
        final AuthenticationStatement authnStatement = newSamlObject(AuthenticationStatement.class);
        authnStatement.setAuthenticationInstant(new DateTime(authentication.getAuthenticatedDate()));
        authnStatement.setAuthenticationMethod(authenticationMethod != null ? authenticationMethod
                : SamlAuthenticationMetaDataPopulator.AUTHN_METHOD_UNSPECIFIED);
        authnStatement.setSubject(newSubject(authentication.getPrincipal().getId()));
        return authnStatement;
    }

    private AttributeStatement newAttributeStatement(final Subject subject, final Map<String, Object> attributes,
                                                     final boolean isRemembered) {

        final AttributeStatement attrStatement = newSamlObject(AttributeStatement.class);
        attrStatement.setSubject(subject);

        for (final Entry<String, Object> e : attributes.entrySet()) {
            if (e.getValue() instanceof Collection<?> && ((Collection<?>) e.getValue()).isEmpty()) {
                logger.info("Skipping attribute " + e.getKey() + " because it does not have any values.");
                continue;
            }
            final Attribute attribute = newSamlObject(Attribute.class);

            attribute.setAttributeName(e.getKey());
            attribute.setAttributeNamespace(NAMESPACE);

            if (e.getValue() instanceof Collection<?>) {
                final Collection<?> c = (Collection<?>) e.getValue();
                for (final Object value : c) {
                    attribute.getAttributeValues().add(newAttributeValue(value, attrStatement));
                }
            } else {
                attribute.getAttributeValues().add(newAttributeValue(e.getValue(), attrStatement));
            }
            attrStatement.getAttributes().add(attribute);
        }

        if (isRemembered) {
            final Attribute attribute = newSamlObject(Attribute.class);
            attribute.setAttributeName(this.rememberMeAttributeName);
            attribute.setAttributeNamespace(NAMESPACE);
            attribute.getAttributeValues().add(newAttributeValue(REMEMBER_ME_ATTRIBUTE_VALUE, attrStatement));
            attrStatement.getAttributes().add(attribute);
        }
        return attrStatement;
    }

    private XSAny newAttributeValue(final Object value, final AttributeStatement statement) {
        final QName temp = statement.getElementQName();
        final QName qName = new QName(temp.getNamespaceURI(), AttributeValue.DEFAULT_ELEMENT_NAME.getLocalPart(),
                temp.getPrefix());

        final XSAny stringValue = this.attrValueBuilder.buildObject(qName);

        if (value instanceof String) {
            stringValue.setTextContent((String) value);
        } else {
            stringValue.setTextContent(value.toString());
        }
        return stringValue;
    }

    public void setIssueLength(final long issueLength) {
        this.issueLength = issueLength;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }

    public void setRememberMeAttributeName(final String rememberMeAttributeName) {
        this.rememberMeAttributeName = rememberMeAttributeName;
    }
}
