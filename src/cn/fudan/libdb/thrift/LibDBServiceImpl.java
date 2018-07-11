package cn.fudan.libdb.thrift;

import cn.fudan.libdb.core.GeneralRemoteRepo;
import cn.fudan.libdb.core.RemoteRepo;
import cn.fudan.libdb.core.RemoteRepoFactory;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class LibDBServiceImpl implements LibDBService.Iface {
    @Override
    public int ping(int test) throws org.apache.thrift.TException{
        System.out.println("ping: "+test);
        return test + 1;
    }

    @Override
    public java.lang.String queryLibsByGAV(java.lang.String groupName, java.lang.String artifactId, java.lang.String version, java.lang.String repoType, boolean jsonOutput, int limit) throws org.apache.thrift.TException{
        RemoteRepo remoteRepo = RemoteRepoFactory.getRepo(repoType);
        return remoteRepo.queryLibsByGAV(groupName, artifactId, version, jsonOutput, limit);
    }



}
