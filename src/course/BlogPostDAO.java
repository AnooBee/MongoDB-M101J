package course;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Sorts;


/**
 * Blog post schema:
 * 
 * db.posts.find().pretty()
 *		{
 *	    "_id" : ObjectId("513d396da0ee6e58987bae74"),
 *	    "title" : "Martians to use MongoDB",
 *  	"author" : "andrew",
 *	    "body" : "Representatives from the planet Mars announced today that the planet would adopt MongoDB as a planetary standard. Head Martian Flipblip said that MongoDB was the perfect tool to store the diversity of life that exists on Mars.",
 *	    "permalink" : "martians_to_use_mongodb",
 *	    "tags" : [
 *	        "martians",
 *	        "seti",
 *	        "nosql",
 *	        "worlddomination"
 *	    ],
 *	    "comments" : [ ],
 *	    "date" : ISODate("2013-03-11T01:54:53.692Z")
 *		}
 * 
 */

public class BlogPostDAO {
    MongoCollection<Document> postsCollection;

    public BlogPostDAO(final MongoDatabase blogDatabase) {
        postsCollection = blogDatabase.getCollection("posts");
    }

    // Return a single post corresponding to a permalink
    public Document findByPermalink(String permalink) {

        // XXX HW 3.2,  Work Here
        Document post = null;

        Bson filter = new Document("permalink", permalink);
        post = postsCollection.find(filter).first();
                
        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<Document> findByDateDescending(int limit) {

        // XXX HW 3.2,  Work Here
        // Return a list of DBObjects, each one a post from the posts collection
        List<Document> posts = new ArrayList<Document>();
        
        Bson sort = Sorts.descending("date");
        FindIterable<Document> postList =  postsCollection.find().sort(sort).limit(limit);
        
        for (Document document : postList) {
			posts.add(document);
		}
        
        return posts;
    }


    public String addPost(String title, String body, List tags, String username) {

        System.out.println("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();


        // XXX HW 3.2, Work Here
        // Remember that a valid post has the following keys:
        // author, body, permalink, tags, comments, date
        //
        // A few hints:
        // - Don't forget to create an empty list of comments
        // - for the value of the date key, today's datetime is fine.
        // - tags are already in list form that implements suitable interface.
        // - we created the permalink for you above.

        // Build the post object and insert it
        Document post = new Document();
        List<Document> comments = new ArrayList(); //empty array to start
        
        post.append("title", title);
        post.append("author", username);
        post.append("body", body);
        post.append("permalink", permalink);
        post.append("tags", tags);
        post.append("comments", comments);
        post.append("date", new Date());
        
        //Add document to posts collection
        postsCollection.insertOne(post);

        return permalink;
    }




    // White space to protect the innocent







    /**
     * "comments" : [
	 *	{
	 *		"author" : "Larry Ellison",
	 *		"body" :  "blah bhal",
	 *		"email" : "larry@oracle.com"
	 *	},
	 *  {
	 *		"author" : "Bill Gates",
	 *		"body" :  "blah bhal"
	 *		
	 *	}]
	 *
     * @param name
     * @param email
     * @param body
     * @param permalink
     */

    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        // XXX HW 3.3, Work Here
        // Hints:
        // - email is optional and may come in NULL. Check for that.
        // - best solution uses an update command to the database and a suitable
        //   operator to append the comment on to any existing list of comments
    	
    	
    	Document commentsDoc = new Document();
    	commentsDoc.append("author", name);
    	commentsDoc.append("body", body);
    	
    	if (checkForNull(email)) {
    		commentsDoc.append("email", email);
    	}
    	//Filter by permalink
    	Bson filter = Filters.eq("permalink", permalink);
    	//Stick the comments in a doc   	   
    	Bson newComment = new Document("comments", commentsDoc);
    	
    	//$push	Adds an item to an array. http://docs.mongodb.org/manual/reference/operator/update/
    	postsCollection.updateOne(filter, new Document("$push", newComment));
    	
    	
    	// findOneAndUpdate wont work correctly, because we need to add comments in an array; this will keep adding new comments [] elements.
    	//postsCollection.findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().upsert(true));
    }

    /**
     * Checks if string is null or empty
     * @param str
     * @return true if valid string (not null and length > 0)
     */
	private boolean checkForNull(String str) {
		
		boolean valid = false;
		
		if (null != str && str.trim().length() > 0) {
			valid = true;
		}
		
		return valid;
	}
}
