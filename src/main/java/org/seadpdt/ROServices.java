/*
 *
 * Copyright 2015 The Trustees of Indiana University, 2015 University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * @author myersjd@umich.edu
 * @author smccaula@indiana.edu
 */

package org.seadpdt;

import org.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

@Path("/researchobjects")
public abstract class ROServices {

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response startROPublicationProcess(String publicationRequestString,
			@QueryParam("requestUrl") String requestURL,
            @QueryParam("oreId") String oreId) throws URISyntaxException;

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getROsList(@QueryParam("Purpose") final String purpose) ;
	
	@GET
	@Path("/new/")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getNewROsList(@QueryParam("Purpose") final String purpose);

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getROProfile(@PathParam("id") String id) ;

	@POST
	@Path("/{id}/status")
	@Consumes(MediaType.APPLICATION_JSON)
	public abstract Response setROStatus(@PathParam("id") String id, String state);

	@GET
	@Path("/{id}/status")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getROStatus(@PathParam("id") String id) ;

	@DELETE
	@Path("/{id}")
	public abstract Response rescindROPublicationRequest(@PathParam("id") String id);

    @DELETE
    @Path("/{id}/override")
    public abstract Response DeleteOverrideRO(@PathParam("id") String id);

    @POST
    @Path("/oremap")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public abstract Response addOreMap(@QueryParam("objectId") String id, String oreMapString);

    @GET
    @Path("/{id}/oremap")
    @Produces(MediaType.APPLICATION_JSON)
    public abstract Response getROOREMap(@PathParam("id") String id) throws JSONException, IOException;

    @DELETE
    @Path("/{id}/oremap")
    public abstract Response deleteOreByDocumentId(@PathParam("id") String id);

    @POST
    @Path("/{id}/fgdc")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public abstract Response addFgdc(String fgdcString, @PathParam("id") String id);

    @GET
    @Path("/{id}/fgdc")
    @Produces(MediaType.APPLICATION_XML)
    public abstract Response getFgdc(@PathParam("id") String id) ;

    //If pid resolves to a published research object, return that RO ID
    @GET
    @Path("/pid/{pid}")
    @Produces(MediaType.APPLICATION_JSON)
    public abstract Response getRoOfPID(@PathParam("pid") String pid);

    //Deprecate oldRO by newRO. Delete the old RO request and OREMap
    @GET
    @Path("/deprecate/{newRO}/{oldRO}")
    @Produces(MediaType.APPLICATION_JSON)
    public abstract Response deprecateRO(@PathParam("newRO") String newRoId,
                                @PathParam("oldRO") String oldRoId);


    //This is a management method used to copy oreMaps from main mongoDB to the GridFS DB
    @PUT
    @Path("/copyoremaps")
    public abstract Response copyOreMaps();

}
