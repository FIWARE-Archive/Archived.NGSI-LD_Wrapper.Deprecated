package org.fiware.ngsi_ld.impl;

import org.fiware.ngsi_ld.C3IMRelationshipSt;

import java.net.URI;

/**
 *
 *   A C3IM Relationship statement implementation
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
public class C3IMRelationshipStImpl extends C3IMObjectImpl implements C3IMRelationshipSt {
    private String relationshipId;
    private URI object;

    public C3IMRelationshipStImpl(String relationshipId, URI object) {
        this.relationshipId = relationshipId;
        this.object = object;
    }

    @Override
    public String getRelationshipId() {
        return relationshipId;
    }

    @Override
    public URI getObject() {
        return object;
    }
}
