# NGSI-LD Wrapper for NGSIv2

[![MIT license][license-image]][license-url]

The purpose of this project is to create an implementation of [NGSI-LD](https://docbox.etsi.org/ISG/CIM/Open/ISG_CIM_NGSI-LD_API_Draft_for_public_review.pdf), based on a wrapper (incarnated by a proxy) on top of FIWARE NGSI. Leveraging on FIWARE NGSI, NGSI-LD is an international standard developed by [ETSI ISG CIM](https://portal.etsi.org/tb.aspx?tbid=854&SubTB=854), intended to provide, consume and subscribe to context information in multiple scenarios and involving multiple stakeholders. It enables close to real-time access to information coming from many different sources (not only IoT). 

The OMA NGSI-9/10 information model, the basis of FIWARE NGSI, is currently being evolved by ETSI CIM to better support linked data (entity's relationships), [property graphs](https://neo4j.com/lp/book-graph-databases/) and semantics (exploiting the capabilities offered by [JSON-LD](https://json-ld.org/primer/latest/)).  The resulting specification has been named **NGSI-LD**. It is noteworthy that the NGSI-LD information model is a generalization of the OMA NGSI-9/10 information model. As a result, it is expected a good level of compatibility and a clear migration path between both information models.  

In the NGSI-LD information model, there are **Entities**, **Properties** and **Relationships**. Entities (instances) can be the subject of other Properties or Relationships. In terms of the traditional NGSI data model, Properties can be seen as the combination of an attribute (property) and its value. Relationships allow to establish "links" between instances using JSON-LD conventions. In practice, they are similar to NGSI attributes, but with an special value (named `object`) which happens to be a URI which points to another entity residing in the same system or externally. They are, somewhat, similar to the “ref” attributes present in the [Fiware Data Models](http://schema.fiware.org).

Properties and Relationships can be the subject of other Properties or Relationships. Thus, in the NGSI-LD information model there are no attribute’s metadata but just “properties of properties”. It is not expected to have infinite graphs and in practice only one or two levels of property or relationship “chaining” will happen. Typically, there will be one, equivalent to the NGSI metadata abstraction. 

This wrapper works on top of the [FIWARE Context Broker](https://github.com/fiware/context.Orion) and basically adapts between NGSIv2 (JSON) representations (http://fiware.github.io/specifications/ngsiv2/latest/) and the **NGSI-LD** (JSON-LD) representations.

## See also:

https://github.com/fiware/dataModels

https://github.com/fiware/context.Orion

[license-image]: https://img.shields.io/badge/license-MIT-blue.svg
[license-url]: LICENSE
