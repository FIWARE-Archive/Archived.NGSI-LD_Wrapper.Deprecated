package org.fiware.ngsi_ld.impl;

import org.fiware.ngsi_ld.C3IMEntity;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *   A C3IM Entity Implementation
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
public class C3IMEntityImpl extends C3IMObjectImpl implements C3IMEntity {
    // Entity id
    private String id;
    // Entity type
    private String type;

    // JSON-LD Context
    private Map context = new HashMap<String, String>();

    public C3IMEntityImpl(String id, String type, Map<String,String> ctx) {
        this.id = id;
        this.type = type;
        this.context = ctx;
    }

    public C3IMEntityImpl(String id, String type) {
        this(id, type, null);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String,String> jsonLdContext() {
        return context;
    }

    public void jsonLdContext(Map<String,String> context) {
        this.context = context;
    }
}
