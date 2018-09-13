package cn.fudan.libdb.client;

import cn.fudan.libdb.util.FileHandle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Dai Jiarun
 * @date 2018/9/13
 */
public abstract class FileRepo {
    private List<String> fileHashList;
    private Queue<String> fileToCache = null;
    private FetcherThreadPool fetcherThreadPool = null;

    public FileRepo() {}

    public List<String> getFileHashList(){return fileHashList;}

    public int getFileHashListSize(){
        return fileHashList.size();
    }

    protected List<String> loadHashListFromFile(String fileHashFile) {
        ArrayList<String> hashList = new ArrayList<>();
        try {
            BufferedReader input = new BufferedReader(new FileReader(fileHashFile));
            String line;
            line = input.readLine();
            while(line != null) {
                hashList.add(line);
                line = input.readLine();
            }
            input.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return hashList;
    }

    protected void init(String repoType, List<String> fileHashList, int prefetchCount) {
        this.fileHashList = fileHashList;
        this.fileToCache = new LinkedList<String>();
        this.fetcherThreadPool = new FetcherThreadPool(repoType);

        for (String apkHash : fileHashList)
            fileToCache.add(apkHash);

        fetcherThreadPool.start(prefetchCount);

        while(prefetchCount > 0) {
            String apkHash = fileToCache.poll();
            if (apkHash == null)
                return;
            fetcherThreadPool.assignJob(apkHash);
            prefetchCount --;
        }
    }

    public void exit() {
        fetcherThreadPool.exit();
        fileToCache.clear();
    }

    public abstract FileHandle getFile(String fileHash);

    public abstract boolean hashValCheck(String fileHash);

    public FileHandle syncGetFile(String fileHash) {
        if(!hashValCheck(fileHash)){
            return null;
        }
        if (fetcherThreadPool.containsJob(fileHash)) {
            while (! fetcherThreadPool.jobFinished(fileHash)) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }
            byte[] result = fetcherThreadPool.getAndRemoveResultIfPresent(fileHash);

            String newFileHash = fileToCache.poll();
            if (newFileHash != null)
                fetcherThreadPool.assignJob(newFileHash);

            if (result == null)
                return null;
            else
                return new FileHandle(result);
        }
        else {
            if (fileToCache.contains(fileHash))
                fileToCache.remove(fileHash);
            return getFile(fileHash);
        }
    }

    public void asyncGetFile(String apkHash, FileProcessor processor) {
        new Thread(() -> {
            FileHandle fileHandle = syncGetFile(apkHash);
            processor.process(fileHandle);
        }).start();
    }
}

