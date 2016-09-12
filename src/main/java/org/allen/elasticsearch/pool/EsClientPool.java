package org.allen.elasticsearch.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * AUTHOR: Allen Fu
 * DATE:   2016-09-12
 */
public class EsClientPool {

    private GenericObjectPool<EsClient> clientPool;

    public EsClientPool() {
        System.out.println("###################");
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMinIdle(2);
        poolConfig.setMaxIdle(5);
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxWaitMillis(5000);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        clientPool = new GenericObjectPool<EsClient>(new EsClientFactory(), poolConfig);
    }

    public EsClient borrowObject() throws Exception {
        return clientPool.borrowObject();
    }

    public void returnObject(EsClient esClient) {
        clientPool.returnObject(esClient);
    }

    public void close() {
        System.out.println("closeAt:" + System.currentTimeMillis());
        clientPool.close();
    }
}
