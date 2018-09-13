package cn.fudan.libdb.client;

import cn.fudan.libdb.core.RemoteRepoFactory;
import cn.fudan.libdb.util.FileHandle;
import org.apache.thrift.TException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/9/13
 */
public class DexRepo extends FileRepo {
    public DexRepo(String fileHashFile, int prefetchCount) {
        this.init(RemoteRepoFactory.DEX_REPO, loadHashListFromFile(fileHashFile), prefetchCount);
    }

    public DexRepo(List<String> fileHashList, int prefetchCount) {
        this.init(RemoteRepoFactory.DEX_REPO, fileHashList, prefetchCount);
    }

    @Override
    public FileHandle getFile(String fileHash) {
        return getDex(fileHash);
    }

    @Override
    public boolean hashValCheck(String fileHash){
        return fileHash.length() == 64;
    }

    /*
    * Get the dexHash-specified dex
    * */
    public static FileHandle getDex(String dexHash) {
        try {
            ByteBuffer result = LibDBServiceClient.defaultClient().fetch(dexHash);
            if(result == null){
                return null;
            }
            return new FileHandle(result.array());
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


}