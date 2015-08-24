package finals;

import java.io.IOException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * Final: Question 8
 * @author avachali
 *
 */
public class InsertTest {

    public static void main(String[] args) throws IOException {
        MongoClient c =  new MongoClient(new MongoClientURI("mongodb://localhost"));
        DB db = c.getDB("test");
        DBCollection animals = db.getCollection("animals");


        BasicDBObject animal = new BasicDBObject("animal", "monkey");

        animals.insert(animal);
        animal.removeField("animal");
        animal.append("animal", "cat");
        animals.insert(animal); // Error here because of duplicate key
        animal.removeField("animal");
        animal.append("animal", "lion");
        animals.insert(animal);

    }

}