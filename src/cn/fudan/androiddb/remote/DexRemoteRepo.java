package cn.fudan.androiddb.remote;

import cn.fudan.androiddb.AndroidDBConfig;
import cn.fudan.androiddb.dao.model.DownloadedLibPackage;
import cn.fudan.androiddb.thrift.FileInfo;

import java.io.File;
import java.util.List;

import static cn.fudan.androiddb.AndroidDBConfig.PROP_KEY_DEX_ROOT_DIR;
import static cn.fudan.androiddb.util.Constants.DEX_SUFFIX;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class DexRemoteRepo extends RemoteRepo {
    public static final String DEX_ROOT_DIR = AndroidDBConfig.getConfig(PROP_KEY_DEX_ROOT_DIR);
    private static DexRemoteRepo singleInstance;


    public synchronized static DexRemoteRepo getInstance(){
        if(singleInstance == null){
            singleInstance = new DexRemoteRepo();
        }
        return singleInstance;
    }

    @Override
    public String queryResToStr(List<DownloadedLibPackage> resList, int limit){
        List<String> dexRes = DownloadedLibPackage.DBHelper.queryResToDexStr(resList);
        if(dexRes == null){
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < Math.min(limit, dexRes.size()); i++){
            stringBuffer.append(dexRes.get(i));
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
        return DownloadedLibPackage.DBHelper.queryResToDexJson(partList);
    }

    @Override
    public FileInfo getFileInfoFromRepo(String key) {
        File dexFile = new File(DEX_ROOT_DIR, key);
        if (dexFile.exists())
            return new FileInfo(readFileToBuffer(dexFile), DEX_SUFFIX);

        dexFile = new File(DEX_ROOT_DIR, key + DEX_SUFFIX);
        if (dexFile.exists())
            return new FileInfo(readFileToBuffer(dexFile), DEX_SUFFIX);

        return null;
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
    public FileInfo fetch(String key){
        FileInfo fileInfo = getFileInfoFromRepo(key);
        if(fileInfo == null){
            return null;
        }
        else{
            return fileInfo;
        }
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
