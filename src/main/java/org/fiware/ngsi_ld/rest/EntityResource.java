package org.fiware.ngsi_ld.rest;

import org.fiware.ngsi.NgsiClient;
import org.fiware.ngsi.QueryData;
import org.fiware.ngsi.QueryResult;
import org.fiware.ngsi_ld.C3IMEntity;
import org.fiware.ngsi_ld.comp.C3IMEntityAdapter;
import org.fiware.Configuration;
import org.fiware.ngsi_ld.comp.Ngsi2C3IM;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


/**
 *   Entity resource
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
    public Response getEntity(@PathParam("id") String id, @QueryParam("options") List<String> options) {
        JsonbConfig config = new JsonbConfig();

        config.withAdapters(new C3IMEntityAdapter());
        Jsonb jsonb = JsonbBuilder.create(config);

        QueryData qd = new QueryData();
        qd.entityIds = id;

        QueryResult result = retrieveNgsiEntity(qd, options);

        if (result.status != 200) {
            return Response.status(result.status).build();
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

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "application/json" media type.
     *
     * @return Object that will be transformed to application/json response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEntities(@QueryParam("id") String id, @QueryParam("type") String type,
                              @QueryParam("q") String q, @QueryParam("georel") String georel,
                              @QueryParam("geometry") String geometry, @QueryParam("coords") String coords,
                              @QueryParam("options") List<String> options) {
        JsonbConfig config = new JsonbConfig();

        config.withAdapters(new C3IMEntityAdapter());
        Jsonb jsonb = JsonbBuilder.create(config);

        QueryData qd = new QueryData();
        if (id != null) {
            qd.entityIds = id;
        }

        if (type != null) {
            qd.types = type;
        }

        if (q != null) {
            qd.queryExpression = q;
        }

        QueryResult result = retrieveNgsiEntity(qd, options);

        if (result.status != 200) {
            return Response.status(result.status).build();
        }
        else {
            JsonArray array = result.result.asJsonArray();
            List<C3IMEntity> resultEntities = new ArrayList<>();

            // Workaround to return a list
            StringBuffer stb = new StringBuffer("[");

            for(int j = 0; j < array.size(); j++) {
                JsonObject obj = array.getJsonObject(j);
                C3IMEntity c3imEntity = Ngsi2C3IM.transform(obj);
                resultEntities.add(c3imEntity);

                stb.append(jsonb.toJson(c3imEntity)).append(",");
            }

            if (stb.length() > 1) {
                stb.deleteCharAt(stb.length() - 1);
            }

            stb.append("]");

            return addJsonLinkHeader(Response.ok(stb.toString())).build();
        }
    }

    private Response.ResponseBuilder addJsonLinkHeader(Response.ResponseBuilder rb) {
        return rb.header("Link", Configuration.LINK_HEADER_VALUE);
    }

    private QueryResult retrieveNgsiEntity(QueryData qd, List<String> options) {
        NgsiClient client = new NgsiClient(Configuration.ORION_BROKER);

        return client.query(qd, options);
    }
}
