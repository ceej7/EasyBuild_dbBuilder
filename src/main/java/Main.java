import crawl.CrawlerCPUData;
import crawl.CrawlerDemo;
import crawl.CrawlerGPUData;

public class Main {
    public static void main(String[] args) {
//        new CrawlerDemo("https://list.jd.com/list.html?cat=9987,653,655&page={page}","F:\\CPUlog.txt").StartCrawling();
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,681&page={page}","F:\\MBlog.txt").StartCrawling();
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,679&page={page}","F:\\CPUlog.txt").StartCrawling();
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,683&page={page}","F:\\HDDlog.txt").StartCrawling();
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,11303&page={page}","F:\\SSDlog.txt").StartCrawling();
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,680&page={page}","F:\\Memorylog.txt").StartCrawling();
        //new CrawlerCPUData("C:\\Users\\ceej_\\Desktop\\CPUDATA.txt").Start();
            new CrawlerGPUData("C:\\Users\\ceej_\\Desktop\\GPUDATA.txt").Start();
    }
}
