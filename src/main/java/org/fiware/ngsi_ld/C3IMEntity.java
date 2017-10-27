package org.fiware.ngsi_ld;


import java.util.HashMap;
import java.util.Map;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

/**
 *
 *   A C3IM Entity
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
public class C3IMEntity {
    // Entity id
    private String id;
    // Entity type
    private String type;

    // JSON-LD Context
    private Map context = new HashMap<String, String>();

    // All the attributes representing properties and relationships
    private Map attributes = new HashMap<String, Object>();

    public C3IMEntity(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public C3IMEntity(String id, String type, Map<String, Object> attrs) {
        this(id, type);

        if (attrs != null) {
            this.attributes = attrs;
        }
    }
    
    public String getId() {
        return id;
    }
    
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
    
    public void addAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /* Naming it without get as there are problems with Jsonbtransient */
    public Map attributes() {
        return attributes;
    }
}
