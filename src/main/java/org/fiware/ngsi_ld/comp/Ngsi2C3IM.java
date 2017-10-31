package org.fiware.ngsi_ld.comp;

import org.fiware.ngsi_ld.C3IMEntity;
import org.fiware.ngsi_ld.C3IMObject;
import org.fiware.ngsi_ld.C3IMPropertySt;
import org.fiware.ngsi_ld.C3IMRelationshipSt;
import org.fiware.ngsi_ld.impl.C3IMEntityImpl;
import org.fiware.ngsi_ld.impl.C3IMPropertyStImpl;
import org.fiware.ngsi_ld.impl.C3IMRelationshipStImpl;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.URI;

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
}
