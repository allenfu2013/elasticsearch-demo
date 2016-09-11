package org.allen.elasticsearch.client;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class TransportClientTest {

    private static final String ES_CLUSTER_NAME = "testClusterName";  // es默认使用elasticsearch作为集群名称
    private static TransportClient client;

    public static void main(String[] args) {
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", ES_CLUSTER_NAME)
//                .put("client.transport.sniff", true)
                .build();

        client = TransportClient.builder().settings(settings).build();

        try {
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.199.100"), 9300));
//            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host2"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        client.close();
        System.out.println("finish...");
    }
}
