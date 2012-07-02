package net.unicon.cas.addons.authentication.handler;

import org.jasig.cas.authentication.handler.PrincipalNameTransformer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EmailAddressToPrincipalNameTransformerTests {

    private PrincipalNameTransformer transformer = null;

    @Before
    public void setUp() {
        this.transformer = new EmailAddressToPrincipalNameTransformer();
    }

    @Test
    public void testBasicUserId() {
        final String emailAddress = "userId";
        Assert.assertEquals(this.transformer.transform(emailAddress), "userId");
    }

    @Test
    public void testBlankUserId() {
        Assert.assertNull(this.transformer.transform(""));
    }

    @Test
    public void testEmailAddressAsUserId() {
        final String emailAddress = "userId@domain.edu";
        Assert.assertEquals(this.transformer.transform(emailAddress), "userId");
    }

    @Test
    public void testNullUserId() {
        Assert.assertNull(this.transformer.transform(null));
    }
}
