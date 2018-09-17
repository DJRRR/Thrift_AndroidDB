package cn.fudan.libdb.remote;

import cn.fudan.libdb.LibDBConfig;
import cn.fudan.libdb.dao.model.DownloadedLibPackage;
import cn.fudan.libdb.thrift.FileInfo;

import java.io.File;
import java.util.List;

import static cn.fudan.libdb.LibDBConfig.PROP_KEY_AAR_ROOT_DIR;
import static cn.fudan.libdb.LibDBConfig.PROP_KEY_APKLIB_ROOT_DIR;
import static cn.fudan.libdb.LibDBConfig.PROP_KEY_JAR_ROOT_DIR;
import static cn.fudan.libdb.util.Constants.AAR_SUFFIX;
import static cn.fudan.libdb.util.Constants.APKLIB_SUFFIX;
import static cn.fudan.libdb.util.Constants.JAR_SUFFIX;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class JarRemoteRepo extends RemoteRepo{
    public static final String JAR_ROOT_DIR = LibDBConfig.getConfig(PROP_KEY_JAR_ROOT_DIR);
    public static final String AAR_ROOT_DIR = LibDBConfig.getConfig(PROP_KEY_AAR_ROOT_DIR);
    public static final String APKLIB_ROOT_DIR = LibDBConfig.getConfig(PROP_KEY_APKLIB_ROOT_DIR);

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
    public FileInfo getFileInfoFromRepo(String key) {
        File jarFile = new File(JAR_ROOT_DIR, key + JAR_SUFFIX);
        if (jarFile.exists()) {
            return new FileInfo(readFileToBuffer(jarFile), JAR_SUFFIX);
        }

        File aarFile = new File(AAR_ROOT_DIR, key + AAR_SUFFIX);
        if(aarFile.exists()){
            return new FileInfo(readFileToBuffer(aarFile), AAR_SUFFIX);
        }

        File apklibFile = new File(APKLIB_ROOT_DIR, key + APKLIB_SUFFIX);
        if(apklibFile.exists()){
            return new FileInfo(readFileToBuffer(apklibFile), APKLIB_SUFFIX);
        }

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
