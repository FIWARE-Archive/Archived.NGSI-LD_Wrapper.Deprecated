package org.fiware.ngsi_ld;

import java.net.URI;
import java.util.Map;

/**
 *
 *   A C3IM Object
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
public interface C3IMObject {
    public C3IMPropertySt getProperty(String id);

    public Object getPropertyValue(String id);

    public C3IMRelationshipSt getRelationship(String id);

    public URI getRelationshipObject(String id);

    public void addProperty(C3IMPropertySt prop);

    public void addRelationship(C3IMRelationshipSt rel);

    public Map<String,C3IMRelationshipSt> getRelationships();

    public Map<String,C3IMPropertySt> getProperties();
}
