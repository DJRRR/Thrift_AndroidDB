package cn.fudan.androiddb.server;

import cn.fudan.androiddb.AndroidDBConfig;
import cn.fudan.androiddb.thrift.AndroidDBService;
import cn.fudan.androiddb.thrift.AndroidDBServiceImpl;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import java.net.InetSocketAddress;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class AndroidDBServiceServer {
    // TODO: 2018/9/17 to add server log function
    public static void main(String[] args) throws TTransportException{
        System.out.println("LibDB Service Starts...");

        String serverIP = AndroidDBConfig.getConfig(AndroidDBConfig.PROP_KEY_SERVER_BIND_IP);
        String serverPort = AndroidDBConfig.getConfig(AndroidDBConfig.PROP_KEY_SERVER_BIND_PORT);
        TProcessor tProcessor = new AndroidDBService.Processor<AndroidDBService.Iface>(new AndroidDBServiceImpl());
        TServerSocket serverTransport = new TServerSocket(new InetSocketAddress(serverIP, Integer.parseInt(serverPort)));
        TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(serverTransport);
        tArgs.processor(tProcessor);
        tArgs.protocolFactory(new TBinaryProtocol.Factory());

        System.out.println("Start to server RPC request at ("+serverIP+":"+serverPort+")....");
        TServer server = new TThreadPoolServer(tArgs);
        server.serve();

    }
}
