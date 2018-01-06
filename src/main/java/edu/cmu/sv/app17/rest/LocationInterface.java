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
import edu.cmu.sv.app17.models.Location;
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


@Path("location")
public class LocationInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> reviewCollection;
    private ObjectWriter ow;


    public LocationInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("ididthatDB");

        this.collection = database.getCollection("users");
        this.reviewCollection = database.getCollection("location");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@DefaultValue("_id") @QueryParam("sort") String sortArg) {

        ArrayList<Location> locationList = new ArrayList<Location>();

        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            FindIterable<Document> results = collection.find().sort(sortParams);
            for (Document item : results) {
                String locName = item.getString("locName");
                Location location = new Location(
                        locName,
                        item.getString("cityName"),
                        item.getString("stateName"),
                        item.getString("countryName"),
                        item.getBoolean("isSuburb"),
                        item.getInteger("numberCheckin")
                );
                location.setId(item.getObjectId("_id").toString());
                locationList.add(location);
            }
            return new APPResponse(locationList);

        } catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }

    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Location getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such location");
            }
            Location location = new Location(
                    item.getString("locName"),
                    item.getString("cityName"),
                    item.getString("stateName"),
                    item.getString("countryName"),
                    item.getBoolean("isSuburb"),
                    item.getInteger("numberCheckin")
            );
            location.setId(item.getObjectId("_id").toString());
            return location;

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
    public ArrayList<Location> getCheckinForUsers(@PathParam("id") String id) {

        ArrayList<Location> checkinList = new ArrayList<Location>();

        try {
            BasicDBObject query = new BasicDBObject();
            query.put("userId", id);

            FindIterable<Document> results = reviewCollection.find(query);
            for (Document item : results) {
                String locName = item.getString("locName");
                Location location = new Location(
                        locName,
                        item.getString("cityName"),
                        item.getString("stateName"),
                        item.getString("countryName"),
                        item.getBoolean("isSuburb"),
                        item.getInteger("numberCheckin")
                );
                location.setId(item.getObjectId("_id").toString());
                checkinList.add(location);
            }
            return checkinList;

        } catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }

    }


    @POST
    @Path("{id}/location")
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
        if (!json.has("locationID"))
            throw new APPBadRequestException(55,"missing location");
        if (!json.has("locName"))
            throw new APPBadRequestException(55,"missing location name");
        if (!json.has("cityName"))
            throw new APPBadRequestException(55,"missing city name");
        if (!json.has("stateName"))
            throw new APPBadRequestException(55,"missing state name");
        if (!json.has("countryName"))
            throw new APPBadRequestException(55,"missing country name");
        if (!json.has("numberCheckin"))
            throw new APPBadRequestException(55, "missing checkin number");

        Document doc = new Document("locName", json.getString("locName"))
                .append("cityName", json.getString("cityName"))
                .append("stateName", json.getString("stateName"))
                .append("countryName", json.getString("countryName"))
                .append("isSuburb", json.getBoolean("isSuburb"))
                .append("numberCheckin", json.getInt("numberCheckin"));
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
        if (!json.has("locationID") ) {
            throw new APPBadRequestException(55,"missing locationID");
        }

        Document doc = new Document("locName", json.getString("locName"))
                .append("cityName", json.getString("cityName"))
                .append("stateName", json.getString("stateName"))
                .append("countryName", json.getString("countryName"))
                .append("isSuburb", json.getBoolean("isSuburb"))
                .append("numberCheckin", json.getInt("numberCheckin"));
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
            if (json.has("locName"))
                doc.append("locName",json.getString("locName"));
            if (json.has("cityName"))
                doc.append("cityName",json.getString("cityName"));
            if (json.has("stateName"))
                doc.append("stateName", json.getString("stateName"));
            if (json.has("countryName"))
                doc.append("countryName", json.getString("countryName"));
            if (json.has("isSuburb"))
                doc.append("isSuburb", json.getBoolean("isSuburb"));
            if (json.has("numberCheckin"))
                doc.append("numberCheckin", json.getInt("numberCheckin"));
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

}