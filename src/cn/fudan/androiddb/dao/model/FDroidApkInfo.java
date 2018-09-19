package cn.fudan.androiddb.dao.model;

import cn.fudan.androiddb.dao.util.DBUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@DatabaseTable(tableName = "fdroid_apk_info")
public class FDroidApkInfo {
    @DatabaseField(generatedId=true) public int id;
    @DatabaseField() public String packageName;
    @DatabaseField() public String version;
    @DatabaseField() public String versionCode;
    @DatabaseField() public String versionDate;
    @DatabaseField() public String apkUrl;
    @DatabaseField() public String apkHash;
    @DatabaseField() public Timestamp crawlerDate;

    public static FDroidApkInfo createInstance(String packageName, String version, String versionCode, String versionDate, String apkUrl, String apkHash, Timestamp crawlerDate){
        FDroidApkInfo fDroidApkInfo = new FDroidApkInfo();
        fDroidApkInfo.packageName = packageName;
        fDroidApkInfo.version = version;
        fDroidApkInfo.versionCode = versionCode;
        fDroidApkInfo.versionDate = versionDate;
        fDroidApkInfo.apkHash = apkHash;
        fDroidApkInfo.apkUrl = apkUrl;
        fDroidApkInfo.crawlerDate = crawlerDate;
        return fDroidApkInfo;
    }

    public static class DBHelper{
        private static boolean init_tested = false;
        public static synchronized void init () {
            try {
                if (! init_tested) {
                    createTableIfNotExists(DBUtil.getApkDBConnectionSource());
                    init_tested = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private static void createTableIfNotExists(ConnectionSource connectionSource) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, FDroidApkInfo.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static List<FDroidApkInfo> queryAllFDroidApkInfo(){
            init();
            try{
                Dao<FDroidApkInfo, String> dao = DaoManager.createDao(DBUtil.getApkDBConnectionSource(), FDroidApkInfo.class);

                return dao.queryForAll();

            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public static FDroidApkInfo queryFDroidApkInfo(String packageName, String versionCode){
            init();
            try{
                Dao<FDroidApkInfo, String> dao = DaoManager.createDao(DBUtil.getApkDBConnectionSource(), FDroidApkInfo.class);

                FDroidApkInfo resItem = dao.queryBuilder().where().eq("packageName", packageName).and().eq("versionCode", versionCode).queryForFirst();
                return resItem;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public static FDroidApkInfo queryFDroidApkInfo(String apkHash){
            init();
            try{
                Dao<FDroidApkInfo, String> dao = DaoManager.createDao(DBUtil.getApkDBConnectionSource(), FDroidApkInfo.class);
                FDroidApkInfo resItem = dao.queryBuilder().where().eq("apkHash", apkHash).queryForFirst();
                return resItem;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
