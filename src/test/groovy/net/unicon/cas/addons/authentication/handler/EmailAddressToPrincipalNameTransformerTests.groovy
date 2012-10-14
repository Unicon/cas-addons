package net.unicon.cas.addons.authentication.handler

import spock.lang.Specification
import org.jasig.cas.authentication.handler.PrincipalNameTransformer

/**
 *
 * @author Dmitriy Kopylenko
 * @author @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
class EmailAddressToPrincipalNameTransformerTests extends Specification {

    //Each spec's feature method gets its own instance - for free!
    PrincipalNameTransformer transformerUnderTest = new EmailAddressToPrincipalNameTransformer()

    def "Test transformation of basic user ids that are not in an email format"() {
        given: 'Basic user id'
        def notAnEmail = 'userId'

        expect: 'Transformer to return the same basic id it was passed in'
        transformerUnderTest.transform(notAnEmail) == 'userId'
    }

    def "Test transformation of null and empty values"() {
        expect: 'Transformer to return null if it was passed null or empty String'
        transformerUnderTest.transform(null) == null
        transformerUnderTest.transform('') == null
    }

    def "Test tranformation from email values to user ids (without the email address part)"() {
        given: "User's email address"
        def emailAddress = 'userId@domain.edu'

        expect: 'Transformer to return the part of an email address before the @ sign'
        transformerUnderTest.transform(emailAddress) == 'userId'
    }
}
