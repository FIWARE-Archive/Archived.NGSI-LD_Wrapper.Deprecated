package org.fiware.ngsi_ld.rest;

import org.fiware.ngsi.NgsiClient;
import org.fiware.ngsi.QueryData;
import org.fiware.ngsi.QueryResult;
import org.fiware.ngsi_ld.C3IMEntity;
import org.fiware.ngsi_ld.comp.C3IMEntityAdapter;
import org.fiware.Configuration;
import org.fiware.ngsi_ld.comp.Ngsi2C3IM;

import javax.json.JsonArray;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


/**
 *   Entities resource
 *
 *   Copyright (c) 2017 FIWARE Foundation e.V.
 *
 *   LICENSE: MIT
 *
 *
 */
@Path("/entities/")
public class EntityResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "application/json" media type.
     *
     * @return Object that will be transformed to application/json response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Object getEntity(@PathParam("id") String id) {
        JsonbConfig config = new JsonbConfig();

        config.withAdapters(new C3IMEntityAdapter());
        Jsonb jsonb = JsonbBuilder.create(config);

        QueryResult result = retrieveNgsiEntity(id,"Vehicle");

        if (result.status != 200) {
            return Response.status(result.status);
        }
        else {
            JsonArray array = result.result.asJsonArray();
            if (array.size() == 0) {
                return Response.status(404).build();
            }

            C3IMEntity c3imEntity = Ngsi2C3IM.transform(array.getJsonObject(0));
            return addJsonLinkHeader(Response.ok()).entity(jsonb.toJson(c3imEntity)).build();
        }
    }

    private Response.ResponseBuilder addJsonLinkHeader(Response.ResponseBuilder rb) {
        return rb.header("Link", Configuration.LINK_HEADER_VALUE);
    }

    private QueryResult retrieveNgsiEntity(String id, String type) {
        NgsiClient client = new NgsiClient(Configuration.ORION_BROKER);
        QueryData qd = new QueryData();
        qd.entityIds.add(id);
        qd.types.add(type);

        return client.query(qd);
    }
}
