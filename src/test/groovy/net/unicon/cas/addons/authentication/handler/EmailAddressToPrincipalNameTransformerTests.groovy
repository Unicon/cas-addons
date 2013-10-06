package net.unicon.cas.addons.authentication.handler

import spock.lang.Specification
import org.jasig.cas.authentication.handler.PrincipalNameTransformer

/**
 *
 * @author Dmitriy Kopylenko
 * @author <a href="mailto:mmoayyed@unicon.net">Misagh Moayyed</a>
 * @author Unicon , inc.
 */
class EmailAddressToPrincipalNameTransformerTests extends Specification {

    //Each spec's feature method gets its own instance - for free!
    PrincipalNameTransformer transformerUnderTest = new EmailAddressToPrincipalNameTransformer()

    def "Test correct transformation logic"() {
        expect: "Transformer returns non-email values as is, --OR-- returns null for passed in null/empty strings, --OR-- returns part before @ sign for passed in email values"
        transformerUnderTest.transform(passedInValue) == expectedReturnValue

        where:
        passedInValue << ['non_email_string', null, '', 'email_string@example.com']
        expectedReturnValue << ['non_email_string', null, null, 'email_string']
    }
}
