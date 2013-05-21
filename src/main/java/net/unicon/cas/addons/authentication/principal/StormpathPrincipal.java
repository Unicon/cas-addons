package net.unicon.cas.addons.authentication.principal;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import net.unicon.cas.addons.support.Immutable;
import net.unicon.cas.addons.support.ThreadSafe;
import org.jasig.cas.authentication.principal.Principal;

import java.util.*;

/**
 * Implementation of {@link org.jasig.cas.authentication.principal.Principal} which encapsulates an <i>Account</i>
 * entity's data from Stormpath cloud IAM provider. This principal gets resolved after a successful authentication against Stormpath.
 * <p/>
 * This implementation encapsulates transformation of Stormpath <i>Account</i> properties into a publicly exposed
 * <code>Map</code> of attributes.
 * <p/>
 * Note that because Stormpath <i>Account</i>s are strongly-typed (in terms of the data they expose), there is no need
 * for a separate complex implementation of <code>IPersonAttributeDao</code> to separately query for attributes. All the
 * necessary attributes will be available in the <i>Account</i> instance made available to this class.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 * @since 1.4
 *
 * @see Account
 */
@Immutable
@ThreadSafe
public class StormpathPrincipal implements Principal {

    private final Map<String, Object> attributes = new HashMap<String, Object>();

    public StormpathPrincipal(final Account account) {
        this.attributes.put("username", account.getUsername());
        this.attributes.put("email", account.getEmail());
        this.attributes.put("givenName", account.getGivenName());
        this.attributes.put("middleName", account.getMiddleName());
        this.attributes.put("surname", account.getSurname());
        this.attributes.put("status", account.getStatus().toString());
        final List<String> groups = new ArrayList<String>();
        final Iterator<Group> iter = account.getGroups().iterator();
        while (iter.hasNext()) {
            groups.add(iter.next().getName());
        }
        this.attributes.put("groups", Collections.unmodifiableList(groups));
    }

    @Override
    public String getId() {
        return String.class.cast(this.attributes.get("username"));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    @Override
    public String toString() {
        return String.format("StormpathPrincipal{ attributes=%s }", attributes);
    }
}
