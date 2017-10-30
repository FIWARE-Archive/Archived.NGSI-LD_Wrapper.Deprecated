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
                C3IMPropertySt pst = new C3IMPropertyStImpl(key,obj.getValue(key));
                ent.addProperty(pst);
            }
        }

        return ent;
    }
}
