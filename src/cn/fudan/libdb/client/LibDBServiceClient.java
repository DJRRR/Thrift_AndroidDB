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
    public FileInfo fetch(java.lang.String hash) throws org.apache.thrift.TException{
        LibDBService.Client client = getAvailableClient();
        FileInfo result = null;
        try {
            result = client.fetch(hash);
        }catch (TApplicationException e){
            System.err.println("Fail to fetch " + hash);
        }
        synchronized (clientPool) {
            clientPool.add(client);
        }
        return result;
    }

    public static FileHandle getFileHandle(String hash, LibDBServiceClient client) throws org.apache.thrift.TException{
        //ByteBuffer result = client.fetch(hash);
        FileInfo result = client.fetch(hash);
        return new FileHandle(result.content.array(), result.suffix);
    }

    public static void queryHandler(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        if(!repoTypeCheck(libDBArgs)){
            return;
        }
        if(libDBArgs.groupUnset() && libDBArgs.artifactUnset() && libDBArgs.versionUnset()){
            System.out.println("Please set -g or -a or -v for a query!");
            System.err.println("-h for more information");
            return;
        }
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

    public static String getDirFromArgs(LibDBArgs libDBArgs){
        String dirPath = null;
        if(libDBArgs.outputPathUnset()){
            //write to current folder
            dirPath = "./";
        }
        else{
            //write to specified folder
            dirPath = libDBArgs.getOutputFilePath();
            File checkDir = new File(dirPath);
            if((checkDir.exists() && !checkDir.isDirectory()) || (!checkDir.exists())){
                try {
                    checkDir.mkdir();
                }catch (Exception e){
                    e.printStackTrace();
                    System.err.println("Fail to create dir " + dirPath);
                    return null;
                }
            }
            if(!dirPath.endsWith("/")){
                dirPath += "/";
            }
        }
        return dirPath;
    }

    public static boolean repoTypeCheck(LibDBArgs libDBArgs){
        if(libDBArgs.repoTypeUnset()){
            System.err.println("Set type for file fetching(-r lib or apk)");
            return false;
        }
        String repoType = libDBArgs.getRepoType();
        if(!repoType.equals("lib") && !repoType.equals("apk")){
            System.err.println("Error repo type, only support lib and apk");
            return false;
        }
        // TODO: 2018/9/15 to merge apk repo and lib repo
        if(repoType.equals("apk")){
            throw new RuntimeException("Apk Repo unsupported yet");
        }
        return true;
    }


    public static void singleFetchHandler(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        if(!repoTypeCheck(libDBArgs)){
            return;
        }
        String hashKey = libDBArgs.getHashKey();
        FileHandle fileHandle = getFileHandle(hashKey, client);
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

    public static void multiFetchHandler(LibDBArgs libDBArgs, LibDBServiceClient client)throws TException{
        if(!repoTypeCheck(libDBArgs)){
            return;
        }
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
                        if(!(hashVal.length() == 32 || hashVal.length() == 64)){
                            System.err.println(hashVal + " Error length or hash key(only support md5 and sha256)");
                            continue;
                        }
                        if (hashVal != null) {
                            FileHandle fileHandle = fileRepo.syncGetFile(hashVal);
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

    public static void fetchHandler(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException{
        if(!libDBArgs.hashKeyUnset() && !libDBArgs.hashListFilePathUnset()){
            System.err.println("Please specify the operation type of fetch(-k or -hl)");
            System.err.println("-h for more information");
            return;
        }
        if(!libDBArgs.hashKeyUnset()){
            singleFetchHandler(libDBArgs, client);
        }
        else if (!libDBArgs.hashListFilePathUnset()){
            multiFetchHandler(libDBArgs, client);
        }
        else{
            System.err.println("No specified fetch operation, please set -k or -hl");
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
