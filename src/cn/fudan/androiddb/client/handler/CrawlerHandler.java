package cn.fudan.androiddb.client.handler;

import cn.fudan.androiddb.client.AndoidDBArgs;
import cn.fudan.androiddb.client.AndroidDBServiceClient;
import cn.fudan.androiddb.crawler.task.TaskManager;
import org.apache.thrift.TException;

/**
 * @author Dai Jiarun
 * @date 2018/9/18
 */
public class CrawlerHandler {

    public static void crawlerHandler(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client) throws TException {
        int taskId = TaskManager.createCrawlerTask(andoidDBArgs);
        client.startCrawler(taskId);
    }
}
