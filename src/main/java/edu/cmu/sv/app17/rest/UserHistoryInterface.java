package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.SearchData;
import edu.cmu.sv.app17.models.User;
import edu.cmu.sv.app17.models.UserHistory;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("userhistory")
public class UserHistoryInterface {
    private MongoCollection<Document> collection;
    private ObjectWriter ow;


    public UserHistoryInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("ididthatDB");

        this.collection = database.getCollection("userhistory");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public ArrayList<UserHistory> getAll() {

        try {
            ArrayList<UserHistory> UserHistoryList = new ArrayList<UserHistory>();


            FindIterable<Document> results = collection.find();
            if (results == null) {
                return UserHistoryList;
            }
            for (Document item : results) {
                UserHistory userHistory = new UserHistory(
                        item.getString("userName"),
                        item.getString("latest"),
                        item.getString("second"),
                        item.getString("third")
                );
                userHistory.setId(item.getObjectId("_id").toString());
                UserHistoryList.add(userHistory);
            }
            return UserHistoryList;
        }catch(NotFoundException e) {
            throw new APPNotFoundException(0,"This search is not listed by IDidThat");
        }
        catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public UserHistory getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such user has checked in");
            }
            UserHistory userHistory = new UserHistory(
                    item.getString("userName"),
                    item.getString("latest"),
                    item.getString("second"),
                    item.getString("third")
            );
            userHistory.setId(item.getObjectId("_id").toString());
            return userHistory;

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"There appear to be no user like this");
        }catch(Exception e) {
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
        Document doc = new Document("userName", json.getString("userName"))
                .append("latest", json.getString("latest"))
                .append("second", json.getString("second"))
                .append("third", json.getString("third"));
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
            if (json.has("latest"))
                doc.append("latest",json.getString("latest"));
            if (json.has("second"))
                doc.append("second", json.getString("second"));
            if (json.has("third"))
                doc.append("third", json.getString("third"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return request;
    }

    //A place can only be created and updated - GET, POST and PATCH implemented.

}