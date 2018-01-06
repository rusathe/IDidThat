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
import edu.cmu.sv.app17.helpers.APPListResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.Place;
import edu.cmu.sv.app17.models.Rating;
import edu.cmu.sv.app17.models.SearchData;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Path("searchdata")
public class SearchDataInterface {
    private MongoCollection<Document> collection;
    private ObjectWriter ow;


    public SearchDataInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("ididthatDB");

        this.collection = database.getCollection("searchdata");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public ArrayList<SearchData> getAll() {

        try {
            ArrayList<SearchData> SearchDataList = new ArrayList<SearchData>();


            FindIterable<Document> results = collection.find();
            if (results == null) {
                return SearchDataList;
            }
            for (Document item : results) {
                SearchData searchData = new SearchData(
                        item.getString("placeType"),
                        item.getString("placeLocation")
                );
                searchData.setId(item.getObjectId("_id").toString());
                SearchDataList.add(searchData);
            }
            return SearchDataList;
        }catch(NotFoundException e) {
            throw new APPNotFoundException(0,"This search is not listed by IDidThat");
        }
        catch(Exception e) {
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
        Document doc = new Document("placeLocation", json.getString("placeLocation"))
                .append("placeType", json.getString("placeType"));
        collection.insertOne(doc);
        return request;
    }

    //A place can only be created and updated - GET, POST and PATCH implemented.

}