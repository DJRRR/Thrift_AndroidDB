package cn.fudan.libdb.thrift;

import cn.fudan.libdb.remote.RemoteRepo;
import cn.fudan.libdb.remote.RemoteRepoFactory;

import static cn.fudan.libdb.util.Constants.*;


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
    public FileInfo fetchLibByHash(java.lang.String hash) throws org.apache.thrift.TException{
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

    @Override
    public FileInfo fetchApkByHash(java.lang.String hash) throws org.apache.thrift.TException{
        RemoteRepo remoteRepo;
        if(hash.length() == 32){//md5
            remoteRepo = RemoteRepoFactory.getRepo(APK_REPO);
            return remoteRepo.fetch(hash);
        }
        return null;
    }

    @Override
    public FileInfo fetchApkByName(java.lang.String packageName, java.lang.String versionCode) throws org.apache.thrift.TException{
        RemoteRepo remoteRepo = RemoteRepoFactory.getRepo(APK_REPO);
        return remoteRepo.fetch(packageName, versionCode);
    }

    @Override
    public FileInfo fetchApkSrcByHash(java.lang.String hash) throws org.apache.thrift.TException{
        RemoteRepo remoteRepo;
        if(hash.length() == 32){//md5
            remoteRepo = RemoteRepoFactory.getRepo(APK_SRC_REPO);
            return remoteRepo.fetch(hash);
        }
        return null;
    }

    @Override
    public FileInfo fetchApkSrcByName(java.lang.String packageName, java.lang.String versionCode) throws org.apache.thrift.TException{
        RemoteRepo remoteRepo = RemoteRepoFactory.getRepo(APK_SRC_REPO);
        return remoteRepo.fetch(packageName, versionCode);
    }



}
