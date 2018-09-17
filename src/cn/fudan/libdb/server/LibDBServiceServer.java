package cn.fudan.libdb.server;

import cn.fudan.libdb.LibDBConfig;
import cn.fudan.libdb.thrift.LibDBService;
import cn.fudan.libdb.thrift.LibDBServiceImpl;
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
public class LibDBServiceServer {
    // TODO: 2018/9/17 to add server log function
    public static void main(String[] args) throws TTransportException{
        System.out.println("LibDB Service Starts...");

        String serverIP = LibDBConfig.getConfig(LibDBConfig.PROP_KEY_SERVER_BIND_IP);
        String serverPort = LibDBConfig.getConfig(LibDBConfig.PROP_KEY_SERVER_BIND_PORT);
        TProcessor tProcessor = new LibDBService.Processor<LibDBService.Iface>(new LibDBServiceImpl());
        TServerSocket serverTransport = new TServerSocket(new InetSocketAddress(serverIP, Integer.parseInt(serverPort)));
        TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(serverTransport);
        tArgs.processor(tProcessor);
        tArgs.protocolFactory(new TBinaryProtocol.Factory());

        System.out.println("Start to server RPC request at ("+serverIP+":"+serverPort+")....");
        TServer server = new TThreadPoolServer(tArgs);
        server.serve();

    }
}
