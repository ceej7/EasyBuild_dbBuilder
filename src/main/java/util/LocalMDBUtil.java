package util;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import units.KeyValue;
import units.TitleKeyValue;

import java.lang.reflect.Array;
import java.util.*;

public class LocalMDBUtil {

    //local
    public static ServerAddress seed1 = new ServerAddress("localhost", 27017);
    public static String DEMO_DB = "db";
    public static String DEMO_COLL = "test";
    public static MongoClient createMongoDBClient() {
        // 构建Seed列表
        List<ServerAddress> seedList = new ArrayList<ServerAddress>();
        seedList.add(seed1);
        // 构建鉴权信息
        return new MongoClient(seedList);
    }
    public static void main(String args[]) {

    }

}
