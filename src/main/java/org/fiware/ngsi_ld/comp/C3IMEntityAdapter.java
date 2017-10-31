package org.fiware.ngsi_ld.comp;


import org.fiware.UrnValidator;
import org.fiware.ngsi_ld.C3IMPropertySt;
import org.fiware.ngsi_ld.C3IMRelationshipSt;
import org.fiware.ngsi_ld.impl.C3IMEntityImpl;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.bind.adapter.JsonbAdapter;
import java.util.Map;
import java.util.Set;

/**
 *
 *   C3IM Entity adapter to JSON
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
public class C3IMEntityAdapter implements JsonbAdapter<C3IMEntityImpl, JsonObject> {
    @Override
    public JsonObject adaptToJson(C3IMEntityImpl e) throws Exception {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        String id = e.getId();
        if (!UrnValidator.isValid(id)) {
            id = "urn" + ":" + "c3im" + ":" + e.getType() + ":" + e.getId();
        }

        builder.add("id", id);
        builder.add("type", e.getType());

        // Iterate over the properties of the C3IM entity
        Map<String,C3IMPropertySt> props = e.getProperties();

        Set<String> keys = props.keySet();
        for (String key:keys) {
            C3IMPropertySt prop = props.get(key);

            adaptPropertyStToJson(builder,key, prop);
        }

        // Iterate over the relationships of the C3IM entity
        Map<String,C3IMRelationshipSt> rels = e.getRelationships();

        Set<String> relKeys = rels.keySet();
        for (String relKey:relKeys) {
            C3IMRelationshipSt rel = rels.get(relKey);

            adaptRelationshipStToJson(builder,relKey, rel);
        }

        return builder.build();
    }

    @Override
    public C3IMEntityImpl adaptFromJson(JsonObject adapted) throws Exception {
        C3IMEntityImpl e = new C3IMEntityImpl(
                adapted.getString("id"),
                adapted.getString("type")
        );

        return e;
    }

    private void adaptPropertyStToJson(JsonObjectBuilder builder, String key, C3IMPropertySt pst) {
        Map<String,C3IMPropertySt> propsOfProp = pst.getProperties();
        Map<String,C3IMRelationshipSt> relsOfProp = pst.getRelationships();

        if (propsOfProp.size() == 0 && relsOfProp.size() == 0) {
            addValue(builder, key, pst.getValue());
        }
        else {
            JsonObjectBuilder propBuilder = Json.createObjectBuilder();
            propBuilder.add("type", "PropertyStatement");
            // TODO: This will not always be a JsonValue
            propBuilder.add("value", (JsonValue)pst.getValue());
            Set<String> keys = propsOfProp.keySet();
            for (String keyProp : keys) {
                C3IMPropertySt newSt = propsOfProp.get(keyProp);
                adaptPropertyStToJson(propBuilder, keyProp, newSt);
            }
            builder.add(key, propBuilder.build());
        }
    }

    private void adaptRelationshipStToJson(JsonObjectBuilder builder, String key, C3IMRelationshipSt relst) {
        Map<String,C3IMRelationshipSt> relsOfRels = relst.getRelationships();
        Map<String,C3IMPropertySt> propsOfRels = relst.getProperties();

        if (propsOfRels.size() == 0 && relsOfRels.size() == 0) {
            addRelObject(builder, key, relst.getObject().toString());
        }
        else {
            JsonObjectBuilder propBuilder = Json.createObjectBuilder();
            propBuilder.add("type", "RelationshipStatement");
            // TODO: This will not always be a JsonValue
            propBuilder.add("object", relst.getObject().toString());
            Set<String> keys = propsOfRels.keySet();
            for (String keyProp : keys) {
                C3IMPropertySt newSt = propsOfRels.get(keyProp);
                adaptPropertyStToJson(propBuilder, keyProp, newSt);
            }
            builder.add(key, propBuilder.build());
        }
    }

    private void addValue(JsonObjectBuilder obj, String key, Object value) {
        if (value instanceof Integer) {
            obj.add(key, (Integer)value);
        }
        else if (value instanceof String) {
            obj.add(key, (String)value);
        }
        else if (value instanceof Float) {
            obj.add(key, (Float)value);
        }
        else if (value instanceof Double) {
            obj.add(key, (Double)value);
        }
        else if (value instanceof Boolean) {
            obj.add(key, (Boolean)value);
        }
        else if (value instanceof JsonValue) {
            JsonValue val = (JsonValue)value;
            obj.add(key, val);
        }
        // Add more cases here
    }

    private void addRelObject(JsonObjectBuilder obj, String key, String object) {
        obj.add(key, object);
    }
}
