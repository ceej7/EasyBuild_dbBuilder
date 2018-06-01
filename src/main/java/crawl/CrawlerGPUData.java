package crawl;

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

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;

/**
 * This Demo is used for Crawl Data of all moble phones in JD
 */
public class CrawlerGPUData {

    public void Start(){
        try {
            getData(BASE_URL1);
            getData(BASE_URL2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            PrintStream ps = new PrintStream(file);
            System.setOut(ps);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (Vector<GPUUnit> _gpus:gpus.values()
                ) {
            for(GPUUnit gpu:_gpus) {
                System.out.println(gpu.toString());
            }
        }
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

    }

     static  final String BASE_URL1="https://www.techpowerup.com/gpudb/?mfgr%5B%5D=amd&mfgr%5B%5D=ati&mfgr%5B%5D=intel&mfgr%5B%5D=matrox&mfgr%5B%5D=nvidia&mfgr%5B%5D=xgi&mobile=0&released%5B%5D=y14_c&released%5B%5D=y11_14&released%5B%5D=y08_11&released%5B%5D=y05_08&released%5B%5D=y00_05&generation=&chipname=&interface=&ushaders=&tmus=&rops=&memsize=&memtype=&buswidth=&slots=&powerplugs=&sort=released&q=";
    static  final String BASE_URL2="https://www.techpowerup.com/gpudb/?mfgr%5B%5D=amd&mfgr%5B%5D=ati&mfgr%5B%5D=intel&mfgr%5B%5D=matrox&mfgr%5B%5D=nvidia&mfgr%5B%5D=xgi&mobile=0&workstation=1&released%5B%5D=y14_c&released%5B%5D=y11_14&released%5B%5D=y08_11&released%5B%5D=y05_08&released%5B%5D=y00_05&generation=&chipname=&interface=&ushaders=&tmus=&rops=&memsize=&memtype=&buswidth=&slots=&powerplugs=&sort=released&q=";
     HashMap<String,Vector<GPUUnit>> gpus;
    String file;

    /**
     * Constructor
     */
    public CrawlerGPUData(String file){
        gpus=new HashMap<String,Vector<GPUUnit>>();
        this.file=file;

    }

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
                System.out.println(gpu.toString());
                Vector<GPUUnit>_gpus= gpus.get(gpu.Name);
                if(_gpus!=null)
                {
                    _gpus.add(gpu);

                }
                else{
                    _gpus=new Vector<GPUUnit>();
                    _gpus.add(gpu);
                }
                gpus.put(gpu.Name,_gpus);
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
