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
* [<POST> ~/search/{datasource}/{type}/ids](about:blank)
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


### AgensGraph

processing transaction with traversaling vertices and edges

### Elasticsearch indexing

fulltext search about vertices, edges and properties

simple statistics by aggregation of elasticsearch
 
### CytoSON Serializer, Deserializer

convert Graph, Vertices, Edges to Objects of Cytoscape.js  
