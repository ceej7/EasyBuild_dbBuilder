package DailyMaintance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import units.GoodUnit;
import util.DBUtil;
import util.LocalMDBUtil;
import util.RemoteMDBUtil;

import javax.print.Doc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This Demo is used for Crawl Data of all Items&type in JD
 * based on the BASE_URL
 */
public class ItemsUpdate {
    String COMMENT_BASE_URL="https://club.jd.com/clubservice.aspx?method=GetCommentsCount&referenceIds=";
    String PRICE_BASE_URL="https://p.3.cn/prices/mgets?type=1&area=1_72_2799_0&pdbp=0&pdtk=&pdpin=ceej_7&pduid=1517923482737888462660&source=list_pc_front&_=1527740885630&&skuIds=";
    HashMap<Long, Float> good2price=new HashMap<Long, Float>();
    HashMap<Long, Integer> good2comments=new HashMap<Long, Integer>();
    static final ObjectMapper MAPPER=new ObjectMapper();

   public void StartCrawling()
   {
       //数据爬取
       try {

           Vector<String> ids=new Vector<String>();
           //取得需要维护的id
           MongoClient client = RemoteMDBUtil.createMongoDBClient();
           // 取得Collecton句柄
           MongoDatabase database = client.getDatabase("building");
           MongoCollection<org.bson.Document> collection = database.getCollection("items");
           MongoCursor<org.bson.Document> cursor = collection.find().iterator();
           int i=0;
            while (cursor.hasNext()) {
                ids.add(StringUtils.replace(cursor.next().getString("itemID"),"JD_",""));
                i++;
                if (i % 100 == 99) {
                    maintainComment(ids);
                    maintainPrice(ids);
                    System.out.println("A Thous items updated");
                    ids.clear();
                }
            }
           maintainComment(ids);
           maintainPrice(ids);
           ids.clear();
       } catch (Exception e) {
           e.printStackTrace();
       }

       //数据持久
       MongoClient client = RemoteMDBUtil.createMongoDBClient();
       // 取得Collecton句柄
       MongoDatabase database = client.getDatabase("building");
       MongoCollection<org.bson.Document> collection = database.getCollection("items");
       for (long id:good2price.keySet()
            ) {
           //对每一个id更新
           BasicDBObject filter=new BasicDBObject("itemID","JD_"+id);
           FindIterable<org.bson.Document> findIt = collection.find(filter);
           MongoCursor<org.bson.Document> mc = findIt.iterator();
           //得到对应的doc
           org.bson.Document doc=null;
           if(mc.hasNext())
           {
               doc=mc.next();
           }
           if(doc!=null)
           {
               //更新价格波动
               org.bson.Document prices=(org.bson.Document)doc.get("prices");
               JSONObject priceJSON=JSONObject.fromObject(prices.toJson());
               Iterator<String> sIterator = priceJSON.keys();
               Float lastprice=0.0f;
               while(sIterator.hasNext()){
                   // 获得key
                   String key = sIterator.next();
                   lastprice=Float.parseFloat(priceJSON.getString(key));
               }
               if(!lastprice.equals(good2price.get(id)))
               {
                   prices.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),good2price.get(id));
                   collection.updateOne(new BasicDBObject("itemID","JD_"+id),new BasicDBObject("$set",new BasicDBObject("prices",prices)));
                   System.out.println("JD_"+id+"prices updated");
               }
               //更新销量
               collection.updateOne(new BasicDBObject("itemID","JD_"+id),new BasicDBObject("$set",new BasicDBObject("comments",good2comments.get(id))));
           }
       }
   }


    /**
     * Get the content of URL
     * return content in String
     * @param url
     * @return
     */
    private String doGet(String url)throws Exception{
        //创建httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        //创建http请求
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = null;
        try {
            //执行请求
            response = httpclient.execute(httpGet);
            //判断状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                return content;
            }
        }
        finally {
            if(response !=null)
            {
                response.close();
            }
            httpclient.close();
        }
        return null;

    }


    /**
     *traverse all items price given
     * store in good2price maps
     * @throws Exception
     */
    private void maintainComment(Vector<String> ids)throws Exception{
        if(ids.size()==0)
            return;
        //获得价格 因为使用ajax异步加载(CALLBACK JQUERY) 所以需要从network里找到原来的url,这里是PRICE_BASE_URL+"J_"+"XXX"+","+"J_"+...
        String priceURL=COMMENT_BASE_URL+StringUtils.join(ids,',');
        String jsonData=doGet(priceURL);
        JSONObject node= JSONObject.fromObject(jsonData);
        JSONArray array=JSONArray.fromObject(node.getString("CommentsCount"));
        for (Object obj:array
             ) {
            JSONObject kv= JSONObject.fromObject(obj);
            Long id=kv.getLong("ProductId");
            Integer cmt=kv.getInt("CommentCount");
            good2comments.put(id,cmt);
        }
    }

    /**
     *traverse all items price given
     * store in good2price maps
     * @throws Exception
     */
    private void maintainPrice(Vector<String> ids)throws Exception{
        if(ids.size()==0)
            return;
        //获得价格 因为使用ajax异步加载(CALLBACK JQUERY) 所以需要从network里找到原来的url,这里是PRICE_BASE_URL+"J_"+"XXX"+","+"J_"+...
        String priceURL=PRICE_BASE_URL+StringUtils.join(ids,',');
        String jsonData=doGet(priceURL);
        ArrayNode arrayNode=(ArrayNode) MAPPER.readTree(jsonData);
        for(JsonNode jsonNode: arrayNode)
        {
            Long id=Long.valueOf(StringUtils.substringAfter(jsonNode.get("id").asText(),"_"));
            Float price=Float.parseFloat(jsonNode.get("p").asText());
            good2price.put(id,price);
        }
    }


}
