# JSON-LD Representation

## Vehicle Entity

```
{
 	"id": "urn:ngsi-ld:Vehicle:A4567",
  	"type": "Vehicle",
 	"brandName": {
   		"type": "Property",
		"value": "Mercedes"
	},
 	"isParked": {
   		"type": "Relationship",
   		"object": "urn:ngsi-ld:OffStreetParking:Downtown1",
   		"observedAt": "2017-07-29T12:00:04",
   		"providedBy": {
        		"type": "Relationship",
        		"object": "urn:ngsi-ld:Person:Bob"
     		} 
 	},
	"@context": [
		"http://uri.etsi.org/ngsi-ld/coreContext.jsonld",
		"http://example.org/cim/commonTerms.jsonld",
		"http://example.org/cim/vehicle.jsonld", 
		"http://example.org/cim/parking.jsonld" 
	]
}
