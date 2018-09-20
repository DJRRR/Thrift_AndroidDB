package cn.fudan.androiddb.crawler;

import cn.fudan.androiddb.crawler.crawlerEntity.CrawlerTaskInfo;
import cn.fudan.androiddb.crawler.dao.CrawlerTaskInfoDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qiaoying
 * @date 2018/9/16 12:38
 */
public class WorkerRunnerProcess {

    public static class CrawlerWorkerRunnerArgs{
        public int taskId;
        public String market;
        public String scope;

        private static String searchForMainJarPath(){
            String currentDir = System.getProperty("user.dir");
            String jarFileName = "LibDB.jar";
            File jarFile = new File(currentDir, jarFileName);
            if (jarFile.exists()){
                return jarFile.getAbsolutePath();
            } else {
                jarFile = new File(currentDir+File.pathSeparator+"lib",jarFileName);
                if (jarFile.exists()){
                    return jarFile.getAbsolutePath();
                } else {
                    return System.getProperty("java.class.path");
                }
            }
        }


        public List<String> composeProcessStartArgs(){
            List<String> args = new ArrayList<>();
            args.add("java");
            args.add("-Xmx4G");
            args.add("-cp");
            args.add(searchForMainJarPath());
            args.add(WorkerRunnerProcess.class.getCanonicalName());
            args.add("--task");
            args.add(""+taskId);


            return args;
        }

        public static CrawlerWorkerRunnerArgs parseFromArgs(String[] args) {
            try {
                CrawlerWorkerRunnerArgs workerRunnerArgs = new CrawlerWorkerRunnerArgs();
                int i = 0;
                while (i < args.length) {
                    if (args[i].equals("--task"))
                        workerRunnerArgs.taskId = Integer.parseInt(args[i + 1]);

                    i += 2;
                }

                return workerRunnerArgs;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }


    }

    public static void main(String[] args){
        CrawlerWorkerRunnerArgs workerRunnerArgs = CrawlerWorkerRunnerArgs.parseFromArgs(args);

        if (workerRunnerArgs == null){
            System.err.println("wrong cmd options: ");
            for (String arg : args)
                System.err.println("\t"+arg);
            System.exit(-1);
        }
        CrawlerTaskInfo crawlerTaskInfo = CrawlerTaskInfoDao.getCrawlerTaskInfo(workerRunnerArgs.taskId);
        if (crawlerTaskInfo == null) {
            System.out.println("crawler task not exist, taskId: " + workerRunnerArgs.taskId);
            System.exit(-1);
        }
        Crawler crawler = CrawlerFactory.getCrawler(crawlerTaskInfo.market);
        switch (crawlerTaskInfo.scope){
            case "top":
                crawler.crawlerTopAppList(crawlerTaskInfo);
                break;
            case "all":
                crawler.crawlerTopAppList(crawlerTaskInfo);
                break;
        }

    }

}
