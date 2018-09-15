package cn.fudan.libdb.client;

import cn.fudan.libdb.remote.RemoteRepoFactory;
import cn.fudan.libdb.thrift.FileInfo;
import cn.fudan.libdb.util.Constants;
import cn.fudan.libdb.util.FileHandle;
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
    public FileHandle getFile(String fileHash) {
        return getLibPackage(fileHash);
    }

    @Override
    public boolean hashValCheck(String fileHash){
        return (fileHash.length() == 64 || fileHash.length() == 32);
    }

    /*
    * Get the (md5/ sha256)Hash-specified dex/jar/aar/apklib
    * */
    public static FileHandle getLibPackage(String dexHash) {
        try {
            FileInfo result = LibDBServiceClient.defaultClient().fetch(dexHash);
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