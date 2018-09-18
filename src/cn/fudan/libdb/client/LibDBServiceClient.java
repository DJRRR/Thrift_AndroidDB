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
import static cn.fudan.libdb.client.handler.FetchHandler.fetchHandler;
import static cn.fudan.libdb.client.handler.QueryHandler.queryHandler;

import cn.fudan.libdb.client.handler.*;
/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class LibDBServiceClient implements LibDBService.Iface{
    private static Set<LibDBService.Client> clientPool = new HashSet<>();
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
