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
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.Checkin;
import edu.cmu.sv.app17.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;


@Path("users")
public class UserInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> checkinCollection;
    private ObjectWriter ow;


    public UserInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("ididthatDB");

        this.collection = database.getCollection("users");
        this.checkinCollection = database.getCollection("checkin");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public ArrayList<User> getAll() {

       try {
           ArrayList<User> userList = new ArrayList<User>();


           FindIterable<Document> results = collection.find();
           if (results == null) {
               return userList;
           }
           for (Document item : results) {
               User user = new User(
                       item.getString("userName"),
                       item.getString("emailAddress"),
                       item.getString("password")
               );
               user.setId(item.getObjectId("_id").toString());
               userList.add(user);
           }
           return userList;
       }catch(NotFoundException e) {
           throw new APPNotFoundException(0,"This user is not listed by IDidThat");
       }
       catch(Exception e) {
           throw new APPInternalServerException(99,"Oops, Something went wrong");
       }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public User getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such review, my friend");
            }
            User user = new User(
                    item.getString("userName"),
                    item.getString("emailAddress"),
                    item.getString("password")
            );
            user.setId(item.getObjectId("_id").toString());
            return user;

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"There appear to be no user like this");
        }catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }


    }

    @GET
    @Path("{id}/checkin")
    @Produces({MediaType.APPLICATION_JSON})
    public ArrayList<Checkin> getCheckinForUsers(@Context HttpHeaders headers, @PathParam("id") String id,
                                                 @DefaultValue("30") @QueryParam("count") int count,
                                                 @DefaultValue("20") @QueryParam("offset") int offset) {

        ArrayList<Checkin> reviewsList = new ArrayList<Checkin>();

        try {
            BasicDBObject query = new BasicDBObject();
            query.put("checkinID", id);

            long resultCount = checkinCollection.count(query);
            FindIterable<Document> results = checkinCollection.find(query).skip(offset).limit(count);
            for (Document item : results) {
                String checkinID = item.getString("checkinID");
                Checkin checkin = new Checkin(
                        checkinID,
                        item.getString("userID"),
                        item.getString("placeName"),
                        item.getBoolean("isExisting"),
                        item.getInteger("numberCheckin")
                );
                checkin.setId(item.getObjectId("_id").toString());
                reviewsList.add(checkin);
            }
            return reviewsList;

        } catch(Exception e) {
            System.out.println("Oops something went wrong");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
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
        if (!json.has("checkinID"))
            throw new APPBadRequestException(55,"missing checkin");
        if (!json.has("placeID"))
            throw new APPBadRequestException(55,"missing place");
        if (!json.has("ratingID"))
            throw new APPBadRequestException(55,"missing rating");

        Document doc = new Document("placeID", json.getString("placeID"))
                .append("placeName", json.getString("placeName"))
                .append("isExisting", json.getBoolean("isExisting"))
                .append("numberCheckin", json.getInt("numberCheckin"))
                .append("userID", id);
        checkinCollection.insertOne(doc);
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
            if (!json.has("userName") ) {
                throw new APPBadRequestException(55,"missing userName");
            }
            // You need to add all other fields
            Document doc = new Document("userName", json.getString("userName"))
                    .append("emailAddress", json.getString("emailAddress"))
                    .append("password", json.getString("password"));
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
            if (json.has("userName"))
                doc.append("userName",json.getString("userName"));
            if (json.has("emailAddress"))
                doc.append("emailAddress",json.getString("emailAddress"));
            if (json.has("password"))
                doc.append("password", json.getString("password"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return request;
    }

    //A user can be created and updated, but not deleted.

}
