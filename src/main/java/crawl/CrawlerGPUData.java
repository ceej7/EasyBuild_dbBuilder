package crawl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
import units.CPUUnit;
import units.GPUUnit;
import util.DBUtil;
import util.Formatting;
import util.LocalMDBUtil;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Vector;

/**
 * 从网站爬CPU参数存入mysql
 */
public class CrawlerGPUData {
    public static void main(String[] args) {
        //        new CrawlerGPUData().Start();
        new CrawlerGPUData().TransitionStart();
    }

    static  final String BASE_URL1="https://www.techpowerup.com/gpudb/?mfgr%5B%5D=amd&mfgr%5B%5D=ati&mfgr%5B%5D=intel&mfgr%5B%5D=matrox&mfgr%5B%5D=nvidia&mfgr%5B%5D=xgi&mobile=0&released%5B%5D=y14_c&released%5B%5D=y11_14&released%5B%5D=y08_11&released%5B%5D=y05_08&released%5B%5D=y00_05&generation=&chipname=&interface=&ushaders=&tmus=&rops=&memsize=&memtype=&buswidth=&slots=&powerplugs=&sort=released&q=";
    static  final String BASE_URL2="https://www.techpowerup.com/gpudb/?mfgr%5B%5D=amd&mfgr%5B%5D=ati&mfgr%5B%5D=intel&mfgr%5B%5D=matrox&mfgr%5B%5D=nvidia&mfgr%5B%5D=xgi&mobile=0&workstation=1&released%5B%5D=y14_c&released%5B%5D=y11_14&released%5B%5D=y08_11&released%5B%5D=y05_08&released%5B%5D=y00_05&generation=&chipname=&interface=&ushaders=&tmus=&rops=&memsize=&memtype=&buswidth=&slots=&powerplugs=&sort=released&q=";
    HashMap<String,Vector<GPUUnit>> gpus;
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;
    //String file;

    /**
     * Constructor
     */
    public CrawlerGPUData(/*String file*/){
        gpus=new HashMap<String,Vector<GPUUnit>>();
        //this.file=file;
    }

    /**
     * The process of crawling Data
     * @param url
     * @throws Exception
     */
    public void getData(String url)throws Exception
    {
        String content=doGet(url);
        Document root=Jsoup.parse(content);
        Elements tbodies=root.select(("#list table tbody"));
        for(Element tbody:tbodies)
        {
            Elements trs=tbody.children();
            for(Element tr:trs)
            {
                GPUUnit gpu=new GPUUnit(tr.child(0).text(),tr.child(1).text(),tr.child(2).text(),tr.child(3).text(),tr.child(4).text(),tr.child(5).text(),tr.child(6).text(),tr.child(7).text());
                //System.out.println(gpu.toString());
                Vector<GPUUnit>_gpus= gpus.get(gpu.getName());
                if(_gpus!=null)
                {
                    boolean flg=false;
                    for(GPUUnit gpu_test:_gpus)
                    {
                        if(gpu_test.getName().equals(gpu.getName())&&gpu_test.getChip().equals(gpu.getChip()))
                        {
                            System.out.println(gpu.toString()+"----Conflict");
                            flg=true;
                            break;
                        }
                    }
                    if(!flg)
                    _gpus.add(gpu);
                }
                else{
                    _gpus=new Vector<GPUUnit>();
                    _gpus.add(gpu);
                }
                gpus.put(gpu.getName(),_gpus);
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
        try {
            getData(BASE_URL1);
            getData(BASE_URL2);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            PrintStream ps = new PrintStream(file);
//            System.setOut(ps);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        for (Vector<GPUUnit> _gpus:gpus.values()
//                ) {
//            for(GPUUnit gpu:_gpus) {
//                System.out.println(gpu.toString());
//            }
//        }
//        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        try{
            conn= DBUtil.getConnection();
            String sql="insert into gpu(Name,Chip,Released,Bus,Memory_Size,Memory_Type,Memory_Bus,GPU_Clock,M_Clock,Shaders,TMUs,ROPs,Multiplier) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            // JAVA默认为TRUE,我们自己处理需要设置为FALSE,并且修改为手动提交,才可以调用rollback()函数
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            int i=0;
            for (Vector<GPUUnit> _gpus:gpus.values()
                    ) {
                for(GPUUnit gpu:_gpus) {
                    ps.setString(1, gpu.getName());
                    ps.setString(2, gpu.getChip());
                    ps.setString(3, Formatting.DateFormat(gpu.getReleased()));
                    ps.setString(4, gpu.getBus());
                    ps.setInt(5, gpu.getMemory_Size());
                    ps.setString(6, gpu.getMemory_Type());
                    ps.setInt(7, gpu.getMemory_Bus());
                    ps.setInt(8, gpu.getGPU_Clock());
                    ps.setInt(9, gpu.getM_Clock());
                    ps.setInt(10, gpu.getShaders());
                    ps.setInt(11, gpu.getTMUs());
                    ps.setInt(12, gpu.getROPs());
                    ps.setInt(13, gpu.getMultiplier());
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void TransitionStart(){
        //数据持久化
        try {
            conn= DBUtil.getConnection();
            String sql="select Name,Chip,Released,Bus,Memory_Size,Memory_Type,Memory_Bus,GPU_Clock,M_Clock,Shaders,TMUs,ROPs,Multiplier from gpu";
            ps=conn.prepareStatement(sql);
            // JAVA默认为TRUE,我们自己处理需要设置为FALSE,并且修改为手动提交,才可以调用rollback()函数
            rs=ps.executeQuery();
            MongoClient client = LocalMDBUtil.createMongoDBClient();
            // 取得Collecton句柄
            MongoDatabase database = client.getDatabase("building");
            MongoCollection<org.bson.Document> collection = database.getCollection("gpu");
            while(rs.next())
            {
                org.bson.Document doc = new org.bson.Document();
                doc.append("Name",rs.getString(1));
                doc.append("Chip",rs.getString(2));
                doc.append("Released",rs.getString(3));
                doc.append("Bus",rs.getString(4));
                doc.append("Memory_Size", rs.getInt(5));
                doc.append("Memory_Type",rs.getString(6));
                doc.append("Memory_Bus",rs.getInt(7));
                doc.append("GPU_Clock",rs.getInt(8));
                doc.append("Shaders",rs.getInt(9));
                doc.append("TMUs",rs.getInt(10));
                doc.append("ROPs",rs.getInt(11));
                doc.append("TDP",rs.getInt(12));
                doc.append("Multiplier", rs.getInt(13));
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
