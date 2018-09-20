package cn.fudan.androiddb.crawler.task;

import cn.fudan.androiddb.client.AndoidDBArgs;
import cn.fudan.androiddb.crawler.crawlerEntity.CrawlerTaskInfo;
import cn.fudan.androiddb.crawler.dao.AppInfoDao;
import cn.fudan.androiddb.crawler.dao.CrawlerTaskInfoDao;
import cn.fudan.androiddb.crawler.util.DateHelper;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author qiaoying
 * @date 2018/9/17 14:36
 */
public class TaskManager {

    private static String createTable(String tableName){
        AppInfoDao appInfoDao = new AppInfoDao();
        try {
            appInfoDao.createTable(tableName);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String createTableName(String market,String scope){
        String dateStr = DateHelper.getCurrentDayStr();
        String tableName = "apps_"+market+"_"+dateStr+"_"+scope;
        createTable(tableName);
        return tableName;
    }


    public static int  createCrawlerTask(AndoidDBArgs libDBArgs){
        String creator = libDBArgs.getCreator();
        String market = libDBArgs.getMarket();
        String scope = libDBArgs.getScope();
        CrawlerTaskInfo crawlerTaskInfo = new CrawlerTaskInfo();
        crawlerTaskInfo.taskCreator = creator;
        crawlerTaskInfo.market = market;
        crawlerTaskInfo.createDate = new Date();
        crawlerTaskInfo.scope = scope;
        crawlerTaskInfo.tableName = createTableName(market,scope);
        CrawlerTaskInfoDao.addCrawlerTaskInfo(crawlerTaskInfo);
        int taskId = CrawlerTaskInfoDao.getTaskId(crawlerTaskInfo.tableName);
        return taskId;
    }

}
