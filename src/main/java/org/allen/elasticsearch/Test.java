package org.allen.elasticsearch;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.exists.ExistsRequest;
import org.elasticsearch.action.exists.ExistsRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    private static TransportClient client;

    private static String indexName = "dfp";
    private static String indexType = "dfp_android";

    private static void init() {
        Settings settings = Settings.settingsBuilder().put("cluster.name", "gr-es").build();

        try {
            client = TransportClient.builder()
                    .settings(settings)
                    .build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.16.23.70"), 9300))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.16.23.71"), 9300));

            // create index
            IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(Requests.indicesExistsRequest(indexName)).actionGet();
            if (!indicesExistsResponse.isExists()) {
                client.admin().indices().prepareCreate(indexName).execute().actionGet();
            }

            TypesExistsResponse typesExistsResponse = client.admin().indices().typesExists(new TypesExistsRequest(new String[]{indexName}, indexType)).actionGet();
            System.out.println(typesExistsResponse.isExists());

            // way1: set mapping
//            client.admin().indices().preparePutMapping(indexName).setType(indexType).setSource(mapping_json).execute().actionGet();

            // way2: put mapping
            String mapping_json = loadMapping("dfp_android_mapping.json");
            PutMappingRequest mapping = Requests.putMappingRequest(indexName).type(indexType).source(mapping_json);
            client.admin().indices().putMapping(mapping).actionGet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        init();
//        index();
//        index1();
//        search();
        searchDevice();
//        analyze();

        client.close();
        System.out.println("done");
    }

    private static String loadMapping(String fileName) {
        try {
            File mappingFile = ResourceUtils.getFile("classpath:" + fileName);
            String json = FileUtils.readFileToString(mappingFile, "UTF-8");
//            System.out.println(json);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void index() {
        Map<String, Object> product = new HashMap<String, Object>();
        product.put("id", "4");
        product.put("name", "Redis实战");
        product.put("price", 50);
        product.put("desc", "本书深入浅出地介绍了Redis的5种数据类型，并通过多个实用示例展示了Redis的用法。除此之外，书中还讲述了Redis的优化方法以及扩展方法，是一本对于学习和使用 Redis 来说不可多得的参考书籍。\n" +
                "本书一共由三个部分组成。第一部分对Redis进行了介 绍，说明了Redis的基本使用方法、它拥有的5种数据结构以及操作这5种数据结构的命令，并讲解了如何使用Redis去构建文章展示网站、cookie、购物车、网页缓存、数据库行缓存等一系列程序。第二部分对Redis命令进行了更详细的介绍，并展示了如何使用Redis去构建更为复杂的辅助工具和应用程序，并在最后展示了如何使用Redis去构建一个简单的社交网站。第三部分对Redis用户经常会遇到的一些问题进行了介绍，讲解了降低Redis内存占用的方法、扩展Redis性能的方法以及使用Lua语言进行脚本编程的方法。\n" +
                "综上所述， 本书将是一本对于学习和使用 Redis 来说不可多得的参考书籍， 无论是 Redis 新手还是有一定经验的 Redis 使用者， 应该都能从本书中获益。");

        IndexResponse indexResponse = client.prepareIndex(indexName, indexType, (String) product.get("id"))
                .setSource(product).execute().actionGet();
        System.out.println("index success");
    }

    private static void index1() {
        Map<String, String> device = prepareDevice();
        IndexResponse indexResponse = client.prepareIndex(indexName, indexType, (String) device.get("id"))
                .setSource(device).execute().actionGet();
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
//                .setQuery(QueryBuilders.termQuery("desc", "入门"))
                .setQuery(QueryBuilders.boolQuery()
//                        .should(QueryBuilders.termQuery("name", "Redis实战"))
                                .should(QueryBuilders.termQuery("price", 50))
                )
//                .setPostFilter(QueryBuilders.matchQuery("name", "Hadoop"))
                /*.setQuery(QueryBuilders.functionScoreQuery()
//                                .functionScoreQuery(QueryBuilders.queryFilter(QueryBuilders.termQuery("price", "50")))
                                .add(QueryBuilders.termQuery("desc", "java"), ScoreFunctionBuilders.weightFactorFunction(3))
                                .add(QueryBuilders.termQuery("price", "50"), ScoreFunctionBuilders.weightFactorFunction(5))
                                .scoreMode("sum")
                )*/
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

    private static void searchDevice() {
        Map<String, String> device = prepareDevice();
        device.remove("id");
        device.remove("internalId");
        device.remove("deviceType");
        device.remove("fingerPrintId");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (String key : device.keySet()) {
            boolQueryBuilder.should(QueryBuilders.termQuery(key, device.get(key)));
        }

        SearchResponse searchResponse = client.prepareSearch(indexName).setTypes(indexType)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                /*.setQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("model", device.get("model")))
                        .should(QueryBuilders.termQuery("resolution", device.get("resolution")))
                        .should(QueryBuilders.termQuery("totalMemory", device.get("totalMemory")))
                )*/
                .setQuery(boolQueryBuilder)
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

    private static Map<String, String> prepareDevice() {
        Map<String, String> device = new HashMap<String, String>();
        device.put("id", "2");
        device.put("deviceType", "android");
        device.put("internalId", "e78e90eb61dc4309aac1e50631b594e3");
        device.put("fingerPrintId", "123456789");
        device.put("resolution", "1920x1080");
        device.put("model", "MI 5");
        device.put("totalMemory", "2889859072");
        device.put("totalStorage", "57787318272");
        device.put("cpuCount", "4");
        device.put("cellMac", "64:cc:2e:68:bb:4f");
        device.put("cellIp", "10.15.246.72");
        device.put("appList", "链家,爱奇艺,手机营业厅,安兔兔评测,掌上基金,语音设置,大众点评,花生地铁WiFi,豆瓣阅读,招商银行,上海公积金,铁路12306,个税计算器,小米风行播放插件,百度股市通,百度输入法小米版,饿了么,微信,小米金融,安居客,TalkBack,滴滴出行,应用宝,浅塘,58速运,Guitar Tuner,咕咚,你我贷理财,陆金所,QQ同步助手,小米锁屏画报,车来了,淘票票,乐视视频,开眼 Eyepetizer,腾讯视频,万能遥控,QQ浏览器,1号店,米聊,直播吧,口碑外卖,com.xiaomi.adecom,牛呗,有道云笔记,ZAKER,小米商城,KLO Bugreport,爱奇艺播放器,QQ,平安口袋银行,懒投资,我查查,途家,酷狗音乐,Keep,罐头生活,WiFi万能钥匙,优步 - Uber,小米生活,喜马拉雅FM,百度地图,天天基金网,搜房网房天下,途牛旅游,Demo_设备指纹,小米画报奥运特刊,京东金融,央视影音,挖财记账理财,格瓦拉@电影,中国建设银行,壹钱包,摩拜单车,菜鸟裹裹,谷歌拼音输入法,手机京东,天猫,时代财经,高德地图,阅读,点融理财,携程旅行,上海大悦城,墨迹天气,微博,蚂蚁聚宝,蜘蛛电影,百度云,手机淘宝,米家,掌上生活,视频电话,小米运动,简理财,支付宝,小米PPTV播放器插件,小米视频追剧助手,WPS Office,阿里小号");
        device.put("imsi", "460016261608469");
        device.put("touchScreen", "3");
        device.put("brand", "Xiaomi");
        device.put("cpuHardware", "Qualcomm Technologies, Inc MSM8996");
        device.put("cpuSerial", "");
        device.put("cpuSpeed", "1593600");
        device.put("androidDeviceId", "868030020668685");
        device.put("cpuABI", "arm64-v8a,armeabi-v7a,armeabi");
        device.put("location", "121.534912,31.217234");
        device.put("simulator", "0");
        device.put("carrier", "中国移动");
        device.put("networkType", "WIFI");
        device.put("country", "CN");
        return device;
    }


}
