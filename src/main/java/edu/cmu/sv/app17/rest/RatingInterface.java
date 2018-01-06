package edu.cmu.sv.app17.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.helpers.PATCH;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.cmu.sv.app17.models.Rating;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

@Path("rating")
public class RatingInterface {
    private MongoCollection<Document> collection = null;
    private ObjectWriter ow;

    public RatingInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("ididthatDB");
        collection = database.getCollection("ratings");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public ArrayList<Rating> getAll() {

        ArrayList<Rating> RatingsList = new ArrayList<Rating>();

        try {
            FindIterable<Document> results = collection.find();
            for (Document item : results) {
                Integer rating = item.getInteger("rating");
                Rating ratings = new Rating(
                        rating,
                        item.getInteger("avgRating"),
                        item.getString("placeID")
                );
                ratings.setId(item.getObjectId("_id").toString());
                RatingsList.add(ratings);
            }
            return RatingsList;

        } catch(NotFoundException e) {
            throw new APPNotFoundException(0,"There appear to be no ratings");
        }
        catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }

    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Rating getOne(@PathParam("id") String id) {


        BasicDBObject query = new BasicDBObject();

        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "This rating doesn't exist. Try again.");
            }
            Rating ratings = new Rating(
                    item.getInteger("rating"),
                    item.getInteger("avgRating"),
                    item.getString("placeID")
            );
            ratings.setId(item.getObjectId("_id").toString());
            return ratings;

        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Incorrect ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }


    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Object create(Object request) {

        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        if (!json.has("rating") ) {
            throw new APPBadRequestException(55,"missing rating field");
        }
        // You need to add all other fields
        Document doc = new Document("rating", json.getString("rating"))
                .append("avgRating", json.getString("avgRating"))
                .append("placeID", json.getString("placeID"));
        collection.insertOne(doc);

        return request;
    }


    @PATCH
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Object update(@PathParam("id") String id, Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        try {

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("rating"))
                doc.append("rating",json.getString("rating"));
            if (json.has("avgRating"))
                doc.append("avgRating",json.getString("avgRating"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return request;
    }


    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Object delete(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        DeleteResult deleteResult = collection.deleteOne(query);
        if (deleteResult.getDeletedCount() < 1)
            throw new APPNotFoundException(66,"Could not delete");

        return new JSONObject();
    }

    //A Rating can be created,updated and deleted
}
