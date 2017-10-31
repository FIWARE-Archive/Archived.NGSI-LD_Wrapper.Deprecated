package org.fiware.ngsi_ld.comp;

import org.fiware.ngsi_ld.C3IMEntity;
import org.fiware.ngsi_ld.C3IMPropertySt;
import org.fiware.ngsi_ld.impl.C3IMEntityImpl;
import org.fiware.ngsi_ld.impl.C3IMPropertyStImpl;

import javax.json.JsonObject;
import javax.json.JsonValue;

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
                JsonValue value;
                JsonObject ngsiStructure = null;
                boolean hasMetadata = true;

                try {
                    ngsiStructure = obj.getJsonObject(key);
                    value = ngsiStructure.get("value");
                }
                catch(Throwable thr) {
                    value = obj.get(key);
                    hasMetadata = false;
                }

                C3IMPropertySt pst = new C3IMPropertyStImpl(key,value);
                ent.addProperty(pst);

                if (hasMetadata) {
                    JsonObject metadata = ngsiStructure.getJsonObject("metadata");
                    if (metadata != null) {
                        for (String mKey : metadata.keySet()) {
                            JsonObject metadataStructure = metadata.getJsonObject(mKey);
                            C3IMPropertySt metaPropertySt = new C3IMPropertyStImpl(mKey, metadataStructure.get("value"));
                            pst.addProperty(metaPropertySt);
                        }
                    }
                }
            }
        }

        return ent;
    }
}
