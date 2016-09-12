package org.allen.elasticsearch.pool;

/**
 * AUTHOR: Allen Fu
 * DATE:   2016-09-12
 */
public class EsClient {

    public EsClient() {
        System.out.println("createdAt:" + System.currentTimeMillis());
    }

    public void close() {
        System.out.println(String.format("es client[%s] close at:%s", this.hashCode(), System.currentTimeMillis()));
    }
}
