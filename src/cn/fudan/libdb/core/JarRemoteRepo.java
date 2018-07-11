package cn.fudan.libdb.core;

import cn.fudan.libdb.dao.model.DownloadedLibPackage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class JarRemoteRepo extends RemoteRepo{
    private static JarRemoteRepo singleInstance;
    public synchronized static JarRemoteRepo getInstance(){
        if(singleInstance == null){
            singleInstance = new JarRemoteRepo();
        }
        return singleInstance;
    }

    @Override
    public String queryResToStr(List<DownloadedLibPackage> resList, int limit){
        List<String> jarRes = DownloadedLibPackage.DBHelper.queryResToJarStr(resList);
        if(jarRes == null){
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < Math.min(limit, jarRes.size()); i++){
            stringBuffer.append(jarRes.get(i));
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    @Override
    public String queryResToJson(List<DownloadedLibPackage> resList, int limit){
        if(resList == null){
            return null;
        }
        List<DownloadedLibPackage> partList = resList.subList(0, Math.min(limit, resList.size()));
        return DownloadedLibPackage.DBHelper.queryResToJarJson(partList);
    }

    @Override
    public ByteBuffer fetch(String key){
        return null;
    }


    @Override
    public String queryLibsByGAV(String groupName, String artifactId, String version, boolean jsonOutput, int limit){
        List<DownloadedLibPackage> queryRes = DownloadedLibPackage.DBHelper.queryDownloadedLibPackageEliminate(groupName, artifactId, version);
        if(jsonOutput){
            return queryResToJson(queryRes, limit);
        }
        else{
            return queryResToStr(queryRes, limit);
        }
    }
}
