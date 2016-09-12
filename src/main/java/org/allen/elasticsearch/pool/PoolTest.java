package org.allen.elasticsearch.pool;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * AUTHOR: Allen Fu
 * DATE:   2016-09-12
 */
public class PoolTest {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        EsClientPool esClientPool = context.getBean(EsClientPool.class);
        try {
            List<EsClient> list = new ArrayList<EsClient>();
            for (int i = 0; i < 5; i++) {
                EsClient esClient = esClientPool.borrowObject();
                list.add(esClient);
                System.out.println(esClient.hashCode());
            }
            esClientPool.returnObject(list.get(0));
            esClientPool.returnObject(list.get(1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        context.close();
    }

}
