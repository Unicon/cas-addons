package net.unicon.cas.addons.config;

import org.jasig.cas.authentication.AuthenticationManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CasNamespaceAuthenticationManagerWithBindLdapHandlerParserTests {

    @Autowired
    ApplicationContext applicationContext;

    private static final String AUTHN_MANAGER_BEAN_NAME = "authenticationManager";

    @Test
    public void authenticationManagerWithBindLdapHandlerBeanDefinitionCorrectlyParsed() {
        assertTrue(applicationContext.containsBean(AUTHN_MANAGER_BEAN_NAME));
        assertTrue(applicationContext.getBeansOfType(AuthenticationManager.class).size() == 1);
    }
}
