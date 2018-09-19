package cn.fudan.androiddb.dao.model;

/**
 * @author Dai Jiarun
 * @date 2018/7/10
 */
public class LibPackageJar extends MavenPackage{
    public String jarFileMD5;

    public LibPackageJar(DownloadedLibPackage downloadedLibPackage){
        groupName = downloadedLibPackage.groupName;
        artifactId = downloadedLibPackage.artifactId;
        version = downloadedLibPackage.version;
        jarFileMD5 = downloadedLibPackage.jarFileMD5;
    }
}
