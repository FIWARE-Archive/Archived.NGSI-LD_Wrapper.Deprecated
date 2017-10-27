package org.fiware.ngsi_ld;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.bind.adapter.JsonbAdapter;
import java.util.Map;
import java.util.Set;

public class C3IMEntityAdapter implements JsonbAdapter<C3IMEntity, JsonObject> {
    @Override
    public JsonObject adaptToJson(C3IMEntity e) throws Exception {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("id", e.getId());
        builder.add("type", e.getType());

        // Iterate over the attributes of the C3IM entity
        Map<String,Object> attributes = e.attributes();

        Set<String> keys = attributes.keySet();
        for (String key:keys) {
            Object value = attributes.get(key);
            if (value instanceof Map) {
                // Complex serialization
            }
            else {
                builder.add(key, (Integer)attributes.get(key));
            }
        }

        return builder.build();
    }

    @Override
    public C3IMEntity adaptFromJson(JsonObject adapted) throws Exception {
        C3IMEntity e = new C3IMEntity(
                adapted.getString("id"),
                adapted.getString("type")
        );

        return e;
    }
}
