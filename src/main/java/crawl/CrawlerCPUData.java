package crawl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import units.CPUUnit;
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
import util.LocalMDBUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Vector;

/**
 * 从网站爬CPU参数存入mysql
 */
public class CrawlerCPUData {
    public static void main(String[] args) {
        //mysql数据持久化
        //        new CrawlerCPUData().Start();
        //数据迁移至mongoDB
        new CrawlerCPUData().TransitionStart();
    }

     static  final String BASE_URL="https://www.techpowerup.com/cpudb/?mfgr%5B%5D=amd&mfgr%5B%5D=intel&class%5B%5D=desktop&class%5B%5D=server&released%5B%5D=y17_c&released%5B%5D=y14_17&released%5B%5D=y11_14&released%5B%5D=y08_11&released%5B%5D=y05_08&released%5B%5D=y00_05&logo=&nCores=&process=&socket=&codename=&multi=&sort=name&q=";
    HashMap<String,Vector<CPUUnit>> cpus;
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;
//    String file;

    /**
     * Constructor
     */
    public CrawlerCPUData(/*String file*/){
        cpus=new HashMap<String,Vector<CPUUnit>>();
//        this.file=file;
    }

    /**
     * The process of crawling Data
     * @throws Exception
     */
    public void getData()throws Exception{
        String content=doGet(BASE_URL);
        Document root=Jsoup.parse(content);
        Elements tbodies=root.select(("#list table tbody"));
        for(Element tbody:tbodies)
        {
            Elements trs=tbody.children();
            for(Element tr:trs)
            {

                CPUUnit cpu=new CPUUnit(tr.child(0).text(),tr.child(1).text(),tr.child(2).text(),tr.child(3).text(),tr.child(4).text(),tr.child(5).text(),tr.child(6).text(),tr.child(7).text(),tr.child(8).text(),tr.child(9).text());
                //System.out.println(cpu.toString());
                Vector<CPUUnit>_cpus= cpus.get(cpu.getName());
                if(_cpus!=null)
                {
                    boolean flg=false;
                    for(CPUUnit cpu_test:_cpus)
                    {
                        if(cpu_test.getName().equals(cpu.getName())&&cpu_test.getCodename().equals(cpu.getCodename())&&cpu_test.getSocket().equals(cpu.getSocket()))
                        {
                            //System.out.println(cpu.toString()+"----Conflict");
                            flg=true;
                            break;
                        }
                    }
                    if(!flg)
                        _cpus.add(cpu);
                }
                else{
                    _cpus=new Vector<CPUUnit>();
                    _cpus.add(cpu);
                }
                cpus.put(cpu.getName(),_cpus);
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
     * Data persistance
     */
    public void Start(){
        //爬取数据
        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //数据持久化
        try {
            conn= DBUtil.getConnection();
            String sql="insert into cpu(Name,Codename,Cores,Threads,Socket,Process,Clock,Multi,CacheL1,CacheL2,CacheL3,TDP,Released) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            // JAVA默认为TRUE,我们自己处理需要设置为FALSE,并且修改为手动提交,才可以调用rollback()函数
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            int i=0;
            for (Vector<CPUUnit> _cpus:cpus.values())
            {
                for (CPUUnit cpu:_cpus ) {
                    //设置value值
                    System.out.println(cpu.toString()+"buffered-------------------");
                    ps.setString(1, cpu.getName());
                    ps.setString(2, cpu.getCodename());
                    ps.setInt(3, cpu.getCores());
                    if(cpu.getThreads()==-1)
                    {
                        ps.setNull(4, Types.INTEGER);
                    }
                    else{
                        ps.setInt(4, cpu.getThreads());
                    }
                    ps.setString(5, cpu.getSocket());
                    ps.setInt(6, cpu.getProcess());
                    ps.setInt(7, cpu.getClock());
                    ps.setFloat(8, cpu.getMulti());
                    ps.setInt(9, cpu.getCacheL1());
                    ps.setInt(10, cpu.getCacheL2());
                    ps.setInt(11, cpu.getCacheL3());
                    ps.setInt(12, cpu.getTDP());
                    String _date= Formatting.DateFormat(cpu.getReleased());
                    if(_date==null)
                    {
                        ps.setNull(13, Types.DATE);
                    }
                    else{
                        ps.setString(13, _date);
                    }
                    ps.addBatch();
                    //防止内存溢出，我也不是很清楚都这么写
                    if ((i + 1) % 1000 == 0) {
                        ps.executeBatch();
                        ps.clearBatch();
                    }
                    i++;
                }
            }
            ps.executeBatch(); // 批量执行
            conn.commit();// 提交事务
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void TransitionStart(){
        //数据持久化
        try {
            conn= DBUtil.getConnection();
            String sql="select Name,Codename,Cores,Threads,Socket,Process,Clock,Multi,CacheL1,CacheL2,CacheL3,TDP,Released from cpu";
            ps=conn.prepareStatement(sql);
            // JAVA默认为TRUE,我们自己处理需要设置为FALSE,并且修改为手动提交,才可以调用rollback()函数
            rs=ps.executeQuery();
            MongoClient client = LocalMDBUtil.createMongoDBClient();
            // 取得Collecton句柄
            MongoDatabase database = client.getDatabase("building");
            MongoCollection<org.bson.Document> collection = database.getCollection("cpu");
            while(rs.next())
            {
                org.bson.Document doc = new org.bson.Document();
                doc.append("Name",rs.getString(1));
                doc.append("Codename",rs.getString(2));
                doc.append("Cores",rs.getInt(3));
                doc.append("Threads",rs.getInt(4));
                String[] tmp=StringUtils.split(rs.getString(5)," ");
                if(tmp.length>1&&tmp[0].equals("Socket"))
                {
                    doc.append("Socket", tmp[1]);
                }
                else{
                    doc.append("Socket", rs.getString(5));
                }
                doc.append("Process",rs.getInt(6));
                doc.append("Clock",rs.getInt(7));
                doc.append("Multi",rs.getFloat(8));
                doc.append("CacheL1",rs.getInt(9));
                doc.append("CacheL2",rs.getInt(10));
                doc.append("CacheL3",rs.getInt(11));
                doc.append("TDP",rs.getInt(12));
                doc.append("Released", rs.getString(13));
                System.out.println(doc.toString());
                collection.insertOne(doc);
            }
            client.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
