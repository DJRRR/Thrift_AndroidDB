package cn.fudan.androiddb.client;

import cn.fudan.androiddb.thrift.FileInfo;
import cn.fudan.androiddb.util.Constants;
import cn.fudan.androiddb.util.FileHandle;
import org.apache.thrift.TException;

import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/9/15
 */
public class LibRepo extends FileRepo {
    public LibRepo(String fileHashFile, int prefetchCount) {
        this.init(Constants.LIB_REPO, loadHashListFromFile(fileHashFile), prefetchCount);
    }

    public LibRepo(List<String> fileHashList, int prefetchCount) {
        this.init(Constants.LIB_REPO, fileHashList, prefetchCount);
    }

    @Override
    public FileHandle getFileHandle(String fileHash) {
        return getLibPackage(fileHash);
    }

    @Override
    public boolean hashValCheck(String fileHash){
        return (fileHash.length() == 64 || fileHash.length() == 32);
    }

    /*
    * Get the (md5/ sha256)Hash-specified dex/jar/aar/apklib
    * */
    public static FileHandle getLibPackage(String hash) {
        try {
            FileInfo result = AndroidDBServiceClient.defaultClient().fetchLibByHash(hash);
            if(result == null){
                return null;
            }
            return new FileHandle(result.content.array(), result.suffix);
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


}