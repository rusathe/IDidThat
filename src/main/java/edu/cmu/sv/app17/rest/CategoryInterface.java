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
import edu.cmu.sv.app17.models.Category;
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


@Path("category")
public class CategoryInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> reviewCollection;
    private ObjectWriter ow;


        public CategoryInterface() {
            MongoClient mongoClient = new MongoClient();
            MongoDatabase database = mongoClient.getDatabase("ididthatDB");

            this.collection = database.getCollection("users"); //check
            this.reviewCollection = database.getCollection("category"); //check
            ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        }

        @GET
        @Produces({ MediaType.APPLICATION_JSON})
        public APPResponse getAll(@DefaultValue("_id") @QueryParam("sort") String sortArg) {

            ArrayList<Category> categoryList = new ArrayList<Category>();

            BasicDBObject sortParams = new BasicDBObject();
            List<String> sortList = Arrays.asList(sortArg.split(","));
            sortList.forEach(sortItem -> {
                sortParams.put(sortItem,1);
            });

            try {
                FindIterable<Document> results = collection.find().sort(sortParams);
                for (Document item : results) {
                    String categoryName = item.getString("categoryName");
                    Category category = new Category(
                            categoryName
                    );
                    category.setId(item.getObjectId("_id").toString());
                    categoryList.add(category);
                }
                return new APPResponse(categoryList);

            } catch(Exception e) {
                throw new APPInternalServerException(99,"Oops, Something went wrong");
            }

        }

        @GET
        @Path("{id}")
        @Produces({MediaType.APPLICATION_JSON})
        public Category getOne(@PathParam("id") String id) {
            BasicDBObject query = new BasicDBObject();
            try {
                query.put("_id", new ObjectId(id));
                Document item = collection.find(query).first();
                if (item == null) {
                    throw new APPNotFoundException(0, "No such category");
                }
                Category category = new Category(
                        item.getString("categoryName")
                );
                category.setId(item.getObjectId("_id").toString());
                return category;

            } catch(APPNotFoundException e) {
                throw new APPNotFoundException(0,"No such category");
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
            if (!json.has("categoryName") ) {
                throw new APPBadRequestException(55,"missing categoryName");
            }

            Document doc = new Document("categoryName", json.getString("categoryName"));
            collection.insertOne(doc);

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
