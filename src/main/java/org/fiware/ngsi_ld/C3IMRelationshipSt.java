package org.fiware.ngsi_ld;

import java.net.URI;

/**
 *
 *   A C3IM Relationship statement
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
public interface C3IMRelationshipSt extends C3IMObject {
    public URI getObject();
    public String getRelationshipId();
}
