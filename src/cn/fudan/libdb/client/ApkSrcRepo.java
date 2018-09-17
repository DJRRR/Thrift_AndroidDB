package cn.fudan.libdb.client;

import cn.fudan.libdb.thrift.FileInfo;
import cn.fudan.libdb.util.Constants;
import cn.fudan.libdb.util.FileHandle;
import org.apache.thrift.TException;

import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/9/17
 */
public class ApkSrcRepo extends FileRepo{
    public ApkSrcRepo(String fileHashFile, int prefetchCount) {
        this.init(Constants.APK_SRC_REPO, loadHashListFromFile(fileHashFile), prefetchCount);
    }

    public ApkSrcRepo(List<String> fileHashList, int prefetchCount) {
        this.init(Constants.APK_SRC_REPO, fileHashList, prefetchCount);
    }

    @Override
    public FileHandle getFileHandle(String fileHash) {
        if(fileHash.length() == 32 && !fileHash.contains("_")) {
            return getApkSrcPackage(fileHash);
        }
        else if(fileHash.contains("_")){
            String[] fileHashSplit = fileHash.split("_");
            return getApkSrcPackage(fileHashSplit[0], fileHashSplit[1]);
        }
        throw new RuntimeException("Should not reach here in ApkRepo.getFileHandle");
    }

    @Override
    public boolean hashValCheck(String fileHash){
        if(fileHash.length() == 32 && !fileHash.contains("_")){
            return true;
        }
        else if(fileHash.contains("_")){
            String[] fileHashSplit = fileHash.split("_");
            if(fileHashSplit.length == 2){
                return true;
            }
        }
        return false;
    }

    /*
    * Get the md5 hash or (packageName, versionCode) specified apk
    */
    public static FileHandle getApkSrcPackage(String hash) {
        try {
            FileInfo result = LibDBServiceClient.defaultClient().fetchApkSrcByHash(hash);
            if(result == null){
                return null;
            }
            return new FileHandle(result.content.array(), result.suffix);
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FileHandle getApkSrcPackage(String packageName, String versionCode){
        try{
            FileInfo result = LibDBServiceClient.defaultClient().fetchApkSrcByName(packageName, versionCode);
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
