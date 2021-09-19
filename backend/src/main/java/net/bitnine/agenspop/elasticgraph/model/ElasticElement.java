package net.bitnine.agenspop.elasticgraph.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.bitnine.agenspop.basegraph.model.BaseElement;
import net.bitnine.agenspop.basegraph.model.BaseProperty;
import net.bitnine.agenspop.elasticgraph.util.ElasticHelper;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class ElasticElement implements BaseElement {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean removed;

    // **NOTE: 굳이 LocalDateTime 을 유지할 필요가 있을까? 변환하느라 약간 느려짐
    // ==> gremlin 에서 시간 쿼리를 하기 위해??
    // ==> 일단은 String 으로 처리하자! (date range 는 elasticsearch 에서 처리)
    
    // @JsonProperty("@timestamp")
    // protected Long timestamp;     // epoch_millis (elasticsearch default format)
                                     // => convert to LocalDateTime "yyyy-MM-dd'T'HH:mm:ssZ"
    protected String id;
    protected String label;
    protected String datasource;
    protected List<ElasticProperty> properties;

    protected ElasticElement(String datasource, String id, String label){
        this.datasource = datasource;
        this.id = id;
        this.label = label;
        this.properties = new ArrayList<>();
        this.removed = false;
    }
//    protected ElasticElement(String datasource, String id, String label){
//        // if empty, return String of LocalDateTime.now()
//        this(ElasticHelper.date2str(Optional.empty()), datasource, id, label);
//    }

    @Override
    public boolean notexists(){ return this.removed; }
    @Override
    public void remove(){
        this.removed = true;
        this.properties = null;
    }

    @Override
    public Collection<String> keys(){
        if( this.removed ) return Collections.EMPTY_LIST;

        List<String> keys = new ArrayList<>();
        for(ElasticProperty p : properties ){
            if( p.canRead() ) keys.add(p.getKey());   // pre-check exception
        }
        return keys;
    }

    @Override
    public Collection<Object> values(){
        if( this.removed ) return Collections.EMPTY_LIST;

        List<Object> values = new ArrayList<>();
        for(ElasticProperty p : properties ){
            if( p.canRead() ) values.add(p.value());  // pre-check exception
        }
        return values;
    }

    @Override
    public Collection<BaseProperty> properties(){
        if( this.removed ) return Collections.EMPTY_SET;

        Set<BaseProperty> propertySet = new HashSet<>(properties);
        return propertySet;
    }

    @Override
    public void properties(Collection<? extends BaseProperty> properties){
        if( this.removed ) return;

        this.properties = properties.stream().map(r->(ElasticProperty)r).collect(Collectors.toList());
    }

    @Override
    public boolean hasProperty(String key){
        if( this.removed ) return false;
        return keys().contains(key);
    }

    @Override
    public BaseProperty getProperty(String key){
        if( this.removed ) return null;

        for( ElasticProperty property : properties ){
            if( property.getKey().equals(key) ) return property;
        }
        return null;
    }

    @Override
    public BaseProperty getProperty(String key, BaseProperty defaultProperty){
        if( this.removed ) return null;

        BaseProperty property = getProperty(key);
        return property == null ? defaultProperty : property;
    }

    @Override
    public void setProperty(BaseProperty property){
        if( this.removed ) return;

        if( hasProperty(property.key()) ) removeProperty(property.key());
        properties.add((ElasticProperty) property);
    }

    @Override
    public BaseProperty removeProperty(String key){
        if( this.removed ) return null;

        Iterator<ElasticProperty> iter = properties.iterator();
        while(iter.hasNext()){
            BaseProperty property = iter.next();
            if( property.key().equals(key) ){
                iter.remove();
                return property;
            }
        }
        return null;
    }
}
