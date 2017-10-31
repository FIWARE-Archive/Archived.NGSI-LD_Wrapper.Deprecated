package org.fiware.ngsi_ld.impl;


import org.fiware.ngsi_ld.C3IMEntity;
import org.fiware.ngsi_ld.C3IMObject;
import org.fiware.ngsi_ld.C3IMPropertySt;
import org.fiware.ngsi_ld.C3IMRelationshipSt;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *   A C3IM Object implementation
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
public abstract class C3IMObjectImpl implements C3IMObject {
    // Properties map
    private Map<String, C3IMPropertySt> properties = new HashMap<String, C3IMPropertySt>();
    private Map<String, C3IMRelationshipSt> relationships = new HashMap<String, C3IMRelationshipSt>();

    public C3IMObjectImpl() {

    }

    @Override
    public C3IMPropertySt getProperty(String id) {
        return properties.get(id);
    }

    @Override
    public C3IMRelationshipSt getRelationship(String id) {
        return relationships.get(id);
    }

    @Override
    public Object getPropertyValue(String id) {
        Object out = null;

        C3IMPropertySt propertySt = properties.get(id);

        if (propertySt != null) {
            out = propertySt.getValue();
        }

        return out;
    }

    @Override
    public URI getRelationshipObject(String id) {
        URI out = null;

        C3IMRelationshipSt relationshipSt = relationships.get(id);

        if (relationshipSt != null) {
            out = relationshipSt.getObject();
        }

        return out;
    }

    @Override
    public void addProperty(C3IMPropertySt prop) {
        properties.put(prop.getPropertyId(), prop);
    }

    @Override
    public void addRelationship(C3IMRelationshipSt rel) {
        relationships.put(rel.getRelationshipId(), rel);
    }

    @Override
    public Map<String,C3IMPropertySt> getProperties() {
        return properties;
    }

    @Override
    public Map<String,C3IMRelationshipSt> getRelationships() {
        return relationships;
    }
}
