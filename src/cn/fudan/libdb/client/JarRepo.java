package cn.fudan.libdb.client;

import cn.fudan.libdb.core.RemoteRepoFactory;
import cn.fudan.libdb.util.FileHandle;
import org.apache.thrift.TException;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/9/13
 */
public class JarRepo extends FileRepo{
    public JarRepo(String fileHashFile, int prefetchCount) {
        this.init(RemoteRepoFactory.JAR_REPO, loadHashListFromFile(fileHashFile), prefetchCount);
    }

    public JarRepo(List<String> fileHashList, int prefetchCount) {
        this.init(RemoteRepoFactory.JAR_REPO, fileHashList, prefetchCount);
    }

    @Override
    public FileHandle getFile(String fileHash) {
        return getJar(fileHash);
    }

    @Override
    public boolean hashValCheck(String fileHash){
        return fileHash.length() == 32;
    }

    /*
    * Get the jarHash-specified jar
    * */
    public static FileHandle getJar(String jarHash) {
        try {
            ByteBuffer result = LibDBServiceClient.defaultClient().fetch(jarHash);
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
