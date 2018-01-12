# NGSI-LD Wrapper for NGSIv2

The purpose of this project is to create a wrapper on top of NGSIv2 aligned with the latest specification made by the [ETSI ISG CIM](https://portal.etsi.org/tb.aspx?tbid=854&SubTB=854). This wrapper works on top of the [Orion Broker](https://github.com/fiware/context.Orion) and basically adapts between the [NGSIv2 information model](http://fiware.github.io/specifications/ngsiv2/latest/) and the NGSI-LD information model. 

The OMA NGSI information model is currently being evolved to better support linked data (entity relationships), property graphs and semantics (exploiting the capabilities offered by [JSON-LD](https://json-ld.org/primer/latest/)). This work is being conducted under the ETSI ISG CIM initiative. The resulting specification is named NGSI-LD. 

It is noteworthy that the NGSI-LD information model is a generalization of the existing FIWARE NGSI information model. As a result it is expected a good level of compatibility and a clear migration path between both information models. 

In the NGSI-LD information model, there are Entities, Properties and Relationships. Entities (instances) can be the subject of other Properties or Relationships. In terms of the traditional NGSI data model, Properties can be seen as the combination of an attribute (property) and its value. Relationships allow to establish "links" between instances using JSON-LD conventions. In practice, they are similar to NGSI attributes, but with an special value (named òbject`) which happens to be a URI which points to another entity residing in the same system or externally. They are, somewhat, similar to the “ref” attributes present in the Fiware Data Models.

Properties and Relationships can be the subject of other Properties or Relationships. Thus, in the NGSI-LD information model there are no attribute’s metadata but just “properties of properties”. It is not expected to have infinite graphs and in practice only one or two levels of property or relationship “chaining” will happen. Typically, there will be one, equivalent to the NGSI metadata abstraction. 


