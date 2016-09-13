package org.allen.elasticsearch;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.net.InetAddress;

/**
 * elasticsearch operation
 * v1.0
 * v1.1 TODO use connection pool
 */
public class ElasticsearchOps {

    // client to connect elasticsearch
    private TransportClient client;

    // config for elasticsearch
    private ElasticsearchConfig config;

    public ElasticsearchOps(ElasticsearchConfig config) {
        this.config = config;
        init();
    }

    private void init() {
        if (config == null) {
            throw new ElasticsearchException("ElasticsearchConfig must not be null");
        }
        try {
            // setting
            Settings settings = Settings.settingsBuilder().put("cluster.name", config.getClusterName()).build();
            // create client
            String[] hosts = config.getHosts().split(",");
//            InetSocketTransportAddress[] addresses = new InetSocketTransportAddress[hosts.length];

            client = TransportClient.builder().settings(settings).build();

            for (int i = 0; i< hosts.length; i++) {
                String[] hostPort = hosts[i].split(":");
//                addresses[i] = new InetSocketTransportAddress(InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1]));
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1])));
            }

//            client = TransportClient.builder().settings(settings).build().addTransportAddresses(addresses);

            // create index
            String indexName = config.getIndex();
            IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(Requests.indicesExistsRequest(indexName)).actionGet();
            if (!indicesExistsResponse.isExists()) {
                client.admin().indices().prepareCreate(indexName).execute().actionGet();
            }

            if (config.getUpdateMapping()) {
//                putMapping(DfpIndexType.DFP_IOS);
//                putMapping(DfpIndexType.DFP_ANDROID);
            }

        } catch (Exception e) {
            throw new ElasticsearchException("elasticsearch client init failed", e);
        }
    }

    public void destory() {
        client.close();
    }

    private void putMapping(DfpIndexType indexType) {
        String type = indexType.getType();
        String mappingFile = indexType.getMappingFile();
        String mappingJson = loadMappingFile(mappingFile);
        PutMappingRequest mapping = Requests.putMappingRequest(config.getIndex()).type(type).source(mappingJson);
        client.admin().indices().putMapping(mapping).actionGet();
    }

    private String loadMappingFile(String fileName) {
        try {
            File mappingFile = ResourceUtils.getFile("classpath:" + fileName);
            String json = FileUtils.readFileToString(mappingFile, "UTF-8");
            return json;
        } catch (Exception e) {
            throw new ElasticsearchException(String.format("%s cannot be found", fileName));
        }
    }

    public void setConfig(ElasticsearchConfig config) {
        this.config = config;
    }
}