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
import java.net.URI;
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
    public static C3IMEntity toC3IM(JsonObject obj) {
        String id = obj.getString("id");
        String type = obj.getString("type");

        C3IMEntity ent = new C3IMEntityImpl(id, type);

        for(String key:obj.keySet()) {
            if(!key.equals("id") && !key.equals("type")) {
                JsonValue value;
                JsonObject ngsiStructure = null;
                boolean hasMetadata = true;
                String attrType = null;

                try {
                    ngsiStructure = obj.getJsonObject(key);
                    value = ngsiStructure.get("value");
                    attrType = ngsiStructure.getString("type");
                }
                catch(Throwable thr) {
                    value = obj.get(key);
                    hasMetadata = false;
                }

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


                if (hasMetadata) {
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
        }

        return ent;
    }

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

            for(C3IMPropertySt propOfProp:propsOfProps.values()) {
                JsonObjectBuilder metadataValueBuilder = Json.createObjectBuilder();
                String metadataName = propOfProp.getPropertyId();
                metadataBuilder.add(metadataName, toNgsiAttr(metadataValueBuilder, propOfProp));
            }

            valueBuilder.add("metadata", metadataBuilder.build());
            builder.add(attrName, toNgsiAttr(valueBuilder, prop));
        }

        Map<String, C3IMRelationshipSt> rels = ent.getRelationships();

        for(C3IMRelationshipSt rel:rels.values()) {
            JsonObjectBuilder valueBuilder = Json.createObjectBuilder();

            String attrName = rel.getRelationshipId();

            JsonObjectBuilder metadataBuilder = Json.createObjectBuilder();

            Map<String, C3IMPropertySt> propsOfRels = rel.getProperties();

            for(C3IMPropertySt propOfRel:propsOfRels.values()) {
                JsonObjectBuilder metadataValueBuilder = Json.createObjectBuilder();
                String metadataName = propOfRel.getPropertyId();
                metadataBuilder.add(metadataName, toNgsiAttr(metadataValueBuilder, propOfRel));
            }

            valueBuilder.add("metadata", metadataBuilder.build());
            valueBuilder.add("type", "Reference");
            builder.add(attrName, toNgsiAttr(valueBuilder, rel));
        }

        return builder.build();
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

        return builder.build();
    }

    public static void main(String[] args) {
        C3IMEntity ent = new C3IMEntityImpl("urn:c3im:Test:abcde", "Test");
        C3IMPropertySt st = new C3IMPropertyStImpl("testProperty", 45);
        st.addProperty(new C3IMPropertyStImpl("timestamp",
                "2017-10-22T12:00:00","DateTime"));
        ent.addProperty(st);
        C3IMRelationshipSt relSt = new C3IMRelationshipStImpl("testRel", "urn:c3im:Test2:abcdef");
        ent.addRelationship(relSt);

        JsonObject obj = Ngsi2C3IM.toNgsi(ent);

        JsonWriter writer = Json.createWriter(System.out);

        writer.writeObject(obj);
    }
}
