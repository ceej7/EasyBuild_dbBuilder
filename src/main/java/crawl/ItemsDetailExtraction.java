package crawl;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import units.KeyValue;
import units.TitleKeyValue;
import util.DBUtil;
import util.LocalMDBUtil;

import java.security.Key;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 从mysql中提取html文件解析出[title [key-value]s]s信息
 */
public class ItemsDetailExtraction {
    //String outputfile;
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;
    String type;
    HashMap<Long,Vector<TitleKeyValue>> items;

    /**
     * Constructor
     * @param _type
     */
    public ItemsDetailExtraction(String _type)
    {
        type=_type;
        items=new HashMap<Long,Vector<TitleKeyValue>>();
    }

    /**
     * 启动解析
     */
    public void startExtracting()
    {
        //从持久化好的mysql中提取出
        try {
            conn = DBUtil.getConnection();
            String sql = "select id,pageText from items where type=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, type);
            rs = ps.executeQuery();
            while (rs.next()) {
                Long id=rs.getLong(1);
                String text=rs.getString(2);
                Vector<TitleKeyValue> detail=textParsing(id,text);
                items.put(id,detail);
            }
            conn.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //做local_mongo的持久化
        MongoClient client = LocalMDBUtil.createMongoDBClient();
        try {
            // 取得Collecton句柄
            MongoDatabase database = client.getDatabase("building");
            MongoCollection<org.bson.Document> collection = database.getCollection(type);

            // 插入数据
            for (Long id:items.keySet()
                 ) {
                Vector<TitleKeyValue> item=items.get(id);
                org.bson.Document doc = new org.bson.Document();
                //存入[title:doc]s  doc={key:value, key:value...}
                for (TitleKeyValue tkv:item
                     ) {
                    org.bson.Document doc_kvs=new org.bson.Document();
                    //存每个key-value到 document里
                    for (KeyValue kv:tkv.kvs
                         ) {
                        doc_kvs.append(kv.key,kv.value);
                    }
                    doc.append(tkv.title, doc_kvs);
                }
                //存入[itemIDs-[id, id, id...]]
                String[] itemIDs=new String[1];
                itemIDs[0]="JD_"+id.toString();
                doc.append("itemIDs", Arrays.asList(itemIDs) );
                collection.insertOne(doc);
                System.out.println("insert document: " + doc);
            }



        } finally {
            //关闭Client，释放资源
            client.close();
        }
        return ;
    }

    /**
     * 解析的item id和html text
     * @param id
     * @param text
     */
    public Vector<TitleKeyValue> textParsing(Long id,String text)
    {
        Vector<TitleKeyValue> detail =new Vector<TitleKeyValue>();
        if(text==null||text=="")
        {
            System.out.println("Content Exception: You do not have htmlText for item "+id);
            return null;
        }
        Document root= Jsoup.parse(text);//Jsoup解析
        Elements pitems=root.select(".Ptable-item");
        for (Element pitem: pitems
             ) {
            TitleKeyValue tkv=new TitleKeyValue();
            String title=pitem.select("h3").text();
            tkv.title=title;
            Elements dts=pitem.select("dl").get(0).select("dt");
            Elements dds=pitem.select("dl").get(0).select("dd");
            for (int i = 0; i <dts.size() ; i++) {
                KeyValue kv= new KeyValue();
                kv.key=dts.get(i).text();
                if(i<dds.size())
                {
                    kv.value=dds.get(i).text();
                }
                tkv.kvs.add(kv);
            }
            detail.add(tkv);
        }
        return detail;
    }

    public static void main(String[] args) {
//        new ItemsDetailExtraction("motherboard").startExtracting();
//        new ItemsDetailExtraction("case").startExtracting();
//        new ItemsDetailExtraction("cooler_wind").startExtracting();
//        new ItemsDetailExtraction("cooler_water").startExtracting();
//        new ItemsDetailExtraction("power").startExtracting();
//        new ItemsDetailExtraction("memory").startExtracting();
//        new ItemsDetailExtraction("ssd").startExtracting();
//        new ItemsDetailExtraction("hdd").startExtracting();
        //根据主体为xxx去重的脚本
//        HashMap<String, org.bson.Document> s2d=new HashMap<String, org.bson.Document>();
//        Vector<org.bson.Document> s0d=new Vector<org.bson.Document>();
//        MongoClient client = LocalMDBUtil.createMongoDBClient();
//        try {
//            // 取得Collecton句柄
//            MongoDatabase database = client.getDatabase("building");
//            MongoCollection<org.bson.Document> collection = database.getCollection("xxx");
//            FindIterable<org.bson.Document> findIterable = collection.find();
//            MongoCursor<org.bson.Document> mongoCursor = findIterable.iterator();
//            while(mongoCursor.hasNext()){
//                org.bson.Document thisdoc=mongoCursor.next();
//                thisdoc.remove("_id");
//                Object bdy=thisdoc.get("主体");
//                if(bdy==null)
//                {
//                    s0d.add(thisdoc);
//                }
//                else{
//                    String title=bdy.toString().toLowerCase();
//                    if(!s2d.containsKey(title))
//                    {
//                        s2d.put(title,thisdoc);
//                    }
//                    else
//                    {
//                        org.bson.Document _doc=s2d.get(title);
//                        String str1=_doc.get("itemIDs").toString().replace("[","").replace("]","");
//                        String str2=thisdoc.get("itemIDs").toString().replace("[","").replace("]","");
//                        String[] arr1= StringUtils.split(str1,",");
//                        String[] arr2=StringUtils.split(str2,",");
//                        ArrayList<String> arrString=new ArrayList<String>();
//                        for (int i = 0; i < arr1.length; i++) {
//                            arrString.add(arr1[i].replace(" ",""));
//                        }
//                        arrString.add(arr2[0].replace(" ",""));
//
//
//                        _doc.remove("itemIDs");
//                        String[] itemIDs=new String[arrString.size()];
//                        for (int i = 0; i <arrString.size() ; i++) {
//                            itemIDs[i]=arrString.get(i);
//                        }
//                        _doc.append("itemIDs",Arrays.asList(itemIDs));
//                    }
//                }
//            }
//        } finally {
//            //关闭Client，释放资源
//            client.close();
//        }
//        //做local_mongo的持久化
//        client = LocalMDBUtil.createMongoDBClient();
//        try {
//            // 取得Collecton句柄
//            MongoDatabase database = client.getDatabase("building");
//            MongoCollection<org.bson.Document> collection = database.getCollection("tmp");
//
//            // 插入数据
//            for (org.bson.Document doc: s2d.values()
//                    ) {
//                collection.insertOne(doc);
//            }
//            for (org.bson.Document doc: s0d
//                    ) {
//                collection.insertOne(doc);
//                System.out.println(doc);
//            }
//
//
//        } finally {
//            //关闭Client，释放资源
//            client.close();
//        }
    }


}
