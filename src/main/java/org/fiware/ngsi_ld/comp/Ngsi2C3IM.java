package org.fiware.ngsi_ld.comp;

import org.fiware.JsonUtilities;
import org.fiware.ngsi_ld.C3IMEntity;
import org.fiware.ngsi_ld.C3IMObject;
import org.fiware.ngsi_ld.C3IMPropertySt;
import org.fiware.ngsi_ld.C3IMRelationshipSt;
import org.fiware.ngsi_ld.impl.C3IMEntityImpl;
import org.fiware.ngsi_ld.impl.C3IMPropertyStImpl;
import org.fiware.ngsi_ld.impl.C3IMRelationshipStImpl;

import javax.json.*;
import javax.json.bind.JsonbBuilder;
import javax.json.stream.JsonGenerator;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 *
 *   Transforms NGSI data to C3IM
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
public class Ngsi2C3IM {
    /**
     *
     *  Converts a Json normalized representation of NGSIv2 to a C3IM Entity
     *
     * @param obj
     * @return
     */
    public static C3IMEntity toC3IM(JsonObject obj) {
        String id = obj.getString("id");
        String type = obj.getString("type");

        C3IMEntity ent = new C3IMEntityImpl(id, type);

        for(String key:obj.keySet()) {
            if(!key.equals("id") && !key.equals("type")) {
                JsonValue value;
                JsonObject ngsiStructure = null;
                String attrType = null;


                ngsiStructure = obj.getJsonObject(key);
                value = ngsiStructure.get("value");
                attrType = ngsiStructure.getString("type");

                C3IMObject c3imObj;
                if (attrType != null && !attrType.equals("Reference")) {
                    c3imObj = new C3IMPropertyStImpl(key, value);
                    ent.addProperty((C3IMPropertySt)c3imObj);
                }
                else {
                    String valStr = ngsiStructure.getString("value");
                    System.out.println(valStr);
                    c3imObj = new C3IMRelationshipStImpl(key, valStr);
                    ent.addRelationship((C3IMRelationshipSt)c3imObj);
                }

                JsonObject metadata = ngsiStructure.getJsonObject("metadata");
                if (metadata != null) {
                    for (String mKey : metadata.keySet()) {
                        JsonObject metadataStructure = metadata.getJsonObject(mKey);
                        C3IMPropertySt metaPropertySt = new C3IMPropertyStImpl(mKey, metadataStructure.get("value"));
                        c3imObj.addProperty(metaPropertySt);
                    }
                }
            }
        }

        return ent;
    }

    /**
     *
     *   Converts a C3IM Entity to a normalized NGSIv2 JSON representation
     *
     *
     * @param ent
     * @return
     */
    public static JsonObject toNgsi(C3IMEntity ent) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("id", ent.getId());
        builder.add("type", ent.getType());

        Map<String, C3IMPropertySt> properties = ent.getProperties();

        for(C3IMPropertySt prop:properties.values()) {
            JsonObjectBuilder valueBuilder = Json.createObjectBuilder();
            String attrName = prop.getPropertyId();
            JsonObjectBuilder metadataBuilder = Json.createObjectBuilder();

            Map<String, C3IMPropertySt> propsOfProps = prop.getProperties();
            addProperties(propsOfProps,metadataBuilder);

            Map<String, C3IMRelationshipSt> relsOfProps = prop.getRelationships();
            addRelationships(relsOfProps, metadataBuilder);

            valueBuilder.add("metadata", metadataBuilder.build());
            builder.add(attrName, toNgsiAttr(valueBuilder, prop));
        }

        Map<String, C3IMRelationshipSt> rels = ent.getRelationships();

        for(C3IMRelationshipSt rel:rels.values()) {
            JsonObjectBuilder valueBuilder = Json.createObjectBuilder();
            String attrName = rel.getRelationshipId();
            JsonObjectBuilder metadataBuilder = Json.createObjectBuilder();

            Map<String, C3IMPropertySt> propsOfRels = rel.getProperties();
            addProperties(propsOfRels, metadataBuilder);

            Map<String, C3IMRelationshipSt> relsOfRels = rel.getRelationships();
            addRelationships(relsOfRels, metadataBuilder);

            valueBuilder.add("metadata", metadataBuilder.build());
            builder.add(attrName, toNgsiAttr(valueBuilder, rel));
        }

        return builder.build();
    }

    private static void addProperties(Map<String, C3IMPropertySt> props, JsonObjectBuilder builder) {
        for(C3IMPropertySt prop:props.values()) {
            JsonObjectBuilder metadataValueBuilder = Json.createObjectBuilder();
            String metadataName = prop.getPropertyId();
            builder.add(metadataName, toNgsiAttr(metadataValueBuilder, prop));
        }
    }

    private static void addRelationships(Map<String, C3IMRelationshipSt> rels, JsonObjectBuilder builder) {
        for(C3IMRelationshipSt relOfProp:rels.values()) {
            JsonObjectBuilder metadataValueBuilder = Json.createObjectBuilder();
            String metadataName = relOfProp.getRelationshipId();
            builder.add(metadataName, toNgsiAttr(metadataValueBuilder, relOfProp));
        }
    }

    private static JsonObject toNgsiAttr(JsonObjectBuilder builder, C3IMPropertySt prop) {
        Object value = prop.getValue();
        JsonUtilities.addValue(builder, "value", value);

        if(prop.getDataType() != null) {
            builder.add("type", prop.getDataType());
        }

        return builder.build();
    }

    private static JsonObject toNgsiAttr(JsonObjectBuilder builder, C3IMRelationshipSt rel) {
        String object = rel.getObject().toString();
        JsonUtilities.addValue(builder, "value", object);

        builder.add("type", "Reference");

        return builder.build();
    }

    public static void main(String[] args) {
        C3IMEntity ent = new C3IMEntityImpl("urn:c3im:Test:abcde", "Test");

        C3IMPropertySt propSt = new C3IMPropertyStImpl("testProperty", 45);
        C3IMRelationshipSt relSt = new C3IMRelationshipStImpl("testRel", "urn:c3im:Test2:abcdef");
        ent.addRelationship(relSt);
        ent.addProperty(propSt);

        C3IMRelationshipSt relst2 = new C3IMRelationshipStImpl("relOfProp",
                "urn:c3im:Test3:xxxxx");

        propSt.addProperty(new C3IMPropertyStImpl("timestamp",
                "2017-10-22T12:00:00","DateTime"));
        propSt.addRelationship(relst2);

        relSt.addProperty(new C3IMPropertyStImpl("propOfRel", "TestValue"));

        JsonObject obj = Ngsi2C3IM.toNgsi(ent);

        JsonWriter writer = Json.createWriterFactory(
                Collections.singletonMap(
                        JsonGenerator.PRETTY_PRINTING,
                        true
                )
        ).createWriter(System.out);

        writer.writeObject(obj);
    }
}
