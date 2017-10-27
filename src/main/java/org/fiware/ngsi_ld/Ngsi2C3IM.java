package org.fiware.ngsi_ld;

import javax.json.JsonObject;

public class Ngsi2C3IM {
    public static C3IMEntity transform(JsonObject obj) {
        String id = obj.getString("id");
        String type = obj.getString("type");

        return new C3IMEntity(id, type);
    }
}
