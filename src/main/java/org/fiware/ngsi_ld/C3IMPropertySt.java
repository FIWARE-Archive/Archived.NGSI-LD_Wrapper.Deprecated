package org.fiware.ngsi_ld;

/**
 *
 *   A C3IM Property Statement
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
public interface C3IMPropertySt extends C3IMObject {
    public Object getValue();
    public String getPropertyId();
}
