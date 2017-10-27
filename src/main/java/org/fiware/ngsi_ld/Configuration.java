package org.fiware.ngsi_ld;

public class Configuration {
    public static String ORION_BROKER = "http://localhost:1026/v2";

    public static String LINK_HEADER_VALUE =
            "<http://json-ld.org/contexts/c3im.jsonld>; " +
                    "rel=\"http://www.w3.org/ns/json-ld#context\"; " +
                    "type=\"application/ld+json\"";
}
