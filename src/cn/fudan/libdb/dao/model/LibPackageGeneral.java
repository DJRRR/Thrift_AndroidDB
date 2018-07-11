package cn.fudan.libdb.dao.model;

/**
 * @author Dai Jiarun
 * @date 2018/7/10
 */
public class LibPackageGeneral extends MavenPackage{
    public String dexFileSHA256;
    public String jarFileMD5;

    public LibPackageGeneral(DownloadedLibPackage downloadedLibPackage){
        groupName = downloadedLibPackage.groupName;
        artifactId = downloadedLibPackage.artifactId;
        version = downloadedLibPackage.version;
        dexFileSHA256 = downloadedLibPackage.dexFileMD5;
        jarFileMD5 = downloadedLibPackage.jarFileMD5;
    }

}
