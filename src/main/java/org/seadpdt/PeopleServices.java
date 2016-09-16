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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/people")
public abstract class PeopleServices {

    /**
     * Register a new Person
     *
     * @param personString
     *  { "provider": &lt;provider&gt;, "identifier", &lt;id&gt; }
     *
     *
     * @return 200: {response: "success", id : &lt;ID&gt;} <br>
     *         400 Bad Request: {response: "failure", reason : &lt;string&gt;} <br>
     *         409 Conflict: {response: "failure", reason : &lt;string&gt;} <br>
     *         500 Failure {response: "failure", reason : &lt;string&gt;}
     *
     */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response registerPerson(String personString);

    /**
     * Return the list of people
     *
     * @return [ "@context" : {},<br>
     *          &ensp;{"@id":&lt;person_id&gt;, "email":&lt;email&gt;, "givenName" : &lt;given_name&gt;, ... },<br>
     *          &ensp;... <br>
     *         ]
     */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getPeopleList();

    /**
     * Return the profile for a given person
     *
     * @param id
     *            the assigned person ID
     *
     * @return 200: {"@context" : {}, "@id":&lt;person_id&gt;, "email":&lt;email&gt;, "givenName" : &lt;given_name&gt;, ... } <br>
     *         404 Not Found: {response: "failure", reason : &lt;string&gt;} <br>
     *         409 Conflict: {response: "failure", reason : &lt;string&gt;}
     */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getPersonProfile(@PathParam("id") String id) ;

    /**
     * Request to update the profile for a given person
     *
     * @param id
     *            the assigned person ID
     *
     * @return 200 OK: {response: &lt;string&gt;} <br>
     *         404 Not Found: {response: "failure", reason : &lt;string&gt;} <br>
     *         409 Conflict: {response: "failure", reason : &lt;string&gt;} <br>
     *         500 Failure {response: "failure", reason : &lt;string&gt;}
     */
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public abstract Response updatePersonProfile(@PathParam("id") String id);

    /**
     * Unregister a person and remove their profile
     *
     * @param id
     *            the assigned person ID
     *
     * @return 200 OK: {response: &lt;string&gt;} <br>
     *         404 Not Found: {response: "failure", reason : &lt;string&gt;} <br>
     *         409 Conflict: {response: "failure", reason : &lt;string&gt;} <br>
     */
	@DELETE
	@Path("/{id}")
	public abstract Response unregisterPerson(@PathParam("id") String id) ;

    /**
     * Convenience method to get the canonical ID associated with the given id string
     *
     * @param id
     *            the assigned person ID
     *
     * Note: does not imply that a profile has been retrieved for the ID.
     * Not exposed via the sead-c3pr api / for internal use, e.g. by matchmaker
     * rightsholder rule
     *
     * @return 200 OK: canonical ID <br>
     *         404 Not Found: {response: "failure", reason : &lt;string&gt;} <br>
     *         409 Conflict: {response: "failure", reason : &lt;string&gt;} <br>
     */
    @GET
	@Path("/canonical/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public abstract Response getCanonicalID(@PathParam("id") String id);

}
