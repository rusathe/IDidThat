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
import edu.cmu.sv.app17.helpers.APPListResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.Place;
import edu.cmu.sv.app17.models.Rating;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import edu.cmu.sv.app17.helpers.APPListResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Path("place")
public class PlaceInterface {
    private MongoCollection<Document> collection;
    private MongoCollection<Document> ratingsCollection;
    private ObjectWriter ow;


    public PlaceInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("ididthatDB");

        this.collection = database.getCollection("places");
        this.ratingsCollection = database.getCollection("ratings");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public ArrayList<Place> getAll() {

        try {
            ArrayList<Place> placesList = new ArrayList<Place>();


            FindIterable<Document> results = collection.find();
            if (results == null) {
                return placesList;
            }
            for (Document item : results) {
                Place place = new Place(
                        item.getString("placeName"),
                        item.getString("cityName"),
                        item.getString("placeCategoryType"),
                        item.getInteger("numberCheckin"),
                        item.getDouble("avgRating"),
                        item.getInteger("latestRankingbyCategory")
                );
                place.setId(item.getObjectId("_id").toString());
                placesList.add(place);
            }
            return placesList;
        }catch(NotFoundException e) {
            throw new APPNotFoundException(0,"This place is not listed by IDidThat");
        }
        catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
        public APPListResponse getplaceForPlaces(@PathParam("id") String id,
            @DefaultValue("20") @QueryParam("count") int count,
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("_id") @QueryParam("sort") String sortArg) {

            ArrayList<Place> placesList = new ArrayList<Place>();
            BasicDBObject sortParams = new BasicDBObject();
            List<String> sortList = Arrays.asList(sortArg.split(","));
            sortList.forEach(sortItem -> {
                sortParams.put(sortItem,1);
            });
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            long resultCount = collection.count(query);
            FindIterable<Document> results = collection.find(query).skip(offset).limit(count).sort(sortParams);
            for (Document item : results) {
                Place place = new Place(
                        item.getString("placeName"),
                        item.getString("cityName"),
                        item.getString("placeCategoryType"),
                        item.getInteger("numberCheckin"),
                        item.getDouble("avgRating"),
                        item.getInteger("latestRankingbyCategory")
                );
                place.setId(item.getObjectId("_id").toString());
                placesList.add(place);
            }
                return new APPListResponse(placesList,resultCount,offset,placesList.size());
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Incorrect ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Oops, Something went wrong");
        }


    }


    @GET
    @Path("{id}/rating")
    @Produces({MediaType.APPLICATION_JSON})
    public APPListResponse getRatingsForPlaces(@PathParam("id") String id,
            @DefaultValue("20") @QueryParam("count") int count,
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("_id") @QueryParam("sort") String sortArg) {

        ArrayList<Rating> ratingsList = new ArrayList<Rating>();
        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            BasicDBObject query = new BasicDBObject();
            query.put("placeID", id);
            long resultCount = ratingsCollection.count(query);
            FindIterable<Document> results = ratingsCollection.find(query).skip(offset).limit(count).sort(sortParams);
            for (Document item : results) {
                Integer ratings1 = item.getInteger("rating");
                Rating ratings = new Rating(
                        ratings1,
                        item.getInteger("avgRating"),
                        item.getString("placeID")
                );
                ratings.setId(item.getObjectId("_id").toString());
                ratingsList.add(ratings);
            }
            return new APPListResponse(ratingsList,resultCount,offset,ratingsList.size());

        } catch(Exception e) {
            System.out.println("Oops something went wrong");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
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
        if (!json.has("placeName") ) {
            throw new APPBadRequestException(55,"missing place name");
        }
        // You need to add all other fields
        Document doc = new Document("placeName", json.getString("placeName"))
                 .append("cityName", json.getString("cityName"))
                .append("placeCategoryType", json.getString("placeCategoryType"))
                .append("numberCheckins", json.getInt("numberCheckins"))
                .append("avgRating", json.getDouble("avgRating"))
                .append("latestRankingbyCategory", json.getInt("latestRankingbyCategory"));
        collection.insertOne(doc);

        return request;
    }

    @POST
    @Path("{id}/rating")
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
        if (!json.has("rating"))
            throw new APPBadRequestException(55,"missing rating");
        if (!json.has("avgRating"))
            throw new APPBadRequestException(55,"missing avgRating");
        Document doc = new Document("rating", json.getDouble("rating"))
                .append("avgRating", json.getDouble("avgRating"))
                .append("placeID", id);
        ratingsCollection.insertOne(doc);
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
            if (json.has("placeName"))
                doc.append("placeName",json.getString("placeName"));
            if (json.has("cityName"))
                doc.append("cityName", json.getString("cityName"));
            if (json.has("placeCategoryType"))
                doc.append("placeCategoryType", json.getString("placeCategoryType"));
            if (json.has("numberCheckin"))
                doc.append("numberCheckin", json.getInt("numberCheckin"));
            if (json.has("avgRating"))
                doc.append("avgRating", json.getDouble("avgRating"));
            if (json.has("latestRankingbyCategory"))
                doc.append("latestRankingbyCategory", json.getInt("latestRankingbyCategory"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return request;
    }


    //A place can only be created and updated - GET, POST and PATCH implemented.

}