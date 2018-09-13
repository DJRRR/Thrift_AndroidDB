package cn.fudan.libdb.thrift;

import cn.fudan.libdb.core.GeneralRemoteRepo;
import cn.fudan.libdb.core.RemoteRepo;
import cn.fudan.libdb.core.RemoteRepoFactory;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

import static cn.fudan.libdb.core.RemoteRepoFactory.DEX_REPO;
import static cn.fudan.libdb.core.RemoteRepoFactory.JAR_REPO;

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

    //TODO: to support source-code fetcher
    @Override
    public java.nio.ByteBuffer fetch(java.lang.String hash) throws org.apache.thrift.TException{
        RemoteRepo remoteRepo;
        if(hash.length() == 32){//md5 jar()
            remoteRepo = RemoteRepoFactory.getRepo(JAR_REPO);
            return remoteRepo.fetch(hash);
        }
        else if(hash.length() == 64){//sha256 dex
            remoteRepo = RemoteRepoFactory.getRepo(DEX_REPO);
            return remoteRepo.fetch(hash);
        }
        return null;
    }



}
