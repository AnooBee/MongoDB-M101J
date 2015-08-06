package hw.hw3;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author avachali
 *
 */
public class HW3 {

	public static void main(String[] args) {
		
		MongoClient client = new MongoClient();
		
			
		doHW31(client);
		
	}

	/**
	 * Deletes the least homework score for each student.
	 * 
	 * Find lowest homework score in each document; delete that from scores array
	 *
	 * @param client
	 */
	private static void doHW31(MongoClient client) {
		
		/*Schema: 
		{
        "_id" : 1,
        "name" : "Aurelia Menendez",
        "scores" : [
                {
                        "type" : "exam",
                        "score" : 60.06045071030959
                },
                {
                        "score" : 52.79790691903873,
                        "type" : "quiz"
                },
                {
                        "type" : "homework",
                        "score" : 71.76133439165544
                },
                {
                        "type" : "homework",
                        "score" : 34.85718117893772
                }
        ]
		}*/
		
		MongoDatabase db = client.getDatabase("school");
		
	/*	MongoCollection<Document> collection = db.getCollection("students");
		
		System.out.println("Collection count - start: "+ collection.count()); //200
		
		Bson scoresFilter = new Document("", "scores"); //find All and return only the scores field
*/		
		MongoCursor<Document> scoresColl = db.getCollection("students").find().iterator();
		
		double id;
		try {
						
			Document studentScore;
			
			while (scoresColl.hasNext()) {
				
				ArrayList hwArray;
				
				studentScore = scoresColl.next();
				id = studentScore.getDouble("_id");
				System.out.println("id..."+ id);
				BasicDBList scoreList =   (BasicDBList) studentScore.get("scores");
				
				for (int i = 0; i < scoreList.size(); i++) {
					
					DBObject score = (DBObject) scoreList.get(i);
					if (score.get("type").toString().equalsIgnoreCase("homework")) {
						System.out.println(score);
					}
				}
				
				
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} finally {
			scoresColl.close();
		}
	}

}
