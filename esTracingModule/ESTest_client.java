package es;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

public class ESTest_client {


    /**
     * create es index
     *
     * @param client es client
     * @return boolean
     */
    public static boolean createIndex(RestHighLevelClient client, String indexName) {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        try {
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            return createIndexResponse.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void queryIndex(RestHighLevelClient client, String indexName) {
        try {
            GetIndexRequest request = new GetIndexRequest(indexName);
            GetIndexResponse getIndexResponse = client.indices().get(request, RequestOptions.DEFAULT);
            System.out.println("*******index info*******");
            System.out.println(getIndexResponse.getAliases());
            System.out.println(getIndexResponse.getMappings());
            System.out.println(getIndexResponse.getSettings());
            System.out.println("************************");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteIndex(RestHighLevelClient client, String indexName) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
            return delete.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void insert(RestHighLevelClient client, String indexName, String idName) {
        int id = 1;
        try {
            List<WrapperTracingTo> list = TracingToUtils.parseTraceData();
            for (WrapperTracingTo wrapperTracingTo : list) {
                ObjectMapper mapper = new ObjectMapper();
                String s = mapper.writeValueAsString(wrapperTracingTo);
                IndexRequest request = new IndexRequest();
                request.index(indexName).id(idName + (id++));
                request.source(s, XContentType.JSON);
                IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                System.out.println("*******insert span*******");
                System.out.println(response.getResult());
                System.out.println(response.getId());
                System.out.println("************************");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void bulkInsert(RestHighLevelClient client, String indexName, String idName) {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest().index(indexName).id(idName).source(XContentType.JSON, "", ""));
        request.add(new IndexRequest().index(indexName).id(idName).source(XContentType.JSON, "", ""));
        request.add(new IndexRequest().index(indexName).id(idName).source(XContentType.JSON, "", ""));

        try {
            BulkResponse bulk = client.bulk(request, RequestOptions.DEFAULT);
            System.out.println("*******bulk insert span*******");
            System.out.println(bulk.getTook());
            System.out.println(bulk.getItems());
            System.out.println("******************************");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void update(RestHighLevelClient client, String indexName, String idName) {
        UpdateRequest request = new UpdateRequest();
        request.index(indexName).id(idName);
        request.doc(XContentType.JSON, "endTime", "33333");
        try {
            UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
            System.out.println(update.getGetResult());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void query(RestHighLevelClient client, String indexName, String idName) {
        GetRequest request = new GetRequest();
        request.index(indexName).id(idName);

        try {
            GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
            System.out.println(documentFields.getSourceAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(RestHighLevelClient client, String indexName, String idName) {
        DeleteRequest request = new DeleteRequest();
        request.index(indexName).id(idName);

        try {
            DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
            System.out.println(delete.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void bulkDelete(RestHighLevelClient client, String indexName, String idName) {
        BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest().index(indexName).id(idName));
        request.add(new IndexRequest().index(indexName).id(idName));
        request.add(new IndexRequest().index(indexName).id(idName));

        try {
            BulkResponse bulk = client.bulk(request, RequestOptions.DEFAULT);
            System.out.println("*******bulk delete span*******");
            System.out.println(bulk.getTook());
            System.out.println(bulk.getItems());
            System.out.println("******************************");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void search1(RestHighLevelClient client, String indexName) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search all
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void search2(RestHighLevelClient client, String indexName) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search by some conditions
        request.source(new SearchSourceBuilder().query(QueryBuilders.termQuery("serviceName", "nova-api")));
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void search3(RestHighLevelClient client, String indexName, int start, int size) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search by page
        request.source();
        try {
            SearchSourceBuilder query = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
            query.from(start);
            query.size(size);
            request.source(query);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public static void search4(RestHighLevelClient client, String indexName) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search all
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        try {
            SearchSourceBuilder query = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
            query.sort("start", SortOrder.ASC);
            request.source(query);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void search5(RestHighLevelClient client, String indexName) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search all
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        try {
            SearchSourceBuilder query = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
            String[] excludes = {};
            String[] includes = {"serviceName"};
            query.fetchSource(includes, excludes);
            request.source(query);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void search6(RestHighLevelClient client, String indexName) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search all
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        try {
            SearchSourceBuilder builder = new SearchSourceBuilder();

            BoolQueryBuilder boolQueryBuilder = boolQuery();
            boolQueryBuilder.must(QueryBuilders.matchQuery("serviceName", "nova-api"));
            boolQueryBuilder.must(QueryBuilders.matchQuery("traceId", "12345"));


            builder.query(boolQueryBuilder);
            request.source(builder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void search7(RestHighLevelClient client, String indexName) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search all
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        try {
            SearchSourceBuilder builder = new SearchSourceBuilder();

            RangeQueryBuilder rangeQueryBuilder = rangeQuery("startTime");
            rangeQueryBuilder.gte("11111");
            rangeQueryBuilder.lte("22222");
            builder.query(rangeQueryBuilder);
            request.source(builder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void search8(RestHighLevelClient client, String indexName) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search all
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        try {
            SearchSourceBuilder builder = new SearchSourceBuilder();

            FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("startTime", "1111").fuzziness(Fuzziness.ONE);
            builder.query(fuzzyQueryBuilder);
            request.source(builder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public static void search9(RestHighLevelClient client, String indexName) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search all
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        try {
            SearchSourceBuilder builder = new SearchSourceBuilder();

            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("serviceName", "api");
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<font color:'red'>");
            highlightBuilder.postTags("</font>");
            highlightBuilder.field("serviceName");

            builder.highlighter(highlightBuilder);
            builder.query(termQueryBuilder);
            request.source(builder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void search10(RestHighLevelClient client, String indexName) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search all
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        try {
            SearchSourceBuilder builder = new SearchSourceBuilder();

            AggregationBuilder aggregationBuilder = AggregationBuilders.avg("endtime");
            TermsAggregationBuilder serviceName = AggregationBuilders.terms("serviceName");
            builder.aggregation(aggregationBuilder);
            request.source(builder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public static void search11(RestHighLevelClient client, String indexName) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search all
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        try {
            String startTime = "";
            String endTime = "";
            SearchSourceBuilder builder = new SearchSourceBuilder();
            BoolQueryBuilder filter = boolQuery()
                    .must(rangeQuery("timestamp_millis")
                            .gte(startTime)
                            .lte(endTime)).must(QueryBuilders.termQuery("serviceName", "nova-api"));

            builder.query(filter);
            request.source(builder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);


            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static SearchHits search12(RestHighLevelClient client, String indexName, String fieldName, String value) {
        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        // search all
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        try {

            SearchSourceBuilder builder = new SearchSourceBuilder();
            MatchPhraseQueryBuilder matchPhraseQueryBuilder = new MatchPhraseQueryBuilder(fieldName, value);

            builder.query(matchPhraseQueryBuilder);
            request.source(builder);

            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            System.out.println(response.getTook());
            return response.getHits();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return null;
    }

    public static void search13(RestHighLevelClient client, String indexName, String fieldName, List<String> values) {
        try {
            SearchRequest request = new SearchRequest();
            request.indices(indexName);
            TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder(fieldName, values);
            SearchSourceBuilder builder = new SearchSourceBuilder();
            builder.query(termsQueryBuilder);
            request.source(builder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            System.out.println(response.getTook());
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void search14aggregate(RestHighLevelClient client, String indexName, String fieldName) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        try {
            TermsAggregationBuilder byId = AggregationBuilders.terms("by_id")
                    .field(fieldName + ".keyword");
            builder.aggregation(byId);
            searchRequest.source(builder);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            ParsedStringTerms result = response.getAggregations().get("by_id");
            for (Terms.Bucket bucket : result.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                long docCount = bucket.getDocCount();
                System.out.println(keyAsString + "=====" + docCount);
            }
//            SearchHits hits = response.getHits();
//            System.out.println(hits.getTotalHits());
//            System.out.println(response.getTook());
//            for (SearchHit hit : hits) {
//                System.out.println(hit.getSourceAsString());
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws Exception {
        System.out.println("start");

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

//         create es index
//        System.out.println("create index : " + createIndex(client, "trace_span"));

//         query es index
//        System.out.println("query es index....");
//        queryIndex(client, "trace_span");

        // delete es index
//        System.out.println("delete es index....");
//        System.out.println(deleteIndex(client, "tracing"));

        // inset es data
//        insert(client, "trace_span", "iaas");

        // update es data
//        update(client, "trace_span", "iaas");

//        query(client, "trace_span", "iaas");

//        delete(client,"trace_span", "iaas" );

/**
 SearchHits hits1 = search12(client, "trace_span", "source", "mounicollectservice/omglobal");
 System.out.println(hits1.getTotalHits());
 for (SearchHit hit : hits1) {
 System.out.println(hit.getSourceAsString());
 }

 System.out.println("================================");

 SearchHits hits2 = search12(client, "trace_span", "target", "mounicollectservice/omglobal");
 System.out.println(hits2.getTotalHits());
 for (SearchHit hit : hits2) {
 System.out.println(hit.getSourceAsString());
 }
 **/
//        List<String> values = new ArrayList<>();
//        values.add("12442");
//        values.add("12444");
//        search13(client, "trace_span", "index", values);

        search14aggregate(client, "trace_span", "source");
        try {
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("stop");
    }
}
