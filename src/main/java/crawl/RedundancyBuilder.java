package crawl;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import util.LocalMDBUtil;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public class RedundancyBuilder {
    static MongoDatabase database;
    public static void main(String[] args) {
        MongoClient client = LocalMDBUtil.createMongoDBClient();
        try {
            // 取得Collecton句柄
            database = client.getDatabase("building");
            MongoCollection<Document> collection = database.getCollection("items");
            FindIterable<Document> findIterable = collection.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while(mongoCursor.hasNext()){
                //对每一个item
                org.bson.Document thisdoc=mongoCursor.next();
                String id=thisdoc.getString("itemID");
                System.out.println(id);
                traverse(id,"cpu",collection);
                traverse(id,"gpu",collection);
                traverse(id,"motherboard",collection);
                traverse(id,"case",collection);
                traverse(id,"cooler_water",collection);
                traverse(id,"cooler_wind",collection);
                traverse(id,"hdd",collection);
                traverse(id,"memory",collection);
                traverse(id,"power",collection);
                traverse(id,"ssd",collection);
            }
        } finally {
            //关闭Client，释放资源
            client.close();
        }


    }

    public static Document examUniqueDocument(MongoCursor<Document> mc,String id)
    {
        int i=0;
        Document doc=null;
        while(mc.hasNext())
        {
            if(i!=0)
            {
                System.out.println(id+"   duplicated");
                return null;
            }
            doc=mc.next();
        }
        return doc;
    }
    public static void traverse(String id,String type,MongoCollection<Document> collection )
    {
        MongoCollection<Document> coll = database.getCollection(type);
        List<String> ids= new ArrayList<String>();
        ids.add(id);
        BasicDBObject queryObject = new BasicDBObject("itemIDs",new BasicDBObject("$all",ids));
        FindIterable<Document> findIt = coll.find(queryObject);
        MongoCursor<Document> mc = findIt.iterator();
        Document doc=examUniqueDocument(mc,id);
        if(doc!=null)
        {
            collection.updateOne(Filters.eq("itemID", id), new Document("$set",new Document(type,doc)));
            collection.updateOne(Filters.eq("itemID", id), new Document("$set",new Document(type,doc)));
            System.out.println(doc+"   inserted");
        }
    }

}
