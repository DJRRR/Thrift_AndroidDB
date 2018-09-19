package cn.fudan.androiddb.client;

import cn.fudan.androiddb.thrift.FileInfo;
import cn.fudan.androiddb.util.Constants;
import cn.fudan.androiddb.util.FileHandle;
import org.apache.thrift.TException;

import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/9/17
 */
public class ApkRepo extends FileRepo{
    public ApkRepo(String fileHashFile, int prefetchCount) {
        this.init(Constants.APK_REPO, loadHashListFromFile(fileHashFile), prefetchCount);
    }

    public ApkRepo(List<String> fileHashList, int prefetchCount) {
        this.init(Constants.APK_REPO, fileHashList, prefetchCount);
    }

    @Override
    public FileHandle getFileHandle(String fileHash) {
        if(fileHash.length() == 32 && !fileHash.contains("_")) {
            return getApkPackage(fileHash);
        }
        else if(fileHash.contains("_")){
            int splitIndex = fileHash.lastIndexOf("_");
            String packageName = fileHash.substring(0, splitIndex);
            String versionCode = fileHash.substring(splitIndex + 1, fileHash.length());
            return getApkPackage(packageName, versionCode);
        }
        throw new RuntimeException("Should not reach here in ApkRepo.getFileHandle");
    }

    @Override
    public boolean hashValCheck(String fileHash){
        if(fileHash.length() == 32 && !fileHash.contains("_")){
            return true;
        }
        else if(fileHash.contains("_")){
            int splitIndex = fileHash.lastIndexOf("_");
            if(splitIndex != fileHash.length() - 1){
                return true;
            }
        }
        return false;
    }

    /*
    * Get the md5 hash or (packageName, versionCode) specified apk
    */
    public static FileHandle getApkPackage(String hash) {
        try {
            FileInfo result = AndroidDBServiceClient.defaultClient().fetchApkByHash(hash);
            if(result == null){
                return null;
            }
            return new FileHandle(result.content.array(), result.suffix);
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FileHandle getApkPackage(String packageName, String versionCode){
        try{
            FileInfo result = AndroidDBServiceClient.defaultClient().fetchApkByName(packageName, versionCode);
            if(result == null){
                return null;
            }
            return new FileHandle(result.content.array(), result.suffix);
        }catch (TException e){
            e.printStackTrace();
        }
        return null;
    }

}
