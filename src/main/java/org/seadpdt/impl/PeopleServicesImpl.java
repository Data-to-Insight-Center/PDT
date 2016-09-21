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

package org.seadpdt.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.sun.jersey.api.client.ClientResponse.Status;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.seadpdt.PeopleServices;
import org.seadpdt.people.Profile;
import org.seadpdt.people.Provider;
import org.seadpdt.util.MongoDB;

import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Path("/people")
public class PeopleServicesImpl extends PeopleServices{

	public static final String identifier = "identifier";
	public static final String provider = "provider";
	private static MongoDatabase db = MongoDB.getServicesDB();
	private static MongoCollection<Document> peopleCollection = db
			.getCollection(MongoDB.people);
	private CacheControl control = new CacheControl();

	public PeopleServicesImpl() {
		control.setNoCache(true);
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerPerson(String personString) {

		JSONObject person = new JSONObject(personString);
		Provider p = null;

		if (person.has(provider)) {
			p = Provider.getProvider((String) person.get(provider));

		}

		if (!person.has(identifier)) {
			return Response
					.status(Status.BAD_REQUEST)
					.entity(new BasicDBObject("Failure",
							"Invalid request format: missing identifier"))
					.build();
		}
		String rawID = (String) person.get(identifier);

		String newID;
		if (p != null) {
			//Know which provider the ID is from (as claimed by the client) so go direct to get its canonical form
			newID = p.getCanonicalId(rawID);
		} else {
			//Don't know the provider, so find it and the canonical ID together
			Profile profile = Provider.findCanonicalId(rawID);
			if(profile!=null) {
				 
			p = Provider.getProvider(profile.getProvider());
			} //else no provider recognized the id (e.g. it's a string), so we'll just fail with a null Provier
			if (p == null) {
				return Response
						.status(Status.BAD_REQUEST)
						.entity(new BasicDBObject("Failure",
								"Invalid request format:identifier not recognized"))
						.build();
			}
			newID = profile.getIdentifier();
		}

		person.put(identifier, newID);

		FindIterable<Document> iter = peopleCollection.find(new Document("@id",
				newID));
		if (iter.iterator().hasNext()) {
			return Response
					.status(Status.CONFLICT)
					.entity(new BasicDBObject("Failure",
							"Person with Identifier " + newID
									+ " already exists")).build();
		} else {
			URI resource = null;
			try {

				Document profileDocument = p.getExternalProfile(person);
				peopleCollection.insertOne(profileDocument);
				resource = new URI("./" + profileDocument.getString("@id"));
			} catch (Exception r) {
				return Response
						.serverError()
						.entity(new BasicDBObject("failure",
								"Provider call failed with status: "
										+ r.getMessage())).build();
			}

			try {
				resource = new URI("./" + newID);
			} catch (URISyntaxException e) {
				// Should not happen given simple ids
				e.printStackTrace();
			}
			return Response.created(resource)
					.entity(new Document("identifier", newID)).build();
		}
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPeopleList() {
		FindIterable<Document> iter = peopleCollection.find();
		iter.projection(getBasicPersonProjection());

		MongoCursor<Document> cursor = iter.iterator();
		ArrayList<Object> array = new ArrayList<Object>();
		while (cursor.hasNext()) {
			Document next = cursor.next();
			array.add(next);
		}
		Document peopleDocument = new Document();
		peopleDocument.put("persons", array);
		peopleDocument.put("@context", getPersonContext());
		return Response.ok(peopleDocument.toJson()).cacheControl(control)
				.build();
	}

    @GET
    @Path("/list/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPeopleListAsArray() {
        FindIterable<Document> iter = peopleCollection.find();
        iter.projection(getBasicPersonProjection());

        MongoCursor<Document> cursor = iter.iterator();
        JSONArray array = new JSONArray();
        while (cursor.hasNext()) {
            Document next = cursor.next();
            next.put("@context", getPersonContext());
            array.put(next);
        }
        return Response.ok(array.toString()).cacheControl(control)
                .build();
    }

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPersonProfile(@PathParam("id") String id) {
		Profile profile = null;
		try {
			profile = Provider.findCanonicalId(id);
		} catch (Exception e) {
			return Response
					.status(Response.Status.CONFLICT)
					.entity(new BasicDBObject("failure", "Ambiguous identifier"))
					.build();
		}
		if (profile == null) {
			return Response.status(Response.Status.NOT_FOUND)
                    .entity(new BasicDBObject("failure", "Person with identifier " + id + " not found"))
					.build();
		}
		Document document = retrieveProfile(profile.getIdentifier());
		if (document != null) {
			return Response.ok(document.toJson()).build();
		} else {
			return Response.status(Status.NOT_FOUND)
                    .entity(new BasicDBObject("failure", "Person with identifier " + id + " not found"))
                    .build();
		}
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePersonProfile(@PathParam("id") String id) {
		Profile profile = null;
		try {
			profile = Provider.findCanonicalId(id);
		} catch (Exception e) {
			return Response
					.status(Response.Status.CONFLICT)
					.entity(new BasicDBObject("failure", "Ambiguous identifier"))
					.build();
		}
		if (profile == null) {
			return Response.status(Response.Status.NOT_FOUND)
                    .entity(new BasicDBObject("failure", "Person with identifier " + id + " not found"))
					.build();
		}
		id = profile.getIdentifier();
		Document profileDoc = retrieveProfile(id);
		if (profileDoc != null) {
			String providerName = profileDoc.getString(provider);
			try {
				profileDoc = Provider.getProvider(providerName)
						.getExternalProfile(profile.asJson());
			} catch (RuntimeException r) {
				return Response
						.serverError()
						.entity(new BasicDBObject("failure",
								"Provider call failed with status: "
										+ r.getMessage())).build();
			}

			peopleCollection.replaceOne(new Document("@id", id), profileDoc);
			return Response.ok(new BasicDBObject("response", "Successfully updated person with identifier " + id)).build();

		} else {
			return Response.status(Status.NOT_FOUND)
                    .entity(new BasicDBObject("failure", "Person with identifier " + id + " not found"))
                    .build();

		}
	}

	@DELETE
	@Path("/{id}")
	public Response unregisterPerson(@PathParam("id") String id) {
		Profile profile = null;
		try {
			profile = Provider.findCanonicalId(id);
		} catch (Exception e) {
			return Response
					.status(Response.Status.CONFLICT)
					.entity(new BasicDBObject("failure", "Ambiguous identifier"))
					.build();
		}
		if (profile == null) {
			return Response.status(Response.Status.NOT_FOUND)
                    .entity(new BasicDBObject("failure", "Person with identifier " + id + " not found"))
					.build();
		}
		id = profile.getIdentifier();
		DeleteResult result = peopleCollection
				.deleteOne(new Document("@id", id));
		if (result.getDeletedCount() == 0) {
			return Response.status(Status.NOT_FOUND)
                    .entity(new BasicDBObject("failure", "Person with identifier " + id + " not found"))
                    .build();
		} else {
			return Response.ok(new BasicDBObject("response", "Successfully deleted person with identifier " + id)).build();
		}
	}

	@GET
	@Path("/canonical/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getCanonicalID(@PathParam("id") String id) {
		Profile profile = null;
		try {
			profile = Provider.findCanonicalId(id);
		} catch (Exception e) {
			return Response
					.status(Response.Status.CONFLICT)
					.entity(new BasicDBObject("failure", "Ambiguous identifier"))
					.build();
		}
		if (profile == null) {
			return Response.status(Response.Status.NOT_FOUND)
                    .entity(new BasicDBObject("failure", "Person with identifier " + id + " not found"))
					.build();
		}
		return Response.ok(profile.getIdentifier()).build();
	}

	static private Document getPersonContext() {
		Document contextDocument = new Document();
		contextDocument.put("givenName", "http://schema.org/Person/givenName");
		contextDocument
				.put("familyName", "http://schema.org/Person/familyName");
		contextDocument.put("email", "http://schema.org/Person/email");
		contextDocument.put("affiliation",
				"http://schema.org/Person/affiliation");
		contextDocument.put("PersonalProfileDocument",
				"http://schema.org/Thing/mainEntityOfPage");
		return contextDocument;
	}

	static protected Document getBasicPersonProjection() {
		return new Document("givenName", 1).append("familyName", 1)
				.append("@id", 1).append("email", 1).append("affiliation", 1)
				.append("PersonalProfileDocument", 1).append("_id", 0);
	}

	static Document retrieveProfile(String canonicalID) {

		Document document = null;
		FindIterable<Document> iter = peopleCollection.find(new Document("@id",
				canonicalID));
		iter.projection(getBasicPersonProjection());
		if (iter.first() != null) {
			document = iter.first();
			document.put("@context", getPersonContext());
		}

		return document;
	}

}
