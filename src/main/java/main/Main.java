package main;

import crawl.CrawlerCPUData;
import crawl.CrawlerDemo;
import crawl.CrawlerGPUData;

public class Main {
    public static void main(String[] args) {
        //new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,678&page={page}").StartCrawling();//cpu
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,681&page={page}").StartCrawling();//主板
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,679&page={page}").StartCrawling();//gpu
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,683&page={page}").StartCrawling();//hdd
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,11303&page={page}").StartCrawling();//ssd
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,680&page={page}").StartCrawling();//memory
//        new CrawlerCPUData().Start();
//        new CrawlerGPUData().Start();
    }
}
