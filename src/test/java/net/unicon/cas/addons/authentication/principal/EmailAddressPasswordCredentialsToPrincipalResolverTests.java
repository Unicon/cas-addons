package net.unicon.cas.addons.authentication.principal;

import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EmailAddressPasswordCredentialsToPrincipalResolverTests {

    private CredentialsToPrincipalResolver resolver    = null;

    @Before
    public void setUp() {
        this.resolver = new EmailAddressPasswordCredentialsToPrincipalResolver();
    }

    @Test
    public void testBasicUserId() {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials();
        credentials.setPassword("somePassword");
        credentials.setUsername("userId");

        final Principal principal = this.resolver.resolvePrincipal(credentials);
        Assert.assertNotNull(principal);
        Assert.assertEquals(principal.getId(), credentials.getUsername());
    }

    @Test
    public void testEmailAddressAsUserId() {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials();
        credentials.setPassword("somePassword");
        credentials.setUsername("userId@domain.edu");
        final Principal principal = this.resolver.resolvePrincipal(credentials);
        Assert.assertNotNull(principal);
        Assert.assertEquals(principal.getId(), "userId");
    }

    @Test
    public void testNullUserId() {
        Assert.assertNull(this.resolver.resolvePrincipal(null));
    }
}
