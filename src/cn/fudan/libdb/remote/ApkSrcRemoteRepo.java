package cn.fudan.libdb.remote;

import cn.fudan.libdb.LibDBConfig;
import cn.fudan.libdb.dao.model.DownloadedLibPackage;
import cn.fudan.libdb.dao.model.FDroidApkInfo;
import cn.fudan.libdb.thrift.FileInfo;
import cn.fudan.libdb.util.FileHandle;

import java.io.File;
import java.util.List;

import static cn.fudan.libdb.LibDBConfig.PROP_KEY_APK_SOURCE_ROOT_DIR;
import static cn.fudan.libdb.util.Constants.APK_SRC_SUFFIX;

/**
 * @author Dai Jiarun
 * @date 2018/9/17
 */
public class ApkSrcRemoteRepo extends RemoteRepo{
    public static final String APK_SRC_ROOT_DIR = LibDBConfig.getConfig(PROP_KEY_APK_SOURCE_ROOT_DIR);
    private static ApkSrcRemoteRepo singleInstance;


    public synchronized static ApkSrcRemoteRepo getInstance(){
        if(singleInstance == null){
            singleInstance = new ApkSrcRemoteRepo();
        }
        return singleInstance;
    }

    @Override
    public FileInfo getFileInfoFromRepo(String key) {
        FDroidApkInfo fDroidApkInfo = FDroidApkInfo.DBHelper.queryFDroidApkInfo(key);
        if(fDroidApkInfo == null){
            return null;
        }
        String packageName = fDroidApkInfo.packageName;
        String versionCode = fDroidApkInfo.versionCode;
        File apkSrcFile = new File(APK_SRC_ROOT_DIR, packageName + "_" + versionCode);
        if (apkSrcFile.exists())
            return new FileInfo(readFileToBuffer(apkSrcFile), APK_SRC_SUFFIX);

        apkSrcFile = new File(APK_SRC_ROOT_DIR, packageName + "_" + versionCode + APK_SRC_SUFFIX);
        String test = APK_SRC_ROOT_DIR + packageName + "_" + versionCode + APK_SRC_SUFFIX;
        System.out.println(test);
        if (apkSrcFile.exists())
            return new FileInfo(readFileToBuffer(apkSrcFile), APK_SRC_SUFFIX);

        return null;
    }

    @Override
    public FileInfo getFileInfoFromRepo(String packageName, String versionCode){
        File apkSrcFile = new File(APK_SRC_ROOT_DIR, packageName + "_" + versionCode);
        if(apkSrcFile.exists()){
            return new FileInfo(readFileToBuffer(apkSrcFile), APK_SRC_SUFFIX);
        }
        apkSrcFile = new File(APK_SRC_ROOT_DIR, packageName + "_" + versionCode + APK_SRC_SUFFIX);
        if (apkSrcFile.exists())
            return new FileInfo(readFileToBuffer(apkSrcFile), APK_SRC_SUFFIX);

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
