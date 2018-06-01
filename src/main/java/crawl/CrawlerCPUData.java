package crawl;

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

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;

/**
 * This Demo is used for Crawl Data of all moble phones in JD
 */
public class CrawlerCPUData {

    public void Start(){
        try {
        getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            PrintStream ps = new PrintStream(file);
            System.setOut(ps);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (CPUUnit cpu:cpus
                ) {
            System.out.println(cpu.toString());
        }
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

    }

     static  final String BASE_URL="https://www.techpowerup.com/cpudb/?mfgr%5B%5D=amd&mfgr%5B%5D=intel&class%5B%5D=desktop&class%5B%5D=server&released%5B%5D=y17_c&released%5B%5D=y14_17&released%5B%5D=y11_14&released%5B%5D=y08_11&released%5B%5D=y05_08&released%5B%5D=y00_05&logo=&nCores=&process=&socket=&codename=&multi=&sort=name&q=";
    Vector<CPUUnit> cpus;
    String file;

    /**
     * Constructor
     */
    public CrawlerCPUData(String file){
        cpus=new Vector<CPUUnit>();
        this.file=file;

    }

    public void getData()throws Exception
    {
        String content=doGet(BASE_URL);
        Document root=Jsoup.parse(content);
        Elements tbodies=root.select(("#list table tbody"));
        for(Element tbody:tbodies)
        {
            Elements trs=tbody.children();
            for(Element tr:trs)
            {

                CPUUnit cpu=new CPUUnit(tr.child(0).text(),tr.child(1).text(),tr.child(2).text(),tr.child(3).text(),tr.child(4).text(),tr.child(5).text(),tr.child(6).text(),tr.child(7).text(),tr.child(8).text(),tr.child(9).text());

                cpus.add(cpu);
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


}
