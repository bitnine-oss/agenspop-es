# Agenspop-es

This project is for AgensGraph v3 based on Apache Tinkerpop using Elasticsearch as storage.
This application can be running by stand alone and have graph visualization, called by `graph-workspace`.
This is a part of Agenspop-es and backend project.
 
## _`agenspop-es`_ main features

### Rest-API

#### 전체 그래프 탐색

* [<GET> ~/search/{datasource}/v](http://localhost:8080/api/search/modern/v)
* [<GET> ~/search/{datasource}/e](http://localhost:8080/api/search/modern/e)

#### vertex, edge 공통 search API

* [<GET> ~/search/{datasource}/{type}/neighbors?q={vid}](http://localhost:8080/api/search/modern/v/neighbors?q=modern_4)
* [<GET> ~/search/{datasource}/{type}/ids?q={[ids]}](http://localhost:8080/api/search/modern/v/neighbors?q=modern_4)
  - POST: [<POST> ~/search/{datasource}/{type}/ids](about:blank)
* [<GET> ~/search/{datasource}/{type}/labels?q={labels}](http://localhost:8080/api/search/modern/v/labels?q=person,software)
* [<GET> ~/search/{datasource}/{type}/keys?q={keys}](http://localhost:8080/api/search/modern/v/keys?q=country,age)
* [<GET> ~/search/{datasource}/{type}/key?q={key}&hasNot={true/false}](http://localhost:8080/api/search/modern/v/key?q=lang&hasNot=true)
* [<GET> ~/search/{datasource}/{type}/values?q={values}](http://localhost:8080/api/search/modern/v/values?q=USA,marko)
* [<GET> ~/search/{datasource}/{type}/value?q={value}](http://localhost:8080/api/search/modern/v/value?q=jav)
* [<GET> ~/search/{datasource}/{type}/keyvalue?key={key}&value={value}](http://localhost:8080/api/search/modern/v/keyvalue?key=country&value=USA)
* [<GET> ~/search/{datasource}/{type}/labelkeyvalue?label={label}&key={key}&value={value}](http://localhost:8080/api/search/modern/v/labelkeyvalue?label=software&key=lang&value=java)

#### edge 전용 search API

* [<GET> ~/search/{datasource}/e/connected?q={vids}](http://localhost:8080/api/search/modern/e/connected?q=modern_1,modern_3,modern_6)

#### Admin API

* [<GET> ~/admin/config](http://localhost:8080/api/admin/config)
* [<GET> ~/admin/graphs](http://localhost:8080/api/admin/graphs)
* [<GET> ~/admin/update](http://localhost:8080/api/admin/update)
* [<GET> ~/admin/remove/{datasource}](http://localhost:8080/api/admin/remove/modern)
* [<GET> ~/admin/labels/{datasource}](http://localhost:8080/api/admin/labels/modern)
* [<GET> ~/admin/keys/{datasource}/{label}](http://localhost:8080/api/admin/keys/modern/person)
* [<GET> ~/admin/reset/sample](http://localhost:8080/api/admin/reset/sample)

#### graph API

* [<GET> ~/graph/gremlin?q={gremlin_query}](http://localhost:8080/api/graph/gremlin?q=modern_g.V().has(%27age%27,gt(30)))
* [<GET> ~/graph/cypher?ds={datasource}&q={cypher_query}](http://localhost:8080/api/graph/cypher?ds=modern&q=match%20(a:person%20%7Bcountry:%20%27USA%27%7D)%20return%20a,%20id(a)%20limit%2010)

### Config-file 

This application was made by spring-boot 2 with MVC.
So all items in application.yml follows Spring config.
Default-config file named 'es-config.yml' are created when maven build.

```
## config context path to "/" by setting an empty string
server:
  port: 8080
  error:
    whitelabel:
      enabled: false            # disable default error-page           

spring:
  pid:
    file: agenspop.pid          # pid filename when start process (not working) 스프링의 설명대로 했는데, 생성 안됨
  main:
    banner-mode: "off"          # hide spring banner when start process
  resources:
    cache:
      period: 3600              # seconds

agens:
  api:
    base-path: /api
    query-timeout: 600000       # 1000 ms = 1 sec
  elasticsearch:
    # host: 27.117.163.21
    # port: 15619
    host: 192.168.0.54                <-- 접속할 ES의 IP
    port: 8087                              <-- 접속할 ES의 PORT
    username:                             <-- 접속할 ES의 username (ES에 설정한 바가 없으면 빈칸)
    password:                             <-- 접속할 ES의 password
    scroll-limit: -1            # -1(unlimit) or more than 2500        <-- ES 로부터 데이터 가져올 때 읽어들이는 fetch size (전부 가져오게 허용할지 한도를 정할지), 가령 2500 이라고 하면 1만개가 있어도 2500개만 읽어옴
    vertex-index: elasticvertex     <-- ES의 vertex용 index 이름 (없으면 이 이름으로 index 생성함)
    edge-index: elasticedge         <-- ES의 edge용 index 이름 (없으면 이 이름으로 index 생성함)
    edge-validation: false      # if true, check valid edge on each request (make slow)      <-- edge 읽어들일때마다, 연결된 vertex 의 존재 유무를 확인하면서 가져오는 옵션 (데이터 무결성 중요할 때 사용, 하지만 매우 느려진다)
    index-shards: 1             # when create index, apply to setting             <-- index 가 없어서 생성할 때 샤드 설정값
    index-replicas: 0           # when create index, apply to setting             <-- index 가 없어서 생성할 때 리플리카 설정값
  product:
    name: agenspop-es                 <-- API 의 응답 header 에 포함되는 항목 (agens.product.name) 클라이언트에게 서버 버전을 알려주기 위한 용도
    version: 0.7.3-dev                    <-- API 의 응답 header 에 포함되는 항목 (agens.product.version)
    hello-msg: agenspop-elasticgraph v1.0 (since 2019-08-01)      <-- agenspop 서버 기동시 나타나는 메시지 문장 
  config:
    debug: false                # front 쪽의 전달할 설정내용 - debug 사용시 브라우저의 개발자모드에서 콘솔 로그 확인 가능 (시간측정 포함)
```

### AgensGraph

processing transaction with traversaling vertices and edges

### Elasticsearch indexing

fulltext search about vertices, edges and properties

simple statistics by aggregation of elasticsearch
 
### CytoSON Serializer, Deserializer

convert Graph, Vertices, Edges to Objects of Cytoscape.js  
