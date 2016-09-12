package org.allen.elasticsearch.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * AUTHOR: Allen Fu
 * DATE:   2016-09-12
 */
public class EsClientFactory extends BasePooledObjectFactory<EsClient> {
    @Override
    public EsClient create() throws Exception {
        return new EsClient();
    }

    @Override
    public PooledObject<EsClient> wrap(EsClient obj) {
        return new DefaultPooledObject<EsClient>(obj);
    }

    @Override
    public boolean validateObject(PooledObject<EsClient> p) {
        return super.validateObject(p);
    }

    @Override
    public void destroyObject(PooledObject<EsClient> p) throws Exception {
        p.getObject().close();
    }
}
