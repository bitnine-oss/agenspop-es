package net.bitnine.agenspop.elasticgraph.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import net.bitnine.agenspop.basegraph.model.BaseProperty;

import java.util.NoSuchElementException;

@Data
public final class ElasticProperty implements BaseProperty {

    private String key;
    private String type;
    private String value;

    public ElasticProperty(String key, Object value){
        this.key = key;
        this.type = value.getClass().getName();
        this.value = value.toString();      // simple style

        // ObjectMapper objectMapper = new ObjectMapper();
        // this.value = objectMapper.writeValueAsString(value);
    }

    @Override
    public String key(){ return key; }

    @Override
    public String type(){ return type; }

    @Override
    public String valueOf(){ return value; }

    @Override
    public Object value() throws NoSuchElementException {
        ObjectMapper mapper = new ObjectMapper();
        if( value == null || value.length() == 0 || value.isEmpty() ){
            throw new NoSuchElementException("ElasticProperty::value");
        }

        Object translated = (Object)value;
        try {
            Class<?> clazz = Class.forName(type);
            translated = mapper.convertValue(value, clazz);
        }
        catch (Exception e){
            // **NOTE: java.util.List 처리 방안에 대해 고민이 필요하다
            //      - Object 로 value 를 받으면 변환이 가능한 듯 (불러쓰는 여러 class 들을 모두 수정해야 함)
            // System.out.printf("property: %s, %s, %s\n", key, type, value);
            // this.type = "java.lang.String";
            return value;
        }

        return translated;
    }

}
