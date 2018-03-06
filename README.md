# NGSI-LD Wrapper for NGSIv2

The purpose of this project is to create a wrapper on top of NGSIv2 aligned with the [latest specification](https://docbox.etsi.org/ISG/CIM/Open/ISG_CIM_NGSI-LD_API_Draft_for_public_review.pdf) made by the [ETSI ISG CIM](https://portal.etsi.org/tb.aspx?tbid=854&SubTB=854). ETSI ISG CIM aims at creating an international standard API to provide, consume and subscribe to context information in multiple scenarios and involving multiple stakeholders. It enables close to real-time access to information coming from many different sources (not only IoT). 

The OMA NGSI information model is currently being evolved by CIM to better support linked data (entity relationships), property graphs and semantics (exploiting the capabilities offered by [JSON-LD](https://json-ld.org/primer/latest/)).  The resulting specification has been named **NGSI-LD**. It is noteworthy that the NGSI-LD information model is a generalization of the existing FIWARE NGSI information model. As a result it is expected a good level of compatibility and a clear migration path between both information models. 

This wrapper works on top of the [Orion Broker](https://github.com/fiware/context.Orion) and basically adapts between the [NGSIv2 information model](http://fiware.github.io/specifications/ngsiv2/latest/) and the **NGSI-LD** information model. 

In the NGSI-LD information model, there are **Entities**, **Properties** and **Relationships**. Entities (instances) can be the subject of other Properties or Relationships. In terms of the traditional NGSI data model, Properties can be seen as the combination of an attribute (property) and its value. Relationships allow to establish "links" between instances using JSON-LD conventions. In practice, they are similar to NGSI attributes, but with an special value (named `object`) which happens to be a URI which points to another entity residing in the same system or externally. They are, somewhat, similar to the “ref” attributes present in the [Fiware Data Models](http://schema.fiware.org).

Properties and Relationships can be the subject of other Properties or Relationships. Thus, in the NGSI-LD information model there are no attribute’s metadata but just “properties of properties”. It is not expected to have infinite graphs and in practice only one or two levels of property or relationship “chaining” will happen. Typically, there will be one, equivalent to the NGSI metadata abstraction. 


