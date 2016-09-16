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

    /**
     * Request publication of a new research object
     *
     * @param publicationRequestString
     *            {Aggregation, &lt;ContentObject&gt;, Preferences, {&lt;Preferences
     *            list&gt;}, Aggregation Statistics {&lt;Aggregation Statistics List
     *            &gt; Repository, &lt;RepositoryId&gt;}}
     *
     * <br>
     *            where Content is a json object including basic metadata and
     *            the unique ID for the entity the user wants to publish. <br>
     *            preferences is a json list of options chosen from those
     *            available (see api ____) <br>
     *            Respository is the ID of the repository as defined within SEAD
     *            (see api _______) Project Space is the base URL for the source
     *            Project Space
     *
     * @see Example input file: _______ <br>
     *      Example output file: _______
     *
     * @return 200: {response: "success", id : &lt;ID&gt;} <br>
     *         400: {response: "failure", reason : &lt;string&gt;} <br>
     *         401: {response: "failure", reason : &lt;string&gt;} 409 Conflict:
     *         {response: "failure", reason : &lt;string&gt;}
     *
     *
     */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response startROPublicationProcess(String publicationRequestString,
			@QueryParam("requestUrl") String requestURL,
            @QueryParam("oreId") String oreId) throws URISyntaxException;

    /**
     * Return the list of requests
     *
     * @param  purpose
     *             filter by the purpose flag of the research object; the values for the 'purpose' can be 'Production' or 'Testing-Only'
     *
     * @return Aggegation (Title, Identifier), Repository, and Status array
     */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getROsList(@QueryParam("Purpose") final String purpose) ;

    /**
     * Return the list of new requests (no status from repository)
     *
     * @param  purpose
     *             filter by the purpose flag of the research object; the values for the 'purpose' can be 'Production' or 'Testing-Only'
     *
     * @return Aggegation (Title, Identifier), Repository, and Status array
     */
	@GET
	@Path("/new/")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getNewROsList(@QueryParam("Purpose") final String purpose);

    /**
     * Return the profile and status for a given publication
     *
     * @param id
     *            the assigned ro/publication ID
     *
     * @return : json-ld profile document
     */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getROProfile(@PathParam("id") String id) ;

    /**
     * Update the status for a given publication / ro
     * Reporter: the entity sending status, e.g. repository (use the orgidentifier term used as an id in repository profiles)
     * Stage: short string describing stage: Recommended values are "Receipt Acknowledged", "Pending", "Success", "Failure"
     * Message" longer string describing the status. For "Success", the message MUST be the persistent identifier assigned to the research object
     *
     * A timestamp will be appended by the services.
     *
     * Body : { "reporter": <reporter>, "Stage": <stage>, "message":<message> }
     *
     *
     * @param id
     *            the assigned repository ID
     *
     * @return 200 OK: {response: "success", id : &lt;ID&gt;} <br>
     *         400 Bad Request: {response: "failure", reason : &lt;string&gt;} <br>
     */
	@POST
	@Path("/{id}/status")
	@Consumes(MediaType.APPLICATION_JSON)
	public abstract Response setROStatus(@PathParam("id") String id, String state);

    /**
     * Return the status for a given publication
     *
     * @param id
     *            the assigned ro/publication ID
     *
     * @return : json-ld profile document
     */
	@GET
	@Path("/{id}/status")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getROStatus(@PathParam("id") String id) ;

    /**
     * Rescind a publication request and mark it as obsolete
     *
     * @param id
     *            the assigned publication/ro ID
     *
     * @return 200 OK: {response: "success", id : &lt;ID&gt;} <br>
     *         400 Bad Request: {response: "failure", reason : &lt;string&gt;} <br>
     */
	@DELETE
	@Path("/{id}")
	public abstract Response rescindROPublicationRequest(@PathParam("id") String id);

    /**
     * This is a management method
     * Rescind a publication request and mark it as obsolete, overriding the permission granted to the API
     *
     * @param id
     *            the assigned publication/ro ID
     *
     * @return 200 OK: {response: "success", id : &lt;ID&gt;} <br>
     *         400 Bad Request: {response: "failure", reason : &lt;string&gt;} <br>
     */
    @DELETE
    @Path("/{id}/override")
    public abstract Response DeleteOverrideRO(@PathParam("id") String id);

    /**
     * Preserve the OreMap of a Research object
     *
     * @param oreMapString
     *            Json representation of the OreMap
     * @param id
     *            MongoDB document ID of the OreMap
     *
     * @return 200: Upon successful deposit of OreMap
     *
     */
    @POST
    @Path("/oremap")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public abstract Response addOreMap(@QueryParam("objectId") String id, String oreMapString);

    /**
     * Return the OREMap associated with the give request
     *
     * @param id
     *            the assigned ro/publication ID
     *
     * @return : json-ld profile document
     */
    @GET
    @Path("/{id}/oremap")
    @Produces(MediaType.APPLICATION_JSON)
    public abstract Response getROOREMap(@PathParam("id") String id) throws JSONException, IOException;

    /**
     * Delete the OreMap
     *
     * @param id
     *            MongoDB document ID of the OreMap
     *
     * @return 200: Upon successful deletion of OreMap
     *
     */
    @DELETE
    @Path("/{id}/oremap")
    public abstract Response deleteOreByDocumentId(@PathParam("id") String id);

    /**
     * Preserve the FGDC of a Research object
     *
     * @param id
     *            Identifier of the Research Object
     *
     * @return 200: Upon successful deposit of FGDC
     *
     */
    @POST
    @Path("/{id}/fgdc")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public abstract Response addFgdc(String fgdcString, @PathParam("id") String id);

    /**
     * Return the FGDC metadata associated with the give request
     *
     * @param id
     *            the assigned ro/publication ID
     *
     * @return : FGDC metadata document
     */
    @GET
    @Path("/{id}/fgdc")
    @Produces(MediaType.APPLICATION_XML)
    public abstract Response getFgdc(@PathParam("id") String id) ;

    /**
     * Return the RO ID of the Research Object associated with the PID
     *
     * @param pid
     *            PID
     *
     * @return : 200 OK: {roId: &lt;RO ID&gt;} <br>
     */
    @GET
    @Path("/pid/{pid}")
    @Produces(MediaType.APPLICATION_JSON)
    public abstract Response getRoOfPID(@PathParam("pid") String pid);

    /**
     * Deprecate oldRO by newRO. Delete the old RO request and OREMap
     * @param newRoId
     *            RO ID of new Research Object
     * @param oldRoId
     *            RO ID of old Research Object
     */
    @GET
    @Path("/deprecate/{newRO}/{oldRO}")
    @Produces(MediaType.APPLICATION_JSON)
    public abstract Response deprecateRO(@PathParam("newRO") String newRoId,
                                @PathParam("oldRO") String oldRoId);


    /**
     * This is a management method used to copy oreMaps from main mongoDB to the GridFS DB
     */
    @PUT
    @Path("/copyoremaps")
    public abstract Response copyOreMaps();

}
