package cn.fudan.androiddb.remote;

import cn.fudan.androiddb.dao.model.DownloadedLibPackage;
import cn.fudan.androiddb.thrift.FileInfo;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public abstract class RemoteRepo {
    protected ByteBuffer readFileToBuffer(File file) {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int actualRead = inputStream.read(buffer);
            while(actualRead > 0) {
                outputStream.write(buffer, 0, actualRead);
                actualRead = inputStream.read(buffer);
            }

            inputStream.close();
            ByteBuffer result = ByteBuffer.wrap(outputStream.toByteArray());
            outputStream.close();

            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: 2018/9/17 Need to be factored(to split apk & lib) 
    public abstract String queryResToStr(List<DownloadedLibPackage> resList, int limit);

    public abstract String queryResToJson(List<DownloadedLibPackage> resList, int limit);

    public abstract FileInfo fetch(String key);

    public abstract FileInfo fetch(String packageName, String versionCode);

    public abstract String queryLibsByGAV(String groupName, String artifactId, String version, boolean jsonOutput, int limit);

    public abstract FileInfo getFileInfoFromRepo(String key);

    public abstract FileInfo getFileInfoFromRepo(String packageName, String versionCode);

    
    

}
