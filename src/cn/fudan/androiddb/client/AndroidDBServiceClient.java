package cn.fudan.androiddb.client;

import cn.fudan.androiddb.AndroidDBConfig;
import cn.fudan.androiddb.thrift.FileInfo;
import cn.fudan.androiddb.thrift.AndroidDBService;
import com.beust.jcommander.JCommander;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.*;

import static cn.fudan.androiddb.client.handler.FetchHandler.fetchHandler;
import static cn.fudan.androiddb.client.handler.QueryHandler.queryHandler;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class AndroidDBServiceClient implements AndroidDBService.Iface{
    private static Set<AndroidDBService.Client> clientPool = new HashSet<>();
    private static AndroidDBService.Client createClient(){
        try{
            String serverIP = AndroidDBConfig.getConfig(AndroidDBConfig.PROP_KEY_CLIENT_BIND_IP);
            String serverPort = AndroidDBConfig.getConfig(AndroidDBConfig.PROP_KEY_SERVER_BIND_PORT);
            TTransport transport = new TSocket(serverIP, Integer.parseInt(serverPort));
            TProtocol tprotocol = new TBinaryProtocol(transport);
            transport.open();
            return new AndroidDBService.Client(tprotocol);
        } catch (TTransportException e){
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isClientOpen(AndroidDBService.Client client) {
        if (! client.getOutputProtocol().getTransport().isOpen() ||
                ! client.getInputProtocol().getTransport().isOpen())
            return false;
        return true;
    }

    public static AndroidDBServiceClient defaultClient() {
        return new AndroidDBServiceClient();
    }


    private static AndroidDBService.Client getAvailableClient() throws TException {
        synchronized (clientPool) {
            if (clientPool.size() == 0) {
                AndroidDBService.Client client = createClient();
                return client;
            } else {
                Iterator<AndroidDBService.Client> clientIterator = clientPool.iterator();
                AndroidDBService.Client client = null;
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
        AndroidDBService.Client client = getAvailableClient();
        int result = client.ping(test);
        synchronized (clientPool) {
            clientPool.add(client);
        }
        return result;
    }


    @Override
    public java.lang.String queryLibsByGAV(java.lang.String groupName, java.lang.String artifactId, java.lang.String version, java.lang.String repoType, boolean jsonOutput, int limit) throws org.apache.thrift.TException{
        AndroidDBService.Client client = getAvailableClient();
        String result = client.queryLibsByGAV(groupName,artifactId,version,repoType,jsonOutput,limit);
        synchronized (clientPool){
            clientPool.add(client);
        }
        return result;
    }

    @Override
    public FileInfo fetchLibByHash(java.lang.String hash) throws org.apache.thrift.TException{
        AndroidDBService.Client client = getAvailableClient();
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
        AndroidDBService.Client client = getAvailableClient();
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
        AndroidDBService.Client client = getAvailableClient();
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
        AndroidDBService.Client client = getAvailableClient();
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
        AndroidDBService.Client client = getAvailableClient();
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

    public static void testConnection(AndroidDBServiceClient client) throws TException{
        client.ping(1);
    }

    public static void main(String ... argv) throws TException{
        AndoidDBArgs andoidDBArgs = new AndoidDBArgs();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(andoidDBArgs)
                .build();
        jCommander.parse(argv);
        if(andoidDBArgs.isHelp()){
            jCommander.usage();
            return;
        }
        AndroidDBServiceClient client = AndroidDBServiceClient.defaultClient();
       //testConnection(client);
        if(andoidDBArgs.isQuery() && andoidDBArgs.isFetch()){
            System.out.println("Please specify the operation type(-q or -f)");
            System.err.println("-h for more information");
            return;
        }
        if(andoidDBArgs.isQuery()){
            queryHandler(andoidDBArgs, client);
        }
        else if(andoidDBArgs.isFetch()){
            fetchHandler(andoidDBArgs, client);
        }
        else{
            System.err.println("No specified operation, please set the operation type(-q or -f).");
            System.err.println("-h for more information");
        }
    }
}
