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

@Path("/repositories")
public abstract class RepoServices {

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response registerRepository(String profileString);

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getRepositoryList() ;

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getRepositoryProfile(@PathParam("id") String id);

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public abstract Response setRepositoryProfile(@PathParam("id") String id,
			String profile);

	@DELETE
	@Path("/{id}")
	public abstract Response unregisterRepository(@PathParam("id") String id);

	@GET
	@Path("/{id}/researchobjects")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getROsByRepository(@PathParam("id") String id, @QueryParam("Purpose") final String purpose) ;

	@GET
	@Path("/{id}/researchobjects/new")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract Response getNewROsByRepository(@PathParam("id") String id, @QueryParam("Purpose") final String purpose);
}
