package org.seadpdt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/people")

public class PeopleServices {

	 @GET
	 @Path("/orcid")
	 @Produces(MediaType.APPLICATION_JSON)
	 
	 public String listORCID(
				@QueryParam("id") String orcidID)  {	
		 return ORCIDcalls.getORCID(orcidID);
	 }			
	
	 @GET
	 @Path("/list")
	 @Produces(MediaType.APPLICATION_JSON)
	 public byte[] listRepos()  {	
		 
		 java.nio.file.Path path = Paths.get("../../sead-json/people.json");
		 byte[] data = new byte[] {'*'};
		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {

		}		 
		 return data;
	 }
	 
		@GET
		@Path("/byid")
		public byte[] getRepoID(
			@QueryParam("id") String repID)  {
			 String repPath = "../../sead-json/" + repID + ".json";
			 java.nio.file.Path path = Paths.get(repPath);
			 byte[] data = new byte[] {'*'};
			try {
				data = Files.readAllBytes(path);
			} catch (IOException e) {

			}	 
			 return data;
		}	 
	
}