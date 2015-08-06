package hw.hw2;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

/**
 * 
 * @author avachali
 *
 */
public class HW2 {

	public static void main(String[] args) {
		
		MongoClient client = new MongoClient();
		MongoDatabase db = client.getDatabase("students");
		
		MongoCollection collection = db.getCollection("grades");
		
		System.out.println("Collection count - start: "+ collection.count()); //800
			
		doHW23(collection);
		
	}

	/**
	 * Deletes the record with the least homework score for each student.
	 * 
	 * Filter  homework records and then filter by score to get two records per student. The second record will be the 
	 * lesser score of the two. Delete the second record for each student.
	 * 
	 * @param collection
	 */
	private static void doHW23(MongoCollection collection) {
			
		Bson filter = new Document("type", "homework"); //Filter out all homework records
		//Bson filter = Filters.eq("type", "homework"); // alt filtering method
		Bson sort = Sorts.descending("student_id", "score"); // sort by student_id and score, descending
		
		MongoCursor hwColl =  collection.find(filter).sort(sort).iterator();
		
		/* Schema:		
		{
	        "_id" : ObjectId("50906d7fa3c412bb040eb577"),
	        "student_id" : 0,
	        "type" : "exam",
	        "score" : 54.6535436362647
		}*/
		
		try {
			
			System.out.println("Collection count - filtered for homework: "+hwColl); //400
			
			Double student_id;
			Double prevStudent_id = null;
			int count = 0;
			
			while (hwColl.hasNext()) {
				Document studentGrade = (Document) hwColl.next();
				student_id = studentGrade.getDouble("student_id");
							
				if (student_id.equals(prevStudent_id)) {
					collection.deleteOne(new Document("_id", studentGrade.getObjectId("_id")));
					count++;
				}
				
				prevStudent_id = student_id; //set prevStudent_id to student_id
				
			}
			
			System.out.println("Total of deleted records - "+ count); //200
			System.out.println("Collection Count after Deleting: "+ collection.count()); //600
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} finally {
			hwColl.close();
		}
	}

}
