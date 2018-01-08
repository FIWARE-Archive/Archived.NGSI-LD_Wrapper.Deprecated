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
public interface CProperty extends CObject {
    public Object getValue();
    public String getPropertyId();
    public String getDataType();
}
