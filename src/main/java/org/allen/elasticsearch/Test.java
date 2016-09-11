package org.allen.elasticsearch;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by allen on 16/9/9.
 */
public class Test {

    private static TransportClient client;

    private static String indexName = "test_name";
    private static String indexType = "test_type";

    static {
        Settings settings = Settings.settingsBuilder().put("cluster.name", "myClusterName").build();

        try {
            client = TransportClient.builder()
//                    .settings(settings)
                    .build()
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host1"), 9300))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.199.100"), 9300));

            String mapping_json = "{" +
                                "\"test_type\":{" +
                                    "\"properties\":{" +
                                        "\"id\":{" +
                                            "\"type\":\"string\"," +
                                            "\"index\":\"not_analyzed\"" +
                                        "}," +
                                        "\"name\":{" +
                                            "\"type\":\"string\"," +
                                            "\"index\":\"not_analyzed\"" +
                                        "}," +
                                        "\"price\":{" +
                                            "\"type\":\"double\"," +
                                            "\"index\":\"not_analyzed\"" +
                                        "}," +
                                        "\"desc\":{" +
                                            "\"type\":\"string\"" +
                                        "}" +
                                    "}" +
                                "}" +
                           "}";
            // create index
            client.admin().indices().prepareCreate(indexName).execute().actionGet();

            // way1: set mapping
//            client.admin().indices().preparePutMapping(indexName).setType(indexType).setSource(mapping_json).execute().actionGet();

            // way2: put mapping
            PutMappingRequest mapping = Requests.putMappingRequest(indexName).type(indexType).source(mapping_json);
            client.admin().indices().putMapping(mapping).actionGet();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        index();
//        search();
//        analyze();

        client.close();
        System.out.println("done");
    }

    private static void index() {
        Map<String, Object> product = new HashMap<String, Object>();
        product.put("id", "4");
        product.put("name", "Redis实战");
        product.put("price", 50);
        product.put("desc", "Redis实战、操作、案例");

        IndexResponse indexResponse = client.prepareIndex(indexName, indexType, (String) product.get("id"))
                .setSource(product).get();
        System.out.println("index success");
    }


    private static void analyze() {
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        AnalyzeRequestBuilder request = new AnalyzeRequestBuilder(indicesAdminClient, AnalyzeAction.INSTANCE, indexName, "我的宝马马力不错");
        request.setTokenizer("smartcn");

        List<AnalyzeResponse.AnalyzeToken> listAnalysis = request.execute().actionGet().getTokens();

        System.out.println(listAnalysis.size());

        for (AnalyzeResponse.AnalyzeToken term : listAnalysis) {
            System.out.println(term.getTerm());
        }
        System.out.print('\n');
    }

    private static void search() {
        SearchResponse searchResponse = client.prepareSearch(indexName).setTypes(indexType)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                .setQuery(QueryBuilders.matchQuery("desc", "Redis实战"))
                .setQuery(QueryBuilders.termQuery("desc", "入门"))
//                .setQuery(QueryBuilders.boolQuery()
//                        .should(QueryBuilders.matchQuery("name", "book"))
//                        .should(QueryBuilders.matchQuery("price", 50))
//                )
//                .setPostFilter(QueryBuilders.matchQuery("name", "Hadoop"))
//                .setQuery(QueryBuilders.functionScoreQuery(QueryBuilders.queryFilter(QueryBuilders.termQuery("price", "50")))
//                                .add(QueryBuilders.matchQuery("desc", "java"), ScoreFunctionBuilders.weightFactorFunction(3))
//                                .add(QueryBuilders.termQuery("price", "50"), ScoreFunctionBuilders.weightFactorFunction(5))
//                                .add(QueryBuilders)
//                                .scoreMode("sum")
//                )
                .setFrom(0).setSize(10)
                .setExplain(true)
                .execute()
                .actionGet();


        System.out.println(String.format("total_hits: %s, took: %s", searchResponse.getHits().getTotalHits(), searchResponse.getTookInMillis()));
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println(String.format("scores: %s", hit.getScore()));
            System.out.println(hit.getSourceAsString() + "\n");
        }
    }

}
