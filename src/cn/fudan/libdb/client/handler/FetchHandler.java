package cn.fudan.libdb.client.handler;

import cn.fudan.libdb.client.*;
import cn.fudan.libdb.thrift.FileInfo;
import cn.fudan.libdb.util.FileHandle;
import org.apache.thrift.TException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import static cn.fudan.libdb.client.LibDBArgs.getDirFromArgs;
import static cn.fudan.libdb.client.LibDBArgs.repoTypeCheck;
import static cn.fudan.libdb.util.Constants.THREAD_DEFAULT_NUM;

/**
 * @author Dai Jiarun
 * @date 2018/9/18
 */
public class FetchHandler {
    public static FileHandle getFileHandleOfLib(String hash, LibDBServiceClient client) throws org.apache.thrift.TException{
        FileInfo result = client.fetchLibByHash(hash);
        return new FileHandle(result.content.array(), result.suffix);
    }

    // TODO: 2018/9/17 to merge FileInfo & FileHandle
    public static FileHandle getFileHandleOfApk(String hash, LibDBServiceClient client) throws org.apache.thrift.TException{
        FileInfo result = client.fetchApkByHash(hash);
        return new FileHandle(result.content.array(), result.suffix);
    }

    public static FileHandle getFileHandleOfApk(String packageName, String versionCode, LibDBServiceClient client) throws org.apache.thrift.TException{
        FileInfo result = client.fetchApkByName(packageName, versionCode);
        return new FileHandle(result.content.array(), result.suffix);
    }

    public static FileHandle getFileHandleOfApkSrc(String hash, LibDBServiceClient client) throws org.apache.thrift.TException{
        FileInfo result = client.fetchApkSrcByHash(hash);
        return new FileHandle(result.content.array(), result.suffix);
    }

    public static FileHandle getFileHandleOfApkSrc(String packageName, String versioCode, LibDBServiceClient client) throws org.apache.thrift.TException{
        FileInfo result = client.fetchApkSrcByName(packageName, versioCode);
        return new FileHandle(result.content.array(), result.suffix);
    }


    public static void singleFetchHandler0fLib(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        String hashKey = libDBArgs.getHashKey();
        FileHandle fileHandle = getFileHandleOfLib(hashKey, client);
        if(fileHandle == null){
            System.err.println("Fetch failed");
            return;
        }
        String dirPath = getDirFromArgs(libDBArgs);
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

    public static void singleFetchHandlerOfApk(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        String fileName = null;
        FileHandle fileHandle = null;
        if(libDBArgs.hashKeyUnset() && (libDBArgs.versionUnset() || libDBArgs.packageNameUnset())){
            System.err.println("Please set -k or (-g and -v) to fetch apk");
            return;
        }
        else if(!libDBArgs.hashKeyUnset() && (!libDBArgs.versionUnset() || !libDBArgs.packageNameUnset())){
            System.err.println("Can only set -k or (-g and -v) to fetch apk");
            return;
        }
        else if(!libDBArgs.hashKeyUnset()){
            fileHandle = getFileHandleOfApk(libDBArgs.getHashKey(), client);
            fileName = libDBArgs.getHashKey();
        }
        else if(!libDBArgs.versionUnset() && !libDBArgs.packageNameUnset()){
            fileHandle = getFileHandleOfApk(libDBArgs.getPackageName(), libDBArgs.getVersion(), client);
            fileName = libDBArgs.getPackageName() + "_" + libDBArgs.getVersion();
        }
        else{
            System.err.println("Please set -k or (-g and -v) to fetch apk");
            return;
        }

        if(fileHandle == null){
            System.err.println("Fetch failed");
            return;
        }
        String dirPath = getDirFromArgs(libDBArgs);
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

    public static void singleFetchHandlerOfApkSrc(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        String fileName = null;
        FileHandle fileHandle = null;
        if(libDBArgs.hashKeyUnset() && (libDBArgs.versionUnset() || libDBArgs.packageNameUnset())){
            System.err.println("Please set -k or (-g and -v) to fetch apk");
            return;
        }
        else if(!libDBArgs.hashKeyUnset() && (!libDBArgs.versionUnset() || !libDBArgs.packageNameUnset())){
            System.err.println("Can only set -k or (-g and -v) to fetch apk");
            return;
        }
        else if(!libDBArgs.hashKeyUnset()){
            fileHandle = getFileHandleOfApkSrc(libDBArgs.getHashKey(), client);
            fileName = libDBArgs.getHashKey();
        }
        else if(!libDBArgs.versionUnset() && !libDBArgs.packageNameUnset()){
            fileHandle = getFileHandleOfApkSrc(libDBArgs.getPackageName(), libDBArgs.getVersion(), client);
            fileName = libDBArgs.getPackageName() + "_" + libDBArgs.getVersion();
        }
        else{
            System.err.println("Please set -k or (-g and -v) to fetch apk");
            return;
        }

        if(fileHandle == null){
            System.err.println("Fetch failed");
            return;
        }
        String dirPath = getDirFromArgs(libDBArgs);
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




    public static void singleFetchHandler(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        if(!repoTypeCheck(libDBArgs)){
            return;
        }
        if(libDBArgs.getRepoType().equals("lib")){
            singleFetchHandler0fLib(libDBArgs, client);
        }
        else if(libDBArgs.getRepoType().equals("apk")){
            singleFetchHandlerOfApk(libDBArgs, client);
        }
        else if(libDBArgs.getRepoType().equals("apk-src")){
            singleFetchHandlerOfApkSrc(libDBArgs, client);
        }
        else{
            throw new RuntimeException("Should not reach here in LibDBServiceClient.singleFetchHandler");
        }

    }


    public static void multiFetchHandlerOfLib(LibDBArgs libDBArgs, LibDBServiceClient client)throws TException {
        String dirPath = getDirFromArgs(libDBArgs);
        if(dirPath == null){
            System.err.println("locate output dir failed");
            return;
        }
        String hashListFilePath = libDBArgs.getHashListFilePath();
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

                    synchronized (LibDBServiceClient.class) {
                        THREAD_DEFAULT_NUM--;
                    }
                }
            }).start();
        }

        while (true) {
            synchronized (LibDBServiceClient.class) {
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

    public static void multiFetchHandlerOfApk(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        String dirPath = getDirFromArgs(libDBArgs);
        if(dirPath == null){
            System.err.println("locate output dir failed");
            return;
        }
        String hashListFilePath = libDBArgs.getHashListFilePath();
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

                    synchronized (LibDBServiceClient.class) {
                        THREAD_DEFAULT_NUM--;
                    }
                }
            }).start();
        }

        while (true) {
            synchronized (LibDBServiceClient.class) {
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

    public static void multiFetchHandlerOfApkSrc(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        String dirPath = getDirFromArgs(libDBArgs);
        if(dirPath == null){
            System.err.println("locate output dir failed");
            return;
        }
        String hashListFilePath = libDBArgs.getHashListFilePath();
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

                    synchronized (LibDBServiceClient.class) {
                        THREAD_DEFAULT_NUM--;
                    }
                }
            }).start();
        }

        while (true) {
            synchronized (LibDBServiceClient.class) {
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

    public static void multiFetchHandler(LibDBArgs libDBArgs, LibDBServiceClient client)throws TException{
        if(!repoTypeCheck(libDBArgs)){
            return;
        }
        if(libDBArgs.getRepoType().equals("lib")){
            multiFetchHandlerOfLib(libDBArgs, client);
        }
        else if(libDBArgs.getRepoType().equals("apk")){
            multiFetchHandlerOfApk(libDBArgs, client);
        }
        else if(libDBArgs.getRepoType().equals("apk-src")){
            multiFetchHandlerOfApkSrc(libDBArgs, client);
        }
        else{
            throw new RuntimeException("Should not reach here in LibDBServiceClient.multiFetchHandler");
        }
    }

    public static void fetchHandler(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        if(!libDBArgs.hashKeyUnset() && !libDBArgs.hashListFilePathUnset() && (!libDBArgs.versionUnset() || !libDBArgs.packageNameUnset())){
            System.err.println("Please specify the operation type of fetch(-k, -hl, (-p,-v))");
            System.err.println("-h for more information");
            return;
        }
        if(!libDBArgs.hashKeyUnset() || (!libDBArgs.packageNameUnset() && !libDBArgs.versionUnset())){
            singleFetchHandler(libDBArgs, client);
        }
        else if (!libDBArgs.hashListFilePathUnset()){
            multiFetchHandler(libDBArgs, client);
        }
        else{
            System.err.println("No specified fetch operation, please set -k, -hl, (-p, -v)");
            System.err.println("-h for more information");
            return;
        }

    }
}
