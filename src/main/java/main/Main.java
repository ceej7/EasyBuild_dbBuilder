package main;

import DailyMaintance.ItemsUpdate;
import crawl.CrawlerCPUData;
import crawl.CrawlerDemo;
import crawl.CrawlerGPUData;

public class Main {
    public static void main(String[] args) {
        //更新item价格和comments的核心线程，每日调用即可
        new ItemsUpdate().StartCrawling();
    }
}
