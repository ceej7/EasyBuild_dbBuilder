package main;

import crawl.CrawlerCPUData;
import crawl.CrawlerDemo;
import crawl.CrawlerGPUData;

public class Main {
    public static void main(String[] args) {
//        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,678&page={page}&delivery=1").StartCrawling();//cpu
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,681&page={page}&delivery=1").StartCrawling();//主板
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,679&page={page}&delivery=1").StartCrawling();//gpu
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,683&page={page}&delivery=1").StartCrawling();//hdd
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,11303&page={page}&delivery=1").StartCrawling();//ssd
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,680&page={page}&delivery=1").StartCrawling();//memory
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,688&page={page}&delivery=1").StartCrawling();//显示器
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,687&page={page}&delivery=1").StartCrawling();//机箱
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,691&page={page}&delivery=1").StartCrawling();//电源
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,682&ev=3680_97402&page={page}&delivery=1").StartCrawling();//散热-水冷
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,682&ev=3680_97403&page={page}&delivery=1").StartCrawling();//散热-风冷
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,682&ev=3680_1062&page={page}&delivery=1").StartCrawling();//机箱风扇
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,684&page={page}&delivery=1").StartCrawling();//光驱
        new CrawlerDemo("https://list.jd.com/list.html?cat=670,677,5008&ev=878_43268&page={page}&delivery=1").StartCrawling();//声卡
        new CrawlerCPUData().Start();
        new CrawlerGPUData().Start();
    }
}
