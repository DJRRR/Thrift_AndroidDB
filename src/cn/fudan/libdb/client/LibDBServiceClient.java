package cn.fudan.libdb.client;

import cn.fudan.libdb.LibDBConfig;
import cn.fudan.libdb.thrift.FileInfo;
import cn.fudan.libdb.thrift.LibDBService;
import cn.fudan.libdb.util.FileHandle;
import cn.fudan.libdb.util.FileUtil;
import com.beust.jcommander.JCommander;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

import static cn.fudan.libdb.client.LibDBArgs.getDirFromArgs;
import static cn.fudan.libdb.client.LibDBArgs.libQueryCheck;
import static cn.fudan.libdb.client.LibDBArgs.repoTypeCheck;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class LibDBServiceClient implements LibDBService.Iface{
    private static Set<LibDBService.Client> clientPool = new HashSet<>();
    public static int THREAD_COUNT = 20;
    private static LibDBService.Client createClient(){
        try{
            String serverIP = LibDBConfig.getConfig(LibDBConfig.PROP_KEY_CLIENT_BIND_IP);
            String serverPort = LibDBConfig.getConfig(LibDBConfig.PROP_KEY_SERVER_BIND_PORT);
            TTransport transport = new TSocket(serverIP, Integer.parseInt(serverPort));
            TProtocol tprotocol = new TBinaryProtocol(transport);
            transport.open();
            return new LibDBService.Client(tprotocol);
        } catch (TTransportException e){
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isClientOpen(LibDBService.Client client) {
        if (! client.getOutputProtocol().getTransport().isOpen() ||
                ! client.getInputProtocol().getTransport().isOpen())
            return false;
        return true;
    }

    public static LibDBServiceClient defaultClient() {
        return new LibDBServiceClient();
    }


    private static LibDBService.Client getAvailableClient() throws TException {
        synchronized (clientPool) {
            if (clientPool.size() == 0) {
                LibDBService.Client client = createClient();
                return client;
            } else {
                Iterator<LibDBService.Client> clientIterator = clientPool.iterator();
                LibDBService.Client client = null;
                while (clientIterator.hasNext()) {
                    client = clientIterator.next();
                    if (isClientOpen(client))
                        break;
                    else {
                        try {
                            client.getOutputProtocol().getTransport().close();
                            client.getInputProtocol().getTransport().close();
                        }
                        catch (Exception ex){}
                        clientIterator.remove();
                    }
                }

                if (client == null) {
                    return createClient();
                }
                else {
                    clientPool.remove(client);
                    return client;
                }
            }
        }
    }


    @Override
    public int ping(int test) throws org.apache.thrift.TException{
        LibDBService.Client client = getAvailableClient();
        int result = client.ping(test);
        synchronized (clientPool) {
            clientPool.add(client);
        }
        return result;
    }


    @Override
    public java.lang.String queryLibsByGAV(java.lang.String groupName, java.lang.String artifactId, java.lang.String version, java.lang.String repoType, boolean jsonOutput, int limit) throws org.apache.thrift.TException{
        LibDBService.Client client = getAvailableClient();
        String result = client.queryLibsByGAV(groupName,artifactId,version,repoType,jsonOutput,limit);
        synchronized (clientPool){
            clientPool.add(client);
        }
        return result;
    }

    @Override
    public FileInfo fetchLibByHash(java.lang.String hash) throws org.apache.thrift.TException{
        LibDBService.Client client = getAvailableClient();
        FileInfo result = null;
        try {
            result = client.fetchLibByHash(hash);
        }catch (TApplicationException e){
            System.err.println("Fail to fetch " + hash);
        }
        synchronized (clientPool) {
            clientPool.add(client);
        }
        return result;
    }

    @Override
    public FileInfo fetchApkByHash(java.lang.String hash) throws org.apache.thrift.TException{
        LibDBService.Client client = getAvailableClient();
        FileInfo result = null;
        try{
            result = client.fetchApkByHash(hash);
        }catch (TApplicationException e){
            System.err.println("Fail to fetch " + hash);
        }
        synchronized (clientPool){
            clientPool.add(client);
        }
        return result;
    }

    @Override
    public FileInfo fetchApkByName(java.lang.String packageName, java.lang.String versionCode) throws org.apache.thrift.TException{
        LibDBService.Client client = getAvailableClient();
        FileInfo result = null;
        try{
            result = client.fetchApkByName(packageName, versionCode);
        }catch (TApplicationException e){
            System.err.println("Fail to fetch " + packageName + "_" + versionCode);
        }
        synchronized (clientPool){
            clientPool.add(client);
        }
        return result;
    }

    @Override
    public FileInfo fetchApkSrcByHash(java.lang.String hash) throws org.apache.thrift.TException{
        LibDBService.Client client = getAvailableClient();
        FileInfo result = null;
        try{
            result = client.fetchApkSrcByHash(hash);
        }catch (TApplicationException e){
            System.err.println("Fail to fetch " + hash);
        }
        synchronized (clientPool){
            clientPool.add(client);
        }
        return result;
    }

    @Override
    public FileInfo fetchApkSrcByName(java.lang.String packageName, java.lang.String versionCode) throws org.apache.thrift.TException{
        LibDBService.Client client = getAvailableClient();
        FileInfo result = null;
        try{
            result = client.fetchApkSrcByName(packageName, versionCode);
        }catch (TApplicationException e){
            System.err.println("Fail to fetch " + packageName + "_" + versionCode);
        }
        synchronized (clientPool){
            clientPool.add(client);
        }
        return result;
    }



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

    // TODO: 2018/9/17 to collect all handlers separately

    public static void queryHandler(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        if(!(repoTypeCheck(libDBArgs) && libQueryCheck(libDBArgs))) {
            return;
        }
        if(libDBArgs.getRepoType().equals("apk") || libDBArgs.getRepoType().equals("apk-src")){
            throw new RuntimeException("Unsupported Functionality");
        }
        // TODO: 2018/9/17 the param "LIB_REPO" is not needed anymore
        String outputRes = client.queryLibsByGAV(libDBArgs.getGroupName(), libDBArgs.getArtifactId(), libDBArgs.getVersion(),
                "LIB_REPO", libDBArgs.isJsonOutput(), libDBArgs.getLimit());
        if(libDBArgs.outputPathUnset()){
            //command line print
            System.out.println(outputRes);
        }
        else{
            //save to file
            FileUtil.saveStrToFile(outputRes + "\n", libDBArgs.getOutputFilePath());
        }
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

    public static void multiFetchHandlerOfLib(LibDBArgs libDBArgs, LibDBServiceClient client)throws TException{
        String dirPath = getDirFromArgs(libDBArgs);
        if(dirPath == null){
            System.err.println("locate output dir failed");
            return;
        }
        String hashListFilePath = libDBArgs.getHashListFilePath();
        FileRepo fileRepo;
        fileRepo = new LibRepo(hashListFilePath,20);
        THREAD_COUNT = Math.min(fileRepo.getFileHashListSize(), THREAD_COUNT);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> hashList = fileRepo.getFileHashList();
        Queue<String> items = new LinkedBlockingDeque<>(hashList);
        for (int i = 0; i < THREAD_COUNT; i ++) {
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
                        THREAD_COUNT--;
                    }
                }
            }).start();
        }

        while (true) {
            synchronized (LibDBServiceClient.class) {
                if (THREAD_COUNT == 0)
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
        THREAD_COUNT = Math.min(fileRepo.getFileHashListSize(), THREAD_COUNT);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> hashList = fileRepo.getFileHashList();
        Queue<String> items = new LinkedBlockingDeque<>(hashList);
        for (int i = 0; i < THREAD_COUNT; i ++) {
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
                        THREAD_COUNT--;
                    }
                }
            }).start();
        }

        while (true) {
            synchronized (LibDBServiceClient.class) {
                if (THREAD_COUNT == 0)
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
        THREAD_COUNT = Math.min(fileRepo.getFileHashListSize(), THREAD_COUNT);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> hashList = fileRepo.getFileHashList();
        Queue<String> items = new LinkedBlockingDeque<>(hashList);
        for (int i = 0; i < THREAD_COUNT; i ++) {
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
                        THREAD_COUNT--;
                    }
                }
            }).start();
        }

        while (true) {
            synchronized (LibDBServiceClient.class) {
                if (THREAD_COUNT == 0)
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

    public static void testConnection(LibDBServiceClient client) throws TException{
        client.ping(1);
    }

    public static void main(String ... argv) throws TException{
        LibDBArgs libDBArgs = new LibDBArgs();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(libDBArgs)
                .build();
        jCommander.parse(argv);
        if(libDBArgs.isHelp()){
            jCommander.usage();
            return;
        }
        LibDBServiceClient client = LibDBServiceClient.defaultClient();
       //testConnection(client);
        if(libDBArgs.isQuery() && libDBArgs.isFetch()){
            System.out.println("Please specify the operation type(-q or -f)");
            System.err.println("-h for more information");
            return;
        }
        if(libDBArgs.isQuery()){
            queryHandler(libDBArgs, client);
        }
        else if(libDBArgs.isFetch()){
            fetchHandler(libDBArgs, client);
        }
        else{
            System.err.println("No specified operation, please set the operation type(-q or -f).");
            System.err.println("-h for more information");
        }
    }
}
