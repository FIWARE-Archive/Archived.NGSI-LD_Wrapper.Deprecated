package org.fiware.ngsi_ld.resources;

import org.fiware.ngsi_ld.C3IMEntity;
import org.fiware.ngsi_ld.C3IMPropertySt;
import org.fiware.ngsi_ld.impl.C3IMEntityImpl;
import org.fiware.ngsi_ld.comp.C3IMEntityAdapter;
import org.fiware.ngsi_ld.impl.C3IMPropertyStImpl;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource3")
public class MyResourceJson2 {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt() {
        JsonbConfig config = new JsonbConfig();

        config.withAdapters(new C3IMEntityAdapter());
        Jsonb jsonb = JsonbBuilder.create(config);

        C3IMEntity entity = new C3IMEntityImpl("urn:c3im:Vehicle:4567", "Vehicle");
        C3IMPropertySt propertySt = new C3IMPropertyStImpl("speed", 40);

        entity.addProperty(propertySt);

        return jsonb.toJson(entity);
    }
}
