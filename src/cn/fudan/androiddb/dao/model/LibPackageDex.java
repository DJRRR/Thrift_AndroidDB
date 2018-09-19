package cn.fudan.androiddb.dao.model;

/**
 * @author Dai Jiarun
 * @date 2018/7/10
 */
public class LibPackageDex extends MavenPackage{
    public String dexFileSHA256;

    public LibPackageDex(DownloadedLibPackage downloadedLibPackage){
        groupName = downloadedLibPackage.groupName;
        artifactId = downloadedLibPackage.artifactId;
        version = downloadedLibPackage.version;
        dexFileSHA256 = downloadedLibPackage.dexFileMD5;
    }
}
