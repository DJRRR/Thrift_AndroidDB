package cn.fudan.androiddb.crawler.crawlerEntity;

/**
 * Created by qiaoying on 2017/8/10.
 */
public class AppInfo {
    /**
     用于爬取App的信息
     */
    private String id;    //all循环的标识
    private String appName; //app名称，中文名
    private String pkgName;     //app包名
    private String authorName;
    private String appDownCount; //app下载数量
    private String versionName;   //app版本号
    private String categoryName;  //app分类
    private String appSize;       //app大小
    private String apkMd5;        //app Md5
    private String appDownUrl;    //app下载链接

    /**
    Apk中的信息
    * */
    private boolean apkDownloaded;//apk是否已下载
    private String apkActualSize;
    private String apkActualMd5;
    private String apkActualPkgName;
    private String apkStoreTag;
    private String apkStoreKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAppDownCount() {
        return appDownCount;
    }

    public void setAppDownCount(String appDownCount) {
        this.appDownCount = appDownCount;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getApkMd5() {
        return apkMd5;
    }

    public void setApkMd5(String apkMd5) {
        this.apkMd5 = apkMd5;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getAppDownUrl() {
        return appDownUrl;
    }

    public void setAppDownUrl(String appDownUrl) {
        this.appDownUrl = appDownUrl;
    }

    public boolean isApkDownloaded() {
        return apkDownloaded;
    }

    public void setApkDownloaded(boolean apkDownloaded) {
        this.apkDownloaded = apkDownloaded;
    }

    public String getApkActualSize() {
        return apkActualSize;
    }

    public void setApkActualSize(String apkActualSize) {
        this.apkActualSize = apkActualSize;
    }

    public String getApkActualMd5() {
        return apkActualMd5;
    }

    public void setApkActualMd5(String apkActualMd5) {
        this.apkActualMd5 = apkActualMd5;
    }

    public String getApkActualPkgName() {
        return apkActualPkgName;
    }

    public void setApkActualPkgName(String apkActualPkgName) {
        this.apkActualPkgName = apkActualPkgName;
    }

    public String getApkStoreTag() {
        return apkStoreTag;
    }

    public void setApkStoreTag(String apkStoreTag) {
        this.apkStoreTag = apkStoreTag;
    }

    public String getApkStoreKey() {
        return apkStoreKey;
    }

    public void setApkStoreKey(String apkStoreKey) {
        this.apkStoreKey = apkStoreKey;
    }


    @Override
    public String toString() {
        return "AppInfo{" +
                "id=" + id +
                ", appname=" + appName +
                ", pkgname=" + pkgName +
                "}";
    }
}
