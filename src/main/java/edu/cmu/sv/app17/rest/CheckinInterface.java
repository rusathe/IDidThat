package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.Checkin;
import edu.cmu.sv.app17.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Path("checkin")
public class CheckinInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> reviewCollection;
    private ObjectWriter ow;


    public CheckinInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("ididthatDB");

        this.collection = database.getCollection("users");
        this.reviewCollection = database.getCollection("checkin");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@DefaultValue("_id") @QueryParam("sort") String sortArg) {

        ArrayList<Checkin> checkinList = new ArrayList<Checkin>();

        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            FindIterable<Document> results = collection.find().sort(sortParams);
            for (Document item : results) {
                String placeID = item.getString("placeID");
                Checkin checkin = new Checkin(
                        placeID,
                        item.getString("userID"),
                        item.getString("placeName"),
                        item.getBoolean("isExisting"),
                        item.getInteger("rating")
                );
                checkin.setId(item.getObjectId("_id").toString());
                checkinList.add(checkin);
            }
            return new APPResponse(checkinList);

        } catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }

    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Checkin getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such checkin");
            }
            Checkin checkin = new Checkin(
                    item.getString("placeID"),
                    item.getString("userID"),
                    item.getString("placeName"),
                    item.getBoolean("isExisting"),
                    item.getInteger("rating")
            );
            checkin.setId(item.getObjectId("_id").toString());
            return checkin;

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such checkin");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Incorrect ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }


    }

    @GET
    @Path("{id}/checkin")
    @Produces({MediaType.APPLICATION_JSON})
    public ArrayList<Checkin> getCheckinForUsers(@PathParam("id") String id) {

        ArrayList<Checkin> checkinList = new ArrayList<Checkin>();

        try {
            BasicDBObject query = new BasicDBObject();
            query.put("userId", id);

            FindIterable<Document> results = reviewCollection.find(query);
            for (Document item : results) {
                String placeID = item.getString("placeID");
                Checkin checkin = new Checkin(
                        placeID,
                        item.getString("userID"),
                        item.getString("placeName"),
                        item.getBoolean("isExisting"),
                        item.getInteger("rating")
                );
                checkin.setId(item.getObjectId("_id").toString());
                checkinList.add(checkin);
            }
            return checkinList;

        } catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }

    }


    @POST
    @Path("{id}/checkin")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Object create(@PathParam("id") String id, Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        /*if (!json.has("checkinID"))
            throw new APPBadRequestException(55,"missing checkin");*/
        if (!json.has("placeID"))
            throw new APPBadRequestException(55,"missing place");
        if (!json.has("placeName"))
            throw new APPBadRequestException(55,"missing place name");
        if (!json.has("rating"))
            throw new APPBadRequestException(55, "missing checkin number");

        Document doc = new Document("placeID", json.getString("placeID"))
                .append("userID", json.getString("userID"))
                .append("placeName", json.getString("placeName"))
                .append("isExisting", json.getBoolean("isExisting"))
                .append("rating", json.getInt("rating"));
        reviewCollection.insertOne(doc);
        return request;
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
        /*if (!json.has("checkinID") ) {
            throw new APPBadRequestException(55,"missing checkinID");
        }*/
        // You need to add all other fields
        Document doc = new Document("placeID", json.getString("placeID"))
                .append("userID", json.getString("userID"))
                .append("placeName", json.getString("placeName"))
                .append("isExisting", json.getBoolean("isExisting"))
                .append("rating", json.getInt("rating"));
        reviewCollection.insertOne(doc);

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
            if (json.has("placeID"))
                doc.append("placeID",json.getString("placeID"));
            if (json.has("userID"))
                doc.append("userID",json.getString("userID"));
            if (json.has("placeName"))
                doc.append("placeName", json.getString("placeName"));
            if (json.has("rating"))
                doc.append("rating", json.getInt("rating"));
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
    //A Checkin can be created, updated and deleted.

}
