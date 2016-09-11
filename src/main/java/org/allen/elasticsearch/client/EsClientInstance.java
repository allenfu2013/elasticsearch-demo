package org.allen.elasticsearch.client;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EsClientInstance {

    private static final String ES_CLUSTER_NAME = "testClusterName";  // es默认使用elasticsearch作为集群名称
    private static EsClientInstance instance = new EsClientInstance();
    private static TransportClient client;

    private EsClientInstance(){
        init();
    }

    private void init() {
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", ES_CLUSTER_NAME)
//                .put("client.transport.sniff", true)
                .build();

        try {
            client = TransportClient.builder().settings(settings).build()
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300))
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static EsClientInstance getInstance() {
        return instance;
    }

    public static TransportClient getClient() {
        return client;
    }
}
