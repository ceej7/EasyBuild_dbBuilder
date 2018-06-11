package crawl;

import units.GPUUnit;
import units.GoodUnit;
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
import util.DBUtil;
import util.Formatting;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Vector;

/**
 * This Demo is used for Crawl Data of all Items&type in JD
 * based on the BASE_URL
 */
public class CrawlerDemo {
    public static void main(String[] args) {
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,682&ev=3680_97403&page={page}&delivery=1","cooler_wind").StartCrawling();//散热-风冷
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,682&ev=3680_97402&page={page}&delivery=1","cooler_water").StartCrawling();//散热-水冷
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,691&page={page}&delivery=1","power").StartCrawling();//电源
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,687&page={page}&delivery=1","case").StartCrawling();//机箱
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,680&page={page}&delivery=1","memory").StartCrawling();//memory
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,11303&page={page}&delivery=1","ssd").StartCrawling();//ssd
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,683&page={page}&delivery=1","hdd").StartCrawling();//hdd
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,679&page={page}&delivery=1","gpu").StartCrawling();//gpu
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,681&page={page}&delivery=1","motherboard").StartCrawling();//主板
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,678&page={page}&delivery=1","cpu").StartCrawling();//cpu


        /**
         * depricated
         */
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,688&page={page}&delivery=1").StartCrawling();//显示器
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,682&ev=3680_1062&page={page}&delivery=1").StartCrawling();//机箱风扇
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,684&page={page}&delivery=1").StartCrawling();//光驱
        //        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,5008&ev=878_43268&page={page}&delivery=1").StartCrawling();//声卡
    }

    String BASE_URL="https://list.jd.com/list.html?cat=9987,653,655&page={page}";
    String PRICE_BASE_URL="https://p.3.cn/prices/mgets?type=1&area=1_72_2799_0&pdbp=0&pdtk=&pdpin=ceej_7&pduid=1517923482737888462660&source=list_pc_front&_=1527740885630&&skuIds=";
    static final ObjectMapper MAPPER=new ObjectMapper();
    HashMap<Long, GoodUnit> goods;
    //String outputfile;
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;
    String type;
   public void StartCrawling()
   {
       try {
           traversePages();
       } catch (Exception e) {
           e.printStackTrace();
       }
       //持久化
       try{
           conn= DBUtil.getConnection();
           String sql="insert into items(id,title,img,price,type) values (?,?,?,?,?)";
           // JAVA默认为TRUE,我们自己处理需要设置为FALSE,并且修改为手动提交,才可以调用rollback()函数
           conn.setAutoCommit(false);
           ps = conn.prepareStatement(sql);
           int i=0;
           for (GoodUnit good:goods.values()) {
               if(!DBUtil.checkItem(good.getId()))
               {
                   ps.setLong(1, good.getId());
                   ps.setString(2, good.getTitle());
                   ps.setString(3, good.getImg());
                   ps.setFloat(4, good.getPrice());
                   ps.setString(5, type);
                   ps.addBatch();
                   //防止内存溢出，我也不是很清楚都这么写
                   if ((i + 1) % 1000 == 0) {
                       ps.executeBatch();
                       ps.clearBatch();
                   }
                   i++;
               }
               else{
                   System.out.println(good.toString()+" exists");
               }

           }
           ps.executeBatch(); // 批量执行
           conn.commit();// 提交事务
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
    public CrawlerDemo(String url, String _type){
        goods=new HashMap<Long, GoodUnit>();
        BASE_URL=url;
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
     * get the number of pages through BASE_URL
     * return Integer
     * @return
     * @throws Exception
     */
    private Integer getPages()throws Exception{
        //入口页面
        String startURL= StringUtils.replace(BASE_URL,"{page}",String.valueOf(1));
        Integer totalPages= 0;
        //获取启示页面URL
        String html=doGet(startURL);
        //解析html
        Document document= Jsoup.parse(html);
        String pageText=document.select("#J_topPage").text();
        //正则表达分割字符串
        String[] strs=pageText.split("\\D+");
        totalPages=Integer.parseInt(strs[1]);
        return totalPages;
    }

    /**
     *traverse all items in the page
     * store in good maps
     * @throws Exception
     */
    private void traversePage(Document root)throws Exception{
            Elements lis=root.select("#plist li.gl-item");//得到商品列表
        Vector<String> ids=new Vector<String>();//维护本页商品的id

        //遍历商品列表
        //在维护并加入hashMap-goods
        for(Element li:lis)
        {
            Element div=li.child(0);
            //取得ID,并加入ids
            Long id ;
            try{
                id=Long.valueOf(div.attr("data-sku"));
            }
            catch (Exception e)
            {
                div=li.child(0).child(0).child(1).child(0);
                id=Long.valueOf(div.attr("data-sku"));
            }
            ids.add("J_"+id);
            //取得图片
            String image=li.select(".p-img img").attr("src");
            if(image.length()==0)
            {
                image=li.select(".p-img img").attr("data-lazy-img");
            }
            //获得title
            String title=li.select(".p-name").text();
            GoodUnit _good=new GoodUnit();
            _good.setId(id);
            _good.setImg(image);
            _good.setTitle(title);
            //System.out.println(_good.toString());
            goods.put(id,_good);
        }
        //获得价格 因为使用ajax异步加载(CALLBACK JQUERY) 所以需要从network里找到原来的url,这里是PRICE_BASE_URL+"J_"+"XXX"+","+"J_"+...
        String priceURL=PRICE_BASE_URL+StringUtils.join(ids,',');
        //System.out.println(priceURL);
        String jsonData=doGet(priceURL);
        ArrayNode arrayNode=(ArrayNode) MAPPER.readTree(jsonData);
        for(JsonNode jsonNode: arrayNode)
        {
            Long id=Long.valueOf(StringUtils.substringAfter(jsonNode.get("id").asText(),"_"));
            String mprice=jsonNode.get("m").asText();
            String oprice=jsonNode.get("op").asText();
            String price=jsonNode.get("p").asText();
            GoodUnit _good=goods.get(id);
            _good.setMprice(Float.valueOf(mprice));
            _good.setOprice(Float.valueOf(oprice));
            _good.setPrice(Float.valueOf(price));
        }

    }

    /**
     *traverse all pages
     * @throws Exception
     */
    private void traversePages()throws Exception{
        Integer totalPages=getPages();
        //分页查询数据
        for (int i = 1; i <= totalPages; i++) {
            String url= StringUtils.replace(BASE_URL,"{page}",String.valueOf(i));
            System.out.println(url);
            String content = doGet(url);//商品数据
            Document root=Jsoup.parse(content);//Jsoup解析
            traversePage(root);
        }
    }
}
