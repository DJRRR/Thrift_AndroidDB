package cn.fudan.libdb.client;

import cn.fudan.libdb.thrift.FileInfo;
import cn.fudan.libdb.thrift.LibDBService;
import cn.fudan.libdb.util.Constants;
import cn.fudan.libdb.util.FileHandle;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dai Jiarun
 * @date 2018/9/13
 */

public class FetcherThreadPool {
    private String repoType;//jar dex
    private List<String> workerQueue = new ArrayList<>();
    private Object notifier = new Object();
    private boolean exitFlag = false;
    private Map<String, FileHandle> downloadedFile = new HashMap<>();

    private List<FetcherThread> downloaders = new ArrayList<FetcherThread>();

    public FetcherThreadPool(String repoType) {this.repoType = repoType;}

    public class FetcherThread extends Thread {
        private String repoType;
        public FetcherThread(String repoTyep){
            this.repoType = repoTyep;
        }
        @Override
        public void run() {
            while(! exitFlag) {
                String fileHash = null;
                synchronized (workerQueue) {
                    if (workerQueue.size() > 0) {
                        fileHash = workerQueue.remove(0);
                    }
                }
                if (fileHash != null) {
                    try {
                        FileInfo fileInfo = null;
                        if(this.repoType.equals(Constants.LIB_REPO)) {
                            fileInfo = LibDBServiceClient.defaultClient().fetchLibByHash(fileHash);
                        }
                        else if(this.repoType.equals(Constants.APK_REPO)){
                            if(fileHash.length() == 32) {
                                fileInfo = LibDBServiceClient.defaultClient().fetchApkByHash(fileHash);
                            }
                            else if(fileHash.contains("_")){
                                int splitIndex = fileHash.lastIndexOf("_");
                                String packageName = fileHash.substring(0, splitIndex);
                                String versionCode = fileHash.substring(splitIndex + 1, fileHash.length());
                                fileInfo = LibDBServiceClient.defaultClient().fetchApkByName(packageName, versionCode);
                            }
                        }
                        else if(this.repoType.equals(Constants.APK_SRC_REPO)){
                            if(fileHash.length() == 32) {
                                fileInfo = LibDBServiceClient.defaultClient().fetchApkSrcByHash(fileHash);
                            }
                            else if(fileHash.contains("_")){
                                int splitIndex = fileHash.lastIndexOf("_");
                                String packageName = fileHash.substring(0, splitIndex);
                                String versionCode = fileHash.substring(splitIndex + 1, fileHash.length());
                                fileInfo = LibDBServiceClient.defaultClient().fetchApkSrcByName(packageName, versionCode);
                            }
                        }
                        FileHandle fileHandle = new FileHandle(fileInfo.content.array(), fileInfo.getSuffix());
                        synchronized (downloadedFile) {
                            downloadedFile.put(fileHash, fileHandle);
                        }
                    } catch (TException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    try {
                        synchronized (notifier) {
                            notifier.wait(5, 0);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void start(int workerSize) {
        if (downloaders.size() > 0) {
            System.err.println("FetchThreadPool has already been started!");
            return;
        }

        for (int i = 0; i < workerSize; i ++) {
            downloaders.add(new FetcherThread(repoType));
        }
        for (int i = 0; i < workerSize; i ++) {
            downloaders.get(i).start();
        }
    }

    public void assignJob(String fileHash) {
        if (fileHash == null)
            return;
        synchronized (workerQueue) {
            workerQueue.add(fileHash);
        }
        synchronized (notifier) {
            notifier.notifyAll();
        }
    }

    public boolean containsJob(String fileHash) {
        return workerQueue.contains(fileHash);
    }

    public boolean jobFinished(String fileHash) {return downloadedFile.containsKey(fileHash);}

    public FileHandle getAndRemoveResultIfPresent(String fileHash) {
        FileHandle result = null;
        synchronized (downloadedFile) {
            if (downloadedFile.containsKey(fileHash)) {
                result = downloadedFile.get(fileHash);
                downloadedFile.remove(fileHash);
            }
        }
        return result;
    }

    public void exit() {
        exitFlag = true;

        while(! allWorkerExit()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean allWorkerExit() {
        for (int i = 0; i < downloaders.size(); i ++) {
            if (downloaders.get(i).isAlive())
                return false;
        }
        return true;
    }
}
