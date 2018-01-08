package org.fiware.ngsi_ld.comp;

import org.fiware.UrnValidator;
import org.fiware.ngsi_ld.CProperty;
import org.fiware.ngsi_ld.CRelationship;
import org.fiware.ngsi_ld.impl.EntityImpl;
import org.fiware.ngsi_ld.impl.CPropertyImpl;
import org.fiware.ngsi_ld.impl.CRelationshipImpl;

import javax.json.*;
import javax.json.bind.adapter.JsonbAdapter;
import java.io.FileInputStream;
import java.io.InputStream;
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
public class EntityAdapter implements JsonbAdapter<EntityImpl, JsonObject> {
    @Override
    public JsonObject adaptToJson(EntityImpl e) throws Exception {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        String id = e.getId();
        if (!UrnValidator.isValid(id)) {
            id = "urn" + ":" + "c3im" + ":" + e.getType() + ":" + e.getId();
        }

        builder.add("id", id);
        builder.add("type", e.getType());

        // Iterate over the properties of the C3IM entity
        Map<String,CProperty> props = e.getProperties();

        Set<String> keys = props.keySet();
        for (String key:keys) {
            CProperty prop = props.get(key);

            adaptPropertyStToJson(builder,key, prop);
        }

        // Iterate over the relationships of the C3IM entity
        Map<String,CRelationship> rels = e.getRelationships();

        Set<String> relKeys = rels.keySet();
        for (String relKey:relKeys) {
            CRelationship rel = rels.get(relKey);

            adaptRelationshipStToJson(builder,relKey, rel);
        }

        return builder.build();
    }

    @Override
    public EntityImpl adaptFromJson(JsonObject adapted) throws Exception {
        EntityImpl e = new EntityImpl(
                adapted.getString("id"),
                adapted.getString("type")
        );

        for (String key : adapted.keySet()) {
            if(!key.equals("id") && !key.equals("type")) {
                JsonValue val = adapted.get(key);
                String type = val.getValueType().name();
                if (!type.equals("OBJECT")) {
                    throw new Exception("400");
                }
                else {
                    JsonObject obj = val.asJsonObject();
                    String objType = obj.getString("type");
                    if (objType.equals("PropertyStatement")) {
                        e.addProperty(fromJsonToPropertySt(key, obj));
                    }
                    else if (objType.equals("RelationshipStatement")) {
                        e.addRelationship(fromJsonToRelSt(key, obj));
                    }
                    else {
                        throw new Exception("400");
                    }
                }
            }
        }

        return e;
    }

    private CProperty fromJsonToPropertySt(String propName, JsonObject obj) throws Exception {
        CProperty out = new CPropertyImpl(propName, obj.get("value"));
        String timestamp = obj.getString("timestamp", "");
        if (timestamp.length() > 0) {
            ((CPropertyImpl)out).setTimestamp(timestamp);
        }

        for(String key: obj.keySet()) {
            if (!key.equals("type") && !key.equals("value") && !key.equals("timestamp")) {
                String valueType = obj.get(key).getValueType().name();
                if (valueType.equals("OBJECT")) {
                    JsonObject keyObject = obj.get(key).asJsonObject();
                    String keyType = keyObject.getString("type");
                    if (keyType != null) {
                        if (keyType.equals("PropertyStatement")) {
                            out.addProperty(fromJsonToPropertySt(key, keyObject));
                        }
                        else if (keyType.equals("RelationshipStatement")) {
                            out.addRelationship(fromJsonToRelSt(key, keyObject));
                        }
                        else {
                            CProperty pst = new CPropertyImpl(key, keyObject);
                            out.addProperty(pst);
                        }
                    }

                }
                else {
                    throw new Exception("400");
                }
            }
        }

        return out;
    }

    private CRelationship fromJsonToRelSt(String relName, JsonObject obj) throws Exception {
        CRelationship out = new CRelationshipImpl(relName, obj.getString("object"));

        for(String key: obj.keySet()) {
            if (!key.equals("type") && !key.equals("object")) {
                String valueType = obj.get(key).getValueType().name();
                if (valueType.equals("OBJECT")) {
                    JsonObject keyObject = obj.get(key).asJsonObject();
                    String keyType = keyObject.getString("type");
                    if (keyType != null) {
                        if (keyType.equals("PropertyStatement")) {
                            out.addProperty(fromJsonToPropertySt(key, keyObject));
                        }
                        else if (keyType.equals("RelationshipStatement")) {
                            out.addRelationship(fromJsonToRelSt(key, keyObject));
                        }
                    }
                    else {
                        out.addProperty(new CPropertyImpl(key, keyObject));
                    }
                }
                else {
                    throw new Exception("400");
                }
            }
        }

        return out;
    }

    public static void main(String[] args) throws Exception {
        EntityAdapter adapter = new EntityAdapter();

        InputStream fis = new FileInputStream("/Users/jcantera/work/develop/etsi_java/simple-service/test.json");

        JsonReader reader = Json.createReader(fis);

        JsonObject obj = reader.readObject();

        reader.close();

        EntityImpl entity = adapter.adaptFromJson(obj);

        JsonObject out = adapter.adaptToJson(entity);

        JsonWriter writer = Json.createWriter(System.out);

        writer.writeObject(out);
    }

    private void adaptPropertyStToJson(JsonObjectBuilder builder, String key, CProperty pst) {
        Map<String,CProperty> propsOfProp = pst.getProperties();
        Map<String,CRelationship> relsOfProp = pst.getRelationships();

        /*
        if (propsOfProp.size() == 0 && relsOfProp.size() == 0) {
            JsonUtilities.addValue(builder, key, pst.getValue());
        }
        else { */
            JsonObjectBuilder propBuilder = Json.createObjectBuilder();
            propBuilder.add("type", "PropertyStatement");
            // TODO: This will not always be a JsonValue
            propBuilder.add("value", (JsonValue)pst.getValue());
            if (pst.getTimestamp() != null) {
                propBuilder.add("timestamp", pst.getTimestamp());
            }

            Set<String> keys = propsOfProp.keySet();
            for (String keyProp : keys) {
                CProperty newSt = propsOfProp.get(keyProp);
                adaptPropertyStToJson(propBuilder, keyProp, newSt);
            }
            builder.add(key, propBuilder.build());
        /*} */
    }

    private void adaptRelationshipStToJson(JsonObjectBuilder builder, String key, CRelationship relst) {
        Map<String,CRelationship> relsOfRels = relst.getRelationships();
        Map<String,CProperty> propsOfRels = relst.getProperties();
/*
        if (propsOfRels.size() == 0 && relsOfRels.size() == 0) {
            addRelObject(builder, key, relst.getObject().toString());
        }
        else {*/
            JsonObjectBuilder propBuilder = Json.createObjectBuilder();
            propBuilder.add("type", "RelationshipStatement");
            // TODO: This will not always be a JsonValue
            propBuilder.add("object", relst.getObject().toString());
            Set<String> keys = propsOfRels.keySet();
            for (String keyProp : keys) {
                CProperty newSt = propsOfRels.get(keyProp);
                adaptPropertyStToJson(propBuilder, keyProp, newSt);
            }
            builder.add(key, propBuilder.build());
       /* } */
    }

    private void addRelObject(JsonObjectBuilder obj, String key, String object) {
        obj.add(key, object);
    }
}
