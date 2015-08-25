package finals;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class RemoveOrphanImages {

	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		
		MongoClient client = new MongoClient();
		MongoDatabase db = client.getDatabase("photos");
		
		MongoCollection albums = db.getCollection("albums");
        
		MongoCollection images = db.getCollection("images");
		
		removeOrphanImage(albums, images);

		System.out.println("total time: "+ (System.currentTimeMillis() - startTime)/1000 + " secs.");
		
		System.out.println("Total images after removing orphans: "+ db.getCollection("images").count());
	}

	/**
	 * Loop through each image in the images collection; check if it exists in the albums collection. If it doesn't 
	 * exist, remove it from images collection.
	 * @param albums
	 * @param images
	 */
	private static void removeOrphanImage(MongoCollection albums, MongoCollection images) {
		
		MongoCursor imagesColl = images.find().iterator();
		int count = 0;
		while (imagesColl.hasNext()) {
			
			Document image = (Document) imagesColl.next();
			Object imageID = image.get("_id");
			
			MongoCursor imageInAlbumColl = albums.find(new Document("images", imageID)).iterator();
			
			if (! imageInAlbumColl.hasNext()) {
				System.out.println("Found an orphan...deleting:"+ imageID);
				count++;
				images.deleteOne(new Document("_id", imageID));
				
			}
			
			
		}
		
		System.out.println("total deleted images.."+ count);
	}

}
