package hw.hw3;

import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
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
	
		//get student cursor for easier navigation through the documents
		MongoCursor<Document> studentsCursor = db.getCollection("students").find().iterator();
		
		//Student collection for update operation
		MongoCollection<Document> studentsCollection = db.getCollection("students");

		double id; //student id
		
		try {
						
			Document studentScore;
			
			while (studentsCursor.hasNext()) {
								
				studentScore = studentsCursor.next();
				id = studentScore.getDouble("_id");
				System.out.println("id..."+ id);
				
				//Get array of scores 
				List<Document> scoreList = (List<Document>) studentScore.get("scores");
				double lowestScore = 0.0;
				
				//Find lowest homework score in the scores array for each student
				for (Document scoreObject : scoreList) {

					String scoreType = (String) scoreObject.get("type");

					//compare homework scores to find the lowest score
					if ("homework".equalsIgnoreCase(scoreType)) {
						double score = (Double) scoreObject.get("score");
						if (lowestScore == 0.0 || (score < lowestScore)) {
							lowestScore = score;
						}
						//System.out.println("lowest homework score: "+lowestScore);
					}

				}
				// remove the lowest score from scorelist 
				Document lowScoreToRemove = new Document("type", "homework").append("score", lowestScore);
				System.out.println("Removing: " + lowScoreToRemove);
				scoreList.remove(lowScoreToRemove);
				
				//Update the students collection with the new array of scores
				BasicDBObject newDocument = new BasicDBObject();
				newDocument.append("$set", new BasicDBObject().append("scores", scoreList));
				
				//search by student id
				BasicDBObject searchQuery = new BasicDBObject().append( "_id", id);
				
				studentsCollection.updateOne(searchQuery, newDocument);
								
			}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} finally {
			studentsCursor.close();
		}
	}

}
