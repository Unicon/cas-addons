package net.unicon.cas.addons.serviceregistry.mongodb;

import java.util.List;

import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServiceRegistryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public final class MongoServiceRegistryDao implements ServiceRegistryDao, InitializingBean {
    private static final Logger   log            = LoggerFactory.getLogger(MongoServiceRegistryDao.class);

    private String                collectionName = RegisteredService.class.getSimpleName();

    private boolean               dropCollection = false;
    @Autowired
    private final MongoOperations mongoTemplate  = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.dropCollection) {
            log.debug("Dropping database collection: {}", RegisteredService.class.getName());
            this.mongoTemplate.dropCollection(RegisteredService.class);
        }

        if (!this.mongoTemplate.collectionExists(RegisteredService.class)) {
            log.debug("Creating database collection: {}", RegisteredService.class.getName());
            this.mongoTemplate.createCollection(RegisteredService.class);
        }
    }

    @Override
    public boolean delete(final RegisteredService svc) {
        if (this.findServiceById(svc.getId()) != null) {
            this.mongoTemplate.remove(svc, this.collectionName);
            log.debug("Removed registered service: {}", svc);
            return true;
        }
        return false;
    }

    @Override
    public RegisteredService findServiceById(final long svcId) {
        return this.mongoTemplate.findOne(new Query(Criteria.where("id").is(svcId)), RegisteredService.class, this.collectionName);
    }

    @Override
    public List<RegisteredService> load() {
        return this.mongoTemplate.findAll(RegisteredService.class, this.collectionName);
    }

    @Override
    public RegisteredService save(final RegisteredService svc) {
        this.mongoTemplate.save(svc, this.collectionName);
        log.debug("Saved registered service: {}", svc);
        return this.findServiceById(svc.getId());
    }

    public void setCollectionName(final String name) {
        this.collectionName = name;
    }

    public void setDropCollection(final boolean dropCollection) {
        this.dropCollection = dropCollection;
    }
}
