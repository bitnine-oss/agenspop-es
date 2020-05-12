package net.bitnine.agenspop.elasticgraph.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.bitnine.agenspop.basegraph.model.BaseElement;
import net.bitnine.agenspop.basegraph.model.BaseProperty;
import net.bitnine.agenspop.elasticgraph.util.ElasticHelper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class ElasticElement implements BaseElement {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean removed;

    // **NOTE: elasticsearch date type
    // https://www.elastic.co/guide/en/elasticsearch/reference/current/date.html
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    // protected LocalDateTime created;   // Date format: "yyyy-MM-dd HH:mm:ss"

    // **NOTE: 굳이 LocalDateTime 을 유지할 필요가 있을까? 변환하느라 약간 느려짐
    // ==> gremlin 에서 시간 쿼리를 하기 위해??
    // ==> 일단은 String 으로 처리하자! (date range 는 elasticsearch 에서 처리)
    protected String created;   // Date format: "yyyy-MM-dd HH:mm:ss"

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
        keys.add(BaseElement.createdTag);
        for(ElasticProperty p : properties ){
            if( p.canRead() ) keys.add(p.getKey());   // pre-check exception
        }
        return keys;
    }

    @Override
    public Collection<Object> values(){
        if( this.removed ) return Collections.EMPTY_LIST;

        List<Object> values = new ArrayList<>();
        values.add(this.created);
        for(ElasticProperty p : properties ){
            if( p.canRead() ) values.add(p.value());  // pre-check exception
        }
        return values;
    }

    @Override
    public Collection<BaseProperty> properties(){
        if( this.removed ) return Collections.EMPTY_SET;

        Set<BaseProperty> propertySet = new HashSet<>(properties);
        propertySet.add(getCreatedProperty());
        return propertySet;
    }

    @JsonIgnore
    private ElasticProperty getCreatedProperty(){
        return new ElasticProperty(BaseElement.createdTag, this.created);
    }

    @Override
    public void properties(Collection<? extends BaseProperty> properties){
        if( this.removed ) return;

        this.properties = properties.stream().map(r->(ElasticProperty)r).collect(Collectors.toList());
        // if exists, set created from property
        String created = ElasticHelper.getCreatedDate((List<ElasticProperty>)properties);
        if( created != null ) this.created = ElasticHelper.date2str(ElasticHelper.str2date(created));
        // else this.created = ElasticHelper.date2str(LocalDateTime.now());     // Do it at save()
    }

    @Override
    public boolean hasProperty(String key){
        if( this.removed ) return false;
        return keys().contains(key) || key.equals(BaseElement.createdTag);
    }

    @Override
    public BaseProperty getProperty(String key){
        if( this.removed ) return null;

        for( ElasticProperty property : properties ){
            if( property.getKey().equals(key) ) return property;
        }
        // **NOTE : created property 는 properties() 호출시 생성
        if( key.equals(BaseElement.createdTag) ) return getCreatedProperty();
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
        // **NOTE : update created field
        if( property.key().equals(BaseElement.createdTag) && ElasticHelper.checkDateformat(property.valueOf()) )
            this.created = ElasticHelper.date2str(ElasticHelper.str2date(property.valueOf()));
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
