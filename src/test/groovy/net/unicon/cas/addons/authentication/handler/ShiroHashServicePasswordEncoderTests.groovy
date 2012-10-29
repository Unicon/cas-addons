package net.unicon.cas.addons.authentication.handler

import spock.lang.Specification

/**
 * 
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
class ShiroHashServicePasswordEncoderTests extends Specification {

    ShiroHashServicePasswordEncoder passwordEncoderUnderTest = new ShiroHashServicePasswordEncoder()

    static PASSWORD = 'password'

    static SALT = 'salt'

    static HASHED_VALUE_COMPUTED_WITH_DEFAULT_CONFIG_VALUES = 'b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86'

    static HASHED_VALUE_COMPUTED_WITH_SALT = '2908d2c28dfc047741fc590a026ffade237ab2ba7e1266f010fe49bde548b5987a534a86655a0d17f336588e540cd66f67234b152bbb645b4bb85758a1325d64'

    static HASHED_VALUE_COMPUTED_WITH_SALT_AND_100_ITERATIONS = '54d28627f8049b94bd7b8be8317cbdb33b1f4f0cd6f256ed681965967abc9a49ffb67a0191203582e482737bc43f24dffd881cd2f7929428997a2463040066f2'

    static HASHED_VALUE_COMPUTED_WITH_SALT_AND_1000_ITERATIONS_AND_SHA1_ALGORITHM = '5e206dbe5af3cbf75c0890cba68a8270565d1321'



    def "Test correct encoding logic"() {
        expect: 'the correct hash value is computed with DefaultHashService configuration options'
        passwordEncoderUnderTest.encode(PASSWORD) == HASHED_VALUE_COMPUTED_WITH_DEFAULT_CONFIG_VALUES

        when: "the encoder is configured with 'salt' salt value"
        passwordEncoderUnderTest.salt = SALT

        then:
        passwordEncoderUnderTest.encode(PASSWORD) == HASHED_VALUE_COMPUTED_WITH_SALT

        when: "the encoder is configured with 'salt' salt value and 100 hash iterations"
        passwordEncoderUnderTest.salt = SALT
        passwordEncoderUnderTest.hashIterations = 100
        passwordEncoderUnderTest.init()

        then:
        passwordEncoderUnderTest.encode(PASSWORD) == HASHED_VALUE_COMPUTED_WITH_SALT_AND_100_ITERATIONS

        when: "the encoder is configured with 'salt' salt value and 1000 hash iterations, and SHA-1 algorithm"
        passwordEncoderUnderTest.salt = SALT
        passwordEncoderUnderTest.hashIterations = 1000
        passwordEncoderUnderTest.digestAlgorithmName = 'SHA-1'
        passwordEncoderUnderTest.init()

        then:
        passwordEncoderUnderTest.encode(PASSWORD) == HASHED_VALUE_COMPUTED_WITH_SALT_AND_1000_ITERATIONS_AND_SHA1_ALGORITHM
    }
}
