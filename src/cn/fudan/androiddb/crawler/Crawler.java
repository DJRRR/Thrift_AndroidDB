package cn.fudan.libdb.crawler;

import cn.fudan.libdb.crawler.crawlerEntity.CrawlerTaskInfo;

/**
 * @author qiaoying
 * @date 2018/9/16 14:16
 */
public abstract class Crawler {

    public abstract void crawlerTopAppList(CrawlerTaskInfo crawlerTaskInfo);

    public abstract void crawlerAllAppList(CrawlerTaskInfo crawlerTaskInfo);

}
