package cn.fudan.androiddb.remote;

import cn.fudan.androiddb.dao.model.DownloadedLibPackage;
import cn.fudan.androiddb.thrift.FileInfo;

import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class LibRemoteRepo extends RemoteRepo{
    private static LibRemoteRepo singleInstance;
    public synchronized static LibRemoteRepo getInstance(){
        if(singleInstance == null){
            singleInstance = new LibRemoteRepo();
        }
        return singleInstance;
    }

    @Override
    public String queryResToStr(List<DownloadedLibPackage> resList, int limit){
        List<String> generalRes = DownloadedLibPackage.DBHelper.queryResToGeneralStr(resList);
        if(generalRes == null){
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < Math.min(limit, generalRes.size()); i++){
            stringBuffer.append(generalRes.get(i));
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
        return DownloadedLibPackage.DBHelper.queryResToGeneralJson(partList);
    }


    @Override
    public FileInfo getFileInfoFromRepo(String key){
        throw new RuntimeException("not support");
    }

    @Override
    public FileInfo fetch(String key){
        throw new RuntimeException("not support");
    }


    @Override
    public FileInfo getFileInfoFromRepo(String packageName, String versionCode){
        throw new RuntimeException("Need to be refactored");
    }

    @Override
    public FileInfo fetch(String packageName, String versionCode){
        throw new RuntimeException("Need to be refactored");
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
