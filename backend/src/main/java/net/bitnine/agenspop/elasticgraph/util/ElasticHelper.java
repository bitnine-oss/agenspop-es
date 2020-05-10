package net.bitnine.agenspop.elasticgraph.util;

import net.bitnine.agenspop.basegraph.model.BaseEdge;
import net.bitnine.agenspop.basegraph.model.BaseProperty;
import net.bitnine.agenspop.elasticgraph.model.ElasticElement;
import net.bitnine.agenspop.elasticgraph.model.ElasticProperty;
import net.bitnine.agenspop.elasticgraph.repository.ElasticVertexService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

public final class ElasticHelper {

    public static final String createdTag = "_$$created";
    public static final DateTimeFormatter createdFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String date2str(LocalDateTime value){
        if( value == null ) return null;
        return value.format(createdFormatter);
    }

    public static final boolean checkDateformat(String value){
        if( value == null ) return false;
        final DateTimeFormatter dtformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime date = LocalDateTime.parse(value, dtformatter);
            return date != null ? true : false;
        }catch (DateTimeParseException e){
            return false;
        }
    }

    public static String getCreatedDate(final List<ElasticProperty> properties) {
        for(BaseProperty property : properties) {
            if( property.key().equals(createdTag) && checkDateformat(property.valueOf()) ){
                return property.valueOf();
            }
        }
        return null;
    }

    public static void setCreatedDate(ElasticElement element){
        if( element.getCreated() != null ) return;
        String created = getCreatedDate(element.getProperties());
        if( created == null ){  // if not exists, set NOW() to created
            element.setCreated( date2str(LocalDateTime.now()) );    // set to field
            element.setProperty( element.getCreatedProperty() );    // add as property
        }
    }

    // **NOTE: stream 은 재사용이 안됨!! 특히나 무한 리스트인 경우
    //      - .peek() 함수를 사용하려 했으나 vid 필터링 과정 때문에 안됨
    //
    // **IDEA: 매 edges 함수 초입에 vid 리스트를 구하고 filter 에서 활용 (비용문제)
    //
    public static Stream<BaseEdge> filterValidEdges(
            ElasticVertexService service, String datasource, Stream<BaseEdge> stream
    ) {
        // 1) get all vids of vertices
        Set<String> vids = service.idsByDatasource(datasource);
        System.out.println("**vertex ids('"+datasource+"').size = " + vids.size());
        // 2) apply filter to edge stream
        return stream.filter(r-> vids.contains(r.getSrc()) && vids.contains(r.getDst()));
    }


/*
    // https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-search.html
    ** ScoreMode
    total : Add the original score and the rescore query score. The default.
    multiply : Multiply the original score by the rescore query score. Useful for function query rescores.
    avg : Average the original score and the rescore query score.
    max : Take the max of original score and the rescore query score.
    min : Take the min of the original score and the rescore query score.
*/

    public static BoolQueryBuilder addQueryDs(BoolQueryBuilder queryBuilder
            , String datasource){
        return queryBuilder.filter(termQuery("datasource", datasource));
    }

    public static BoolQueryBuilder addQueryLabel(BoolQueryBuilder queryBuilder
            , String label){
        return queryBuilder.filter(termQuery("label", label));
    }

    public static BoolQueryBuilder addQueryLabels(BoolQueryBuilder queryBuilder
            , String[] labels){
        return queryBuilder.filter(termsQuery("label", labels));
    }

    public static BoolQueryBuilder addQueryKey(BoolQueryBuilder queryBuilder, String key){
        return queryBuilder.must(QueryBuilders.nestedQuery("properties",
                    QueryBuilders.boolQuery().must(
                            termQuery("properties.key", key)
                    ), ScoreMode.Total));
    }

    public static BoolQueryBuilder addQueryKeyNot(BoolQueryBuilder queryBuilder, String key){
        return queryBuilder.mustNot(QueryBuilders.nestedQuery("properties",
                    QueryBuilders.boolQuery().must(
                            termQuery("properties.key", key)
                    ), ScoreMode.Total));
    }

    public static BoolQueryBuilder addQueryKeys(BoolQueryBuilder queryBuilder, String[] keys){
        for( String key : keys ){       // AND
            queryBuilder = queryBuilder.must(QueryBuilders.nestedQuery("properties",
                    QueryBuilders.boolQuery().must(
                            termQuery("properties.key", key)
                    ), ScoreMode.Total));
        }
        return queryBuilder;
    }

    public static BoolQueryBuilder addQueryValue(BoolQueryBuilder queryBuilder, String value){
        return queryBuilder.must(QueryBuilders.nestedQuery("properties",
                    QueryBuilders.boolQuery().must(
                            QueryBuilders.queryStringQuery("properties.value:\"" + value.toLowerCase() + "\"")
                    ), ScoreMode.Total));
    }

    public static BoolQueryBuilder addQueryValues(BoolQueryBuilder queryBuilder, String[] values){
        for( String value : values ){       // AND
            queryBuilder = queryBuilder.must(QueryBuilders.nestedQuery("properties",
                    QueryBuilders.boolQuery().must(
                            QueryBuilders.queryStringQuery("properties.value:\"" + value.toLowerCase() + "\"")
                    ), ScoreMode.Total));
        }
        return queryBuilder;
    }

    public static BoolQueryBuilder addQueryKeyValue(BoolQueryBuilder queryBuilder, String key, String value){
        return queryBuilder.must(QueryBuilders.nestedQuery("properties",
                    QueryBuilders.boolQuery()
                        .must(termQuery("properties.key", key.toLowerCase()))
                        .must(QueryBuilders.queryStringQuery("properties.value:\""+value.toLowerCase()+"\""))
                    , ScoreMode.Total));
    }

    public static <T> List<T> postFilterByValue(List<T> list, String filter){
        List<T> temp = new ArrayList<>();
        for( T item : list ){
            List<String> pvalues = ((ElasticElement)item).getProperties().stream().map(p->p.getValue().toLowerCase()).collect(Collectors.toList());
            if( pvalues.contains(filter) ) temp.add(item);
        }
        return temp;
    }

    public static <T> List<T> postFilterByValues(List<T> list, List<String> filters){
        List<T> temp = new ArrayList<>();
        for( T item : list ){
            List<String> pvalues = ((ElasticElement)item).getProperties().stream().map(p->p.getValue().toLowerCase()).collect(Collectors.toList());
            if( pvalues.containsAll(filters) ) temp.add(item);
        }
        return temp;
    }
}
