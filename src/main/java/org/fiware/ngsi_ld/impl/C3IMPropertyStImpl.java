package org.fiware.ngsi_ld.impl;

import org.fiware.ngsi_ld.C3IMPropertySt;

/**
 *
 *   A C3IM Property Statement implementation
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
public class C3IMPropertyStImpl extends C3IMObjectImpl implements C3IMPropertySt {
    private Object value;
    private String propertyId;
    private String dataType;
    private String timestamp;

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getPropertyId() {
        return propertyId;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String getDataType() {
        return dataType;
    }

    public C3IMPropertyStImpl(String propertyId, Object value) {
        this.propertyId = propertyId;
        this.value = value;
    }

    public C3IMPropertyStImpl(String propertyId, Object value, String dataType) {
        this(propertyId, value);
        this.dataType = dataType;
    }
}
