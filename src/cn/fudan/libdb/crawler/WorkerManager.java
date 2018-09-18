package cn.fudan.libdb.crawler;


import cn.fudan.libdb.crawler.crawlerEntity.CrawlerTaskInfo;
import cn.fudan.libdb.crawler.dao.CrawlerTaskInfoDao;
import cn.fudan.libdb.crawler.util.CrawlerServiceConstants;
import cn.fudan.libdb.crawler.util.DateHelper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author qiaoying
 * @date 2018/9/15 22:37
 */
public class WorkerManager {

    public static class CrawlerWorker{
        public Date startDate;
        public Process runner;
        public int taskId;


        public CrawlerWorker(int taskId){
            this.taskId = taskId;
        }

        /*
        * 根据参数状态确定爬虫的任务
        *
         */

        private WorkerRunnerProcess.CrawlerWorkerRunnerArgs createStartProcessArgs(){
            CrawlerTaskInfo crawlerTaskInfo = CrawlerTaskInfoDao.getCrawlerTaskInfo(taskId);
            if (crawlerTaskInfo == null){
                System.out.println("crawlerTaskInfo为空");
                return null;
            }
            return createCrawlerArgs();
        }

        private WorkerRunnerProcess.CrawlerWorkerRunnerArgs createCrawlerArgs(){
            WorkerRunnerProcess.CrawlerWorkerRunnerArgs args = new WorkerRunnerProcess.CrawlerWorkerRunnerArgs();
            args.taskId = taskId;
            return args;
        }

        public int startProcess(){
            WorkerRunnerProcess.CrawlerWorkerRunnerArgs workerRunnerArgs = createStartProcessArgs();

            if (workerRunnerArgs == null){
                return -1;
            }
            return startProcess(workerRunnerArgs);
        }


        private int startProcess(WorkerRunnerProcess.CrawlerWorkerRunnerArgs workerRunnerArgs){

            List<String> cmd = workerRunnerArgs.composeProcessStartArgs();
            ProcessBuilder pb = new ProcessBuilder(cmd);
            String logTag = workerRunnerArgs.market+"_"+workerRunnerArgs.scope+"_"+ DateHelper.getCurrentTimeStr();

            pb.directory(new File(System.getProperty("user.dir")));
            File logDir = new File("./worker_logs");
            if (! logDir.exists()) {
                logDir.mkdirs();
            }
            File outputFile = new File("./worker_logs/out_"+logTag);
            File errorFile = new File("./worker_logs/err_"+logTag);

            try {
                if (! outputFile.exists())
                    outputFile.createNewFile();
                if (! errorFile.exists())
                    errorFile.createNewFile();

                pb.redirectOutput(outputFile);
                pb.redirectError(errorFile);

                startDate = new Date();
                runner = pb.start();

                return 0;
            } catch (IOException e) {
                System.err.println(outputFile.getAbsolutePath());
                e.printStackTrace();
                return -1;
            }
        }

        public int endProcess() {
            if (runner != null) {
                try {
                    runner.destroy();
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                    return -1;
                }
            }
            return 0;
        }

        public boolean isRunning() {return runner != null && runner.isAlive();}
    }


    public Map<Integer, CrawlerWorker> allWorkers = new HashMap<>();



    private WorkerManager(){}

    private static WorkerManager DEFAULT_WORK_MANGER = null;
    public static synchronized WorkerManager instantiate() {
        if (DEFAULT_WORK_MANGER == null) {
            DEFAULT_WORK_MANGER = new WorkerManager();

            return DEFAULT_WORK_MANGER;
        }
        else
            return DEFAULT_WORK_MANGER;
    }

    public synchronized int startWorkerForTask(int taskId) {
        System.out.println("startWorkerForTask()的taskId:"+taskId);
        CrawlerWorker worker;
        if (allWorkers.containsKey(taskId)){
            worker = allWorkers.get(taskId);
            if (worker.runner.isAlive()){
                return CrawlerServiceConstants.ERROR_CODE_CRAWLER_SERVICE_TASK_ALREADY_STARTED;
            }
        }else {
            worker = new CrawlerWorker(taskId);
        }


        if (worker.startProcess() > 0)
            return CrawlerServiceConstants.ERROR_CODE_CRAWLER_SERVICE_SUCCESS;
        else
            return CrawlerServiceConstants.ERROR_CODE_CRAWLER_SERVICE_FAILED;

    }



//    public synchronized int stopWorkerForTask(int taskId) {
//        CrawlerWorker worker;
//        if (allWorkers.containsKey(taskId)) {
//            worker = allWorkers.get(taskId);
//            if (worker.isRunning()) {
//                if (worker.endProcess() > 0)
//                    return CrawlerServiceConstants.ERROR_CODE_CRAWLER_SERVICE_SUCCESS;
//                else
//                    return CrawlerServiceConstants.ERROR_CODE_CRAWLER_SERVICE_FAILED;
//            }
//            return CrawlerServiceConstants.ERROR_CODE_CRAWLER_SERVICE_SUCCESS;
//        }
//        else
//            return CrawlerServiceConstants.ERROR_CODE_CRAWLER_SERVICE_TASK_NOT_EXIST;
//    }

}
