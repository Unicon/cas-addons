package net.unicon.cas.addons.persondir;

import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.StubPersonAttributeDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An extension of the {@link StubPersonAttributeDao} that is able to identify itself
 * by populating the backing map with the received username. This allows for static attributes
 * to be merged with other DAOs via {@link org.jasig.services.persondir.support.MergingPersonAttributeDaoImpl}.
 * Without the unique identifier that is username, the merge would fail resulting in two distinct attribute sets
 * for the same principal in the ultimate attribute map.
 * @author Misagh Moayyed
 * @since 1.7
 */
public class NamedStubPersonAttributeDao extends StubPersonAttributeDao {

    public NamedStubPersonAttributeDao() {
        super();
    }

    public NamedStubPersonAttributeDao(final Map backingMap) {
        super(backingMap);
    }

    @Override
    public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(final Map<String, List<Object>> query) {

        final List list = query.get("username");
        final String uid = list.get(0).toString();

        final Map m = new HashMap(this.getBackingMap());

        m.put("username", list);

        this.setBackingMap(m);
        return super.getPeopleWithMultivaluedAttributes(query);
    }
}
