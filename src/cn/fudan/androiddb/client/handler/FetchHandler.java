package cn.fudan.androiddb.client.handler;

import cn.fudan.androiddb.client.*;
import cn.fudan.androiddb.thrift.FileInfo;
import cn.fudan.androiddb.util.FileHandle;
import org.apache.thrift.TException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import static cn.fudan.androiddb.client.AndoidDBArgs.getDirFromArgs;
import static cn.fudan.androiddb.client.AndoidDBArgs.repoTypeCheck;
import static cn.fudan.androiddb.util.Constants.THREAD_DEFAULT_NUM;

/**
 * @author Dai Jiarun
 * @date 2018/9/18
 */
public class FetchHandler {
    public static FileHandle getFileHandleOfLib(String hash, AndroidDBServiceClient client) throws org.apache.thrift.TException{
        FileInfo result = client.fetchLibByHash(hash);
        return new FileHandle(result.content.array(), result.suffix);
    }

    // TODO: 2018/9/17 to merge FileInfo & FileHandle
    public static FileHandle getFileHandleOfApk(String hash, AndroidDBServiceClient client) throws org.apache.thrift.TException{
        FileInfo result = client.fetchApkByHash(hash);
        return new FileHandle(result.content.array(), result.suffix);
    }

    public static FileHandle getFileHandleOfApk(String packageName, String versionCode, AndroidDBServiceClient client) throws org.apache.thrift.TException{
        FileInfo result = client.fetchApkByName(packageName, versionCode);
        return new FileHandle(result.content.array(), result.suffix);
    }

    public static FileHandle getFileHandleOfApkSrc(String hash, AndroidDBServiceClient client) throws org.apache.thrift.TException{
        FileInfo result = client.fetchApkSrcByHash(hash);
        return new FileHandle(result.content.array(), result.suffix);
    }

    public static FileHandle getFileHandleOfApkSrc(String packageName, String versioCode, AndroidDBServiceClient client) throws org.apache.thrift.TException{
        FileInfo result = client.fetchApkSrcByName(packageName, versioCode);
        return new FileHandle(result.content.array(), result.suffix);
    }


    public static void singleFetchHandler0fLib(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client) throws TException{
        String hashKey = andoidDBArgs.getHashKey();
        FileHandle fileHandle = getFileHandleOfLib(hashKey, client);
        if(fileHandle == null){
            System.err.println("Fetch failed");
            return;
        }
        String dirPath = getDirFromArgs(andoidDBArgs);
        if(dirPath == null){
            System.err.println("locate output dir failed");
            return;
        }
        if(hashKey.length() == 32 || hashKey.length() == 64){
            String filepath = dirPath + hashKey + fileHandle.getSuffix();
            try {
                fileHandle.writeFile(filepath);
                System.out.println("write " + fileHandle.getSuffix() + " to " + filepath);
            }catch (IOException e){
                e.printStackTrace();
                System.err.println("write " + fileHandle.getSuffix() + " to local failed");
            }
        }
        else{
            System.err.println("Error length or hash key(only support md5 and sha256)");
        }
    }

    public static void singleFetchHandlerOfApk(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client) throws TException{
        String fileName = null;
        FileHandle fileHandle = null;
        if(andoidDBArgs.hashKeyUnset() && (andoidDBArgs.versionUnset() || andoidDBArgs.packageNameUnset())){
            System.err.println("Please set -k or (-g and -v) to fetch apk");
            return;
        }
        else if(!andoidDBArgs.hashKeyUnset() && (!andoidDBArgs.versionUnset() || !andoidDBArgs.packageNameUnset())){
            System.err.println("Can only set -k or (-g and -v) to fetch apk");
            return;
        }
        else if(!andoidDBArgs.hashKeyUnset()){
            fileHandle = getFileHandleOfApk(andoidDBArgs.getHashKey(), client);
            fileName = andoidDBArgs.getHashKey();
        }
        else if(!andoidDBArgs.versionUnset() && !andoidDBArgs.packageNameUnset()){
            fileHandle = getFileHandleOfApk(andoidDBArgs.getPackageName(), andoidDBArgs.getVersion(), client);
            fileName = andoidDBArgs.getPackageName() + "_" + andoidDBArgs.getVersion();
        }
        else{
            System.err.println("Please set -k or (-g and -v) to fetch apk");
            return;
        }

        if(fileHandle == null){
            System.err.println("Fetch failed");
            return;
        }
        String dirPath = getDirFromArgs(andoidDBArgs);
        if(dirPath == null){
            System.err.println("locate output dir failed");
            return;
        }

        String filepath = dirPath + fileName + fileHandle.getSuffix();
        try {
            fileHandle.writeFile(filepath);
            System.out.println("write " + fileHandle.getSuffix() + " to " + filepath);
        }catch (IOException e){
            e.printStackTrace();
            System.err.println("write " + fileHandle.getSuffix() + " to local failed");
        }
    }

    public static void singleFetchHandlerOfApkSrc(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client) throws TException{
        String fileName = null;
        FileHandle fileHandle = null;
        if(andoidDBArgs.hashKeyUnset() && (andoidDBArgs.versionUnset() || andoidDBArgs.packageNameUnset())){
            System.err.println("Please set -k or (-g and -v) to fetch apk");
            return;
        }
        else if(!andoidDBArgs.hashKeyUnset() && (!andoidDBArgs.versionUnset() || !andoidDBArgs.packageNameUnset())){
            System.err.println("Can only set -k or (-g and -v) to fetch apk");
            return;
        }
        else if(!andoidDBArgs.hashKeyUnset()){
            fileHandle = getFileHandleOfApkSrc(andoidDBArgs.getHashKey(), client);
            fileName = andoidDBArgs.getHashKey();
        }
        else if(!andoidDBArgs.versionUnset() && !andoidDBArgs.packageNameUnset()){
            fileHandle = getFileHandleOfApkSrc(andoidDBArgs.getPackageName(), andoidDBArgs.getVersion(), client);
            fileName = andoidDBArgs.getPackageName() + "_" + andoidDBArgs.getVersion();
        }
        else{
            System.err.println("Please set -k or (-g and -v) to fetch apk");
            return;
        }

        if(fileHandle == null){
            System.err.println("Fetch failed");
            return;
        }
        String dirPath = getDirFromArgs(andoidDBArgs);
        if(dirPath == null){
            System.err.println("locate output dir failed");
            return;
        }

        String filepath = dirPath + fileName + fileHandle.getSuffix();
        try {
            fileHandle.writeFile(filepath);
            System.out.println("write " + fileHandle.getSuffix() + " to " + filepath);
        }catch (IOException e){
            e.printStackTrace();
            System.err.println("write " + fileHandle.getSuffix() + " to local failed");
        }
    }




    public static void singleFetchHandler(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client) throws TException{
        if(!repoTypeCheck(andoidDBArgs)){
            return;
        }
        if(andoidDBArgs.getRepoType().equals("lib")){
            singleFetchHandler0fLib(andoidDBArgs, client);
        }
        else if(andoidDBArgs.getRepoType().equals("apk")){
            singleFetchHandlerOfApk(andoidDBArgs, client);
        }
        else if(andoidDBArgs.getRepoType().equals("apk-src")){
            singleFetchHandlerOfApkSrc(andoidDBArgs, client);
        }
        else{
            throw new RuntimeException("Should not reach here in AndroidDBServiceClient.singleFetchHandler");
        }

    }


    public static void multiFetchHandlerOfLib(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client)throws TException {
        String dirPath = getDirFromArgs(andoidDBArgs);
        if(dirPath == null){
            System.err.println("locate output dir failed");
            return;
        }
        String hashListFilePath = andoidDBArgs.getHashListFilePath();
        FileRepo fileRepo;
        fileRepo = new LibRepo(hashListFilePath,20);
        THREAD_DEFAULT_NUM = Math.min(fileRepo.getFileHashListSize(), THREAD_DEFAULT_NUM);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> hashList = fileRepo.getFileHashList();
        Queue<String> items = new LinkedBlockingDeque<>(hashList);
        for (int i = 0; i < THREAD_DEFAULT_NUM; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (items.size() != 0) {
                        String hashVal = null;
                        synchronized (items) {
                            hashVal = items.poll();
                        }
                        if (hashVal != null) {
                            FileHandle fileHandle = fileRepo.syncGetFileHandle(hashVal);
                            if (fileHandle == null) {
                                System.out.println( hashVal + " no such file");
                                continue;
                            }
                            String actualHashVal = fileHandle.getContentHash();
                            System.out.println(dateFormat.format(new Date())+"\t" + hashVal + "\t" + actualHashVal
                                    +"\t" + hashVal.equals(actualHashVal));
                            if(hashVal.equals(actualHashVal)){
                                try {
                                    fileHandle.writeFile(dirPath + hashVal + fileHandle.getSuffix());
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        try {
                            Thread.sleep(10);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    synchronized (AndroidDBServiceClient.class) {
                        THREAD_DEFAULT_NUM--;
                    }
                }
            }).start();
        }

        while (true) {
            synchronized (AndroidDBServiceClient.class) {
                if (THREAD_DEFAULT_NUM == 0)
                    break;
            }
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        fileRepo.exit();
    }

    public static void multiFetchHandlerOfApk(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client) throws TException{
        String dirPath = getDirFromArgs(andoidDBArgs);
        if(dirPath == null){
            System.err.println("locate output dir failed");
            return;
        }
        String hashListFilePath = andoidDBArgs.getHashListFilePath();
        FileRepo fileRepo;
        fileRepo = new ApkRepo(hashListFilePath,20);
        THREAD_DEFAULT_NUM = Math.min(fileRepo.getFileHashListSize(), THREAD_DEFAULT_NUM);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> hashList = fileRepo.getFileHashList();
        Queue<String> items = new LinkedBlockingDeque<>(hashList);
        for (int i = 0; i < THREAD_DEFAULT_NUM; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (items.size() != 0) {
                        String hashVal = null;
                        synchronized (items) {
                            hashVal = items.poll();
                        }

                        if (hashVal != null) {
                            FileHandle fileHandle = fileRepo.syncGetFileHandle(hashVal);
                            if (fileHandle == null) {
                                System.out.println( hashVal + " no such file");
                                continue;
                            }
                            String actualHashVal = fileHandle.getContentHash();
                            System.out.println(dateFormat.format(new Date())+"\t" + hashVal + "\t" + actualHashVal
                                    +"\t" + hashVal.equals(actualHashVal));
                            try {
                                fileHandle.writeFile(dirPath + hashVal + fileHandle.getSuffix());
                            }catch (IOException e){
                                e.printStackTrace();
                            }

                        }
                        try {
                            Thread.sleep(10);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    synchronized (AndroidDBServiceClient.class) {
                        THREAD_DEFAULT_NUM--;
                    }
                }
            }).start();
        }

        while (true) {
            synchronized (AndroidDBServiceClient.class) {
                if (THREAD_DEFAULT_NUM == 0)
                    break;
            }
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        fileRepo.exit();
    }

    public static void multiFetchHandlerOfApkSrc(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client) throws TException{
        String dirPath = getDirFromArgs(andoidDBArgs);
        if(dirPath == null){
            System.err.println("locate output dir failed");
            return;
        }
        String hashListFilePath = andoidDBArgs.getHashListFilePath();
        FileRepo fileRepo;
        fileRepo = new ApkSrcRepo(hashListFilePath,20);
        THREAD_DEFAULT_NUM = Math.min(fileRepo.getFileHashListSize(), THREAD_DEFAULT_NUM);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> hashList = fileRepo.getFileHashList();
        Queue<String> items = new LinkedBlockingDeque<>(hashList);
        for (int i = 0; i < THREAD_DEFAULT_NUM; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (items.size() != 0) {
                        String hashVal = null;
                        synchronized (items) {
                            hashVal = items.poll();
                        }
                        if (hashVal != null) {
                            FileHandle fileHandle = fileRepo.syncGetFileHandle(hashVal);
                            if (fileHandle == null) {
                                System.out.println( hashVal + " no such file");
                                continue;
                            }
                            String actualHashVal = fileHandle.getContentHash();
                            System.out.println(dateFormat.format(new Date())+"\t" + hashVal + "\t" + actualHashVal
                                    +"\t" + hashVal.equals(actualHashVal));

                            try {
                                fileHandle.writeFile(dirPath + hashVal + fileHandle.getSuffix());
                            }catch (IOException e){
                                e.printStackTrace();
                            }

                        }
                        try {
                            Thread.sleep(10);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    synchronized (AndroidDBServiceClient.class) {
                        THREAD_DEFAULT_NUM--;
                    }
                }
            }).start();
        }

        while (true) {
            synchronized (AndroidDBServiceClient.class) {
                if (THREAD_DEFAULT_NUM == 0)
                    break;
            }
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        fileRepo.exit();
    }

    public static void multiFetchHandler(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client)throws TException{
        if(!repoTypeCheck(andoidDBArgs)){
            return;
        }
        if(andoidDBArgs.getRepoType().equals("lib")){
            multiFetchHandlerOfLib(andoidDBArgs, client);
        }
        else if(andoidDBArgs.getRepoType().equals("apk")){
            multiFetchHandlerOfApk(andoidDBArgs, client);
        }
        else if(andoidDBArgs.getRepoType().equals("apk-src")){
            multiFetchHandlerOfApkSrc(andoidDBArgs, client);
        }
        else{
            throw new RuntimeException("Should not reach here in AndroidDBServiceClient.multiFetchHandler");
        }
    }

    public static void fetchHandler(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client) throws TException{
        if(!andoidDBArgs.hashKeyUnset() && !andoidDBArgs.hashListFilePathUnset() && (!andoidDBArgs.versionUnset() || !andoidDBArgs.packageNameUnset())){
            System.err.println("Please specify the operation type of fetch(-k, -hl, (-p,-v))");
            System.err.println("-h for more information");
            return;
        }
        if(!andoidDBArgs.hashKeyUnset() || (!andoidDBArgs.packageNameUnset() && !andoidDBArgs.versionUnset())){
            singleFetchHandler(andoidDBArgs, client);
        }
        else if (!andoidDBArgs.hashListFilePathUnset()){
            multiFetchHandler(andoidDBArgs, client);
        }
        else{
            System.err.println("No specified fetch operation, please set -k, -hl, (-p, -v)");
            System.err.println("-h for more information");
            return;
        }

    }
}
