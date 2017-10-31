package org.fiware.ngsi_ld.comp;


import org.fiware.ngsi_ld.C3IMPropertySt;
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

        builder.add("id", e.getId());
        builder.add("type", e.getType());

        // Iterate over the attributes of the C3IM entity
        Map<String,C3IMPropertySt> props = e.getProperties();

        Set<String> keys = props.keySet();
        for (String key:keys) {
            C3IMPropertySt prop = props.get(key);

            adaptPropertyStToJson(builder,key, prop);
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

        if (propsOfProp.size() == 0) {
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
}
