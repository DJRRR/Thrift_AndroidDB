package cn.fudan.androiddb.remote;

import cn.fudan.androiddb.AndroidDBConfig;
import cn.fudan.androiddb.dao.model.DownloadedLibPackage;
import cn.fudan.androiddb.dao.model.FDroidApkInfo;
import cn.fudan.androiddb.thrift.FileInfo;

import java.io.File;
import java.util.List;

import static cn.fudan.androiddb.AndroidDBConfig.PROP_KEY_APK_ROOT_DIR;
import static cn.fudan.androiddb.util.Constants.APK_SUFFIX;

/**
 * @author Dai Jiarun
 * @date 2018/9/17
 */
public class ApkRemoteRepo extends RemoteRepo{
    public static final String APK_ROOT_DIR = AndroidDBConfig.getConfig(PROP_KEY_APK_ROOT_DIR);
    private static ApkRemoteRepo singleInstance;


    public synchronized static ApkRemoteRepo getInstance(){
        if(singleInstance == null){
            singleInstance = new ApkRemoteRepo();
        }
        return singleInstance;
    }

    @Override
    public FileInfo getFileInfoFromRepo(String key) {
        File apkFile = new File(APK_ROOT_DIR, key);
        if (apkFile.exists())
            return new FileInfo(readFileToBuffer(apkFile), APK_SUFFIX);

        apkFile = new File(APK_ROOT_DIR, key + APK_SUFFIX);
        if (apkFile.exists())
            return new FileInfo(readFileToBuffer(apkFile), APK_SUFFIX);

        return null;
    }

    @Override
    public FileInfo getFileInfoFromRepo(String packageName, String versionCode){
        FDroidApkInfo fDroidApkInfo = FDroidApkInfo.DBHelper.queryFDroidApkInfo(packageName, versionCode);
        String key = fDroidApkInfo.apkHash;
        File apkFile = new File(APK_ROOT_DIR, key);
        if (apkFile.exists())
            return new FileInfo(readFileToBuffer(apkFile), APK_SUFFIX);

        apkFile = new File(APK_ROOT_DIR, key + APK_SUFFIX);
        if (apkFile.exists())
            return new FileInfo(readFileToBuffer(apkFile), APK_SUFFIX);

        return null;

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
    public FileInfo fetch(String packageName, String versionCode){
        FileInfo fileInfo = getFileInfoFromRepo(packageName, versionCode);
        if(fileInfo == null){
            return null;
        }
        else{
            return fileInfo;
        }
    }

    @Override
    public String queryLibsByGAV(String groupName, String artifactId, String version, boolean jsonOutput, int limit){
        throw new RuntimeException("Need to be refactored");
    }

    @Override
    public String queryResToStr(List<DownloadedLibPackage> resList, int limit){
        throw new RuntimeException("Need to be refactored");
    }

    @Override
    public String queryResToJson(List<DownloadedLibPackage> resList, int limit){
        throw new RuntimeException("Need to be refactored");
    }

}
