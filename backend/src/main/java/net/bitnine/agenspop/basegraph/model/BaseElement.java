package net.bitnine.agenspop.basegraph.model;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public interface BaseElement {

    public static final String createdTag = "_$$created";   // "@timestamp" 는 Java에서 변수명이 못됨;
    public static final String dummyTime = " 00:00:05";
    public static final DateTimeFormatter createdFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter[] validFormatters = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")
    };

    String getId();
    String getLabel();
    String getDatasource();

    boolean notexists();
    void remove();

    Collection<String> keys();
    Collection<Object> values();

    Collection<BaseProperty> properties();
    void properties(Collection<? extends BaseProperty> properties);

    boolean hasProperty(String key);
    BaseProperty getProperty(String key);
    BaseProperty getProperty(String key, BaseProperty defaultProperty);

    // upsert
    void setProperty(BaseProperty property);
    // delete
    BaseProperty removeProperty(String key);

}
