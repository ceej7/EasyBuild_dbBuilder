package main;

import crawl.CrawlerCPUData;
import crawl.CrawlerDemo;
import crawl.CrawlerGPUData;

public class Main {
    public static void main(String[] args) {
//        new CrawlerDemo("https://list.jd.com/list.html?cat=9987,653,655&page={page}","C:\\Users\\ceej_\\Desktop\\CPU.txt").StartCrawling();
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,681&page={page}","C:\\Users\\ceej_\\Desktop\\MB.txt").StartCrawling();
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,679&page={page}","C:\\Users\\ceej_\\Desktop\\GPU.txt").StartCrawling();
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,683&page={page}","C:\\Users\\ceej_\\Desktop\\HDD.txt").StartCrawling();
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,11303&page={page}","C:\\Users\\ceej_\\Desktop\\SSD.txt").StartCrawling();
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,680&page={page}","C:\\Users\\ceej_\\Desktop\\MEMORY.txt").StartCrawling();
        new CrawlerCPUData().Start();
//        new CrawlerGPUData("C:\\Users\\ceej_\\Desktop\\GPUDATA.txt").Start();
    }
}
