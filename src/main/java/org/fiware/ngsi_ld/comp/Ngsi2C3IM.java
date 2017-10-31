package org.fiware.ngsi_ld.comp;

import org.fiware.ngsi_ld.C3IMEntity;
import org.fiware.ngsi_ld.C3IMPropertySt;
import org.fiware.ngsi_ld.impl.C3IMEntityImpl;
import org.fiware.ngsi_ld.impl.C3IMPropertyStImpl;

import javax.json.JsonObject;

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
    public static C3IMEntity transform(JsonObject obj) {
        String id = obj.getString("id");
        String type = obj.getString("type");

        C3IMEntity ent = new C3IMEntityImpl(id, type);

        for(String key:obj.keySet()) {
            if(!key.equals("id") && !key.equals("type")) {
                JsonObject ngsiStructure = obj.getJsonObject(key);
                C3IMPropertySt pst = new C3IMPropertyStImpl(key,ngsiStructure.get("value"));
                ent.addProperty(pst);

                JsonObject metadata = ngsiStructure.getJsonObject("metadata");
                if (metadata != null) {
                    for(String mKey:metadata.keySet()) {
                        JsonObject metadataStructure = metadata.getJsonObject(mKey);
                        C3IMPropertySt metaPropertySt = new C3IMPropertyStImpl(mKey, metadataStructure.get("value"));
                        pst.addProperty(metaPropertySt);
                    }
                }
            }
        }

        return ent;
    }
}
