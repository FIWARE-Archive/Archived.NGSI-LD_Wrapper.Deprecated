# NGSI-LD Wrapper for NGSIv2

The purpose of this project is to create a wrapper on top of NGSIv2 aligned with the latest specification made by the ETSI ISG CIM. This wrapper works on top of the Orion Broker and basically adapts between the NGSIv2 information model and the ETSI ISG CIM information model. 

The OMA NGSI information model is currently being evolved to better support linked data (entity relationships), property graphs and semantics (exploiting the capabilities offered by JSON-LD). This work is being conducted under the ETSI ISG CIM initiative. 

It is noteworthy that the ETSI ISG CIM information model is a generalization of the existing FIWARE NGSI information model. As a result it is expected a good level of compatibility and a clear migration path between both information models. 

In the ETSI ISG CIM information model. There are Entities, Property Statements and Relationship Statements. Entities (instances) can be the subject of Property Statements or Relationship Statements. In terms of the traditional NGSI data model, Property statements can be seen as the combination of an attribute (property) and its value. Relationship Statements allow to establish relationships between instances using linked data. In practice, they are similar to NGSI attributes, but with an special value which happens to be a URI which points to another entity. They are similar to the “ref” attributes present in the Fiware Data Models.

Property Statements and Relationship Statements can be the subject of other Property Statements or Relationship Statements. Thus, in the ETSI ISG CIM information model there are no attribute’s metadata but just “properties of properties”. It is not expected to have infinite graphs and in practice only one or two levels of property or relationship “chaining” will happen. Typically, there will be one, equivalent to the NGSI metadata abstraction. 


