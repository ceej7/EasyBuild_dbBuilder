package crawl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import units.GoodUnit;
import util.DBUtil;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 从数据库中提取id，根据id得到html文件内容，进行mysql持久化
 */
public class ItemDetailDemo {
    String BASE_URL="https://item.jd.com/{page}.html";

    //Storage
    Vector<Long> itemIDs;

    //String outputfile;
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;
    String type;

   public void StartCrawling()
   {
       //IDs initial
       itemIDs=new Vector<Long>();
       //查询对应type的id
       try {

           conn = DBUtil.getConnection();
           String sql = "select id from items where type=?";
           ps = conn.prepareStatement(sql);
           ps.setString(1, type);
           rs = ps.executeQuery();
           while (rs.next()) {
               itemIDs.add(rs.getLong("id"));
           }
           conn.close();

       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
       //插入对应的description
       try{
           conn = DBUtil.getConnection();
           for (Long id : itemIDs
                   ) {
               String sql = "update items set pageText=? WHERE id=?";
               ps = conn.prepareStatement(sql);
               ps.setString(1, getPageText(id));
               ps.setLong(2,id);
               ps.executeUpdate();
           }
           conn.close();
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
   }

    /**
     * Constructor
     */
    public ItemDetailDemo(String _type){
        type=_type;
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
     * Get pageText
     */
    private String getPageText(long itemID)throws Exception{

        String itemURL= StringUtils.replace(BASE_URL,"{page}",String.valueOf(itemID));
        System.out.println(itemURL);
        String html=doGet(itemURL);
        return html;
    }

    public static void main(String[] args) {
//        new ItemDetailDemo("motherboard").StartCrawling();
//        new ItemDetailDemo("case").StartCrawling();
//        new ItemDetailDemo("cpu+motherboard").StartCrawling();
//        new ItemDetailDemo("cooler_wind").StartCrawling();
//        new ItemDetailDemo("cooler_water").StartCrawling();
//        new ItemDetailDemo("power").StartCrawling();
//        new ItemDetailDemo("memory").StartCrawling();
//        new ItemDetailDemo("ssd").StartCrawling();
//        new ItemDetailDemo("hdd").StartCrawling();
//        new ItemDetailDemo("case").StartCrawling();

    }

}
