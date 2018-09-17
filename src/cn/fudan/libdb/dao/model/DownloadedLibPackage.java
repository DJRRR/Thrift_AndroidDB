package cn.fudan.libdb.dao.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.fudan.libdb.dao.util.DBUtil;
import org.sqlite.core.DB;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
@DatabaseTable(tableName = "maven_packages_downloaded")
public class DownloadedLibPackage {
    @DatabaseField() public String groupName;
    @DatabaseField() public String artifactId;
    @DatabaseField() public String version;
    @DatabaseField() public String timestamp;
    @DatabaseField(canBeNull = true) public String repo;//optional

    @DatabaseField() public String downloadTimestamp;
    @DatabaseField(canBeNull = true) public String jarFileMD5;
    @DatabaseField(canBeNull = true) public String dexFileMD5;
    @DatabaseField(canBeNull = true) public String sourceFileMD5;

    public static DownloadedLibPackage createFromParsedLibPackage(ParsedLibPackage pkg) {
        DownloadedLibPackage downloadedMavenPackage = new DownloadedLibPackage();
        downloadedMavenPackage.groupName = pkg.groupName;
        downloadedMavenPackage.artifactId = pkg.artifactId;
        downloadedMavenPackage.version = pkg.version;
        downloadedMavenPackage.timestamp = pkg.timestamp;
        downloadedMavenPackage.repo = pkg.repo;
        return downloadedMavenPackage;
    }

    public String getLibKey() {
        return groupName+":"+artifactId;
    }

    public String getLibSig() {
        return groupName+":"+artifactId+":"+version;
    }

    public String toString() {
        return ""+repo+":"+groupName+":"+artifactId+":"+version+":"+timestamp+":"+jarFileMD5+":"+dexFileMD5+":"+sourceFileMD5;
    }

    public String toGeneralString(){
        return "" + repo + ":" + groupName + ":" + artifactId + ":" + version + ":" + jarFileMD5 + ":" + dexFileMD5;
    }

    public String toDexString(){
        if(dexFileMD5 == null || dexFileMD5.length() == 0){
            return null;
        }
        return "" + repo + ":" + groupName + ":" + artifactId + ":" + version + ":" + dexFileMD5;
    }

    public String toJarString(){
        if(jarFileMD5 == null || jarFileMD5.length() == 0){
            return null;
        }
        return "" + repo + ":" + groupName + ":" + artifactId + ":" + version + ":" + jarFileMD5;
    }


    public static class DBHelper {
        private static boolean init_tested = false;

        public static synchronized void init() {
            try {
                if (!init_tested) {
                    createTableIfNotExists(DBUtil.getLibDBConnectionSource());
                    init_tested = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private static void createTableIfNotExists(ConnectionSource connectionSource) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, DownloadedLibPackage.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static List<DownloadedLibPackage> queryAllDownloadedLibPackage() {
            init();

            try {
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                List<DownloadedLibPackage> allPkgs = dao.queryForAll();
                return allPkgs;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static List<DownloadedLibPackage> queryDownloadedLibPackage(String groupName, String artifactId){
            init();
            try{
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                List<DownloadedLibPackage> allPkgs = dao.queryBuilder().where().eq("groupName",groupName).and().eq("artifactId",artifactId).query();

                return allPkgs;

            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public static List<DownloadedLibPackage> queryDownloadedLibPackageEliminate(String groupName, String artifactId, String version){
            if(groupName != null && artifactId != null && version != null){
                return queryDownloadedLibPackageByGAV(groupName, artifactId, version);
            }
            else if(groupName != null && artifactId != null && version == null){
                return queryDownloadedLibPackageByGA(groupName, artifactId);
            }
            else if(groupName != null && artifactId == null && version != null){
                return queryDownloadedLibPackageByGV(groupName, version);
            }
            else if(groupName != null && artifactId == null && version == null){
                return queryDownloadedLibPackageByG(groupName);
            }
            else if(groupName == null && artifactId != null && version != null){
                return queryDownloadedLibPackageByAV(artifactId, version);
            }
            else if(groupName == null && artifactId != null && version == null){
                return queryDownloadedLibPackageByA(artifactId);
            }
            else if(groupName == null && artifactId == null && version != null){
                return queryDownloadedLibPackageByV(version);
            }
            else{
                return null;
            }
        }

        public static List<DownloadedLibPackage> queryDownloadedLibPackageByGA(String groupName, String artifactId){
            init();
            try{
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                List<DownloadedLibPackage> allPkgs = dao.queryBuilder().where().eq("groupName",groupName).and().eq("artifactId",artifactId).query();

                return allPkgs;

            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public static List<DownloadedLibPackage> queryDownloadedLibPackageByGV(String groupName, String version){
            init();
            try{
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                List<DownloadedLibPackage> allPkgs = dao.queryBuilder().where().eq("groupName",groupName).and().eq("version",version).query();

                return allPkgs;

            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public static List<DownloadedLibPackage> queryDownloadedLibPackageByAV(String artifactId, String version){
            init();
            try{
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                List<DownloadedLibPackage> allPkgs = dao.queryBuilder().where().eq("version",version).and().eq("artifactId",artifactId).query();

                return allPkgs;

            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public static List<DownloadedLibPackage> queryDownloadedLibPackageByGAV(String groupName, String artifactId, String version) {
            init();

            try {
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                List<DownloadedLibPackage> result = dao.queryBuilder()
                        .where().eq("groupName", groupName)
                        .and().eq("artifactId", artifactId)
                        .and().eq("version", version).query();
                return result;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static List<DownloadedLibPackage> queryDownloadedLibPackageByG(String groupName){
            init();
            try{
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                List<DownloadedLibPackage> result = dao.queryBuilder().where().eq("groupName",groupName).query();
                return result;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public static List<DownloadedLibPackage> queryDownloadedLibPackageByA(String artifactId){
            init();
            try{
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                List<DownloadedLibPackage> result = dao.queryBuilder().where().eq("artifactId", artifactId).query();
                return result;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public static List<DownloadedLibPackage> queryDownloadedLibPackageByV(String version){
            init();
            try{
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                List<DownloadedLibPackage> result = dao.queryBuilder().where().eq("version",version).query();
                return result;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public static boolean addDownloadedLibPackage(DownloadedLibPackage pkg) {
            init();

            try {
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                dao.create(pkg);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public static boolean hasDownloadedLibPackage(DownloadedLibPackage pkg) {
            init();

            try {
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                if (dao.queryBuilder()
                        .where().eq("groupName", pkg.groupName)
                        .and().eq("artifactId", pkg.artifactId)
                        .and().eq("version", pkg.version).countOf() > 0) {
                    return true;
                }

                return false;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }

        public static DownloadedLibPackage getDownloadedLibPackage(String libSig){
            String[] libSigSplit = libSig.split(":");
            String group = libSigSplit[0];
            String artifact = libSigSplit[1];
            String version = libSigSplit[2];
            return getDownloadedLibPackage(group, artifact, version);
        }


        public static DownloadedLibPackage getDownloadedLibPackage(String groupName, String artifactId, String version) {
            init();

            try {
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                List<DownloadedLibPackage> result = dao.queryBuilder()
                        .where().eq("groupName", groupName)
                        .and().eq("artifactId", artifactId)
                        .and().eq("version", version).query();
                if (result != null && result.size() > 0) {
                    return result.get(0);
                }

                return null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static boolean updateDownloadedLibPackage(DownloadedLibPackage pkg) {
            init();

            try {
                Dao<DownloadedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), DownloadedLibPackage.class);

                UpdateBuilder<DownloadedLibPackage, String> updateBuilder = dao.updateBuilder();
                updateBuilder.updateColumnValue("jarFileMD5", pkg.jarFileMD5)
                        .updateColumnValue("dexFileMD5", pkg.dexFileMD5)
                        .updateColumnValue("sourceFileMD5", pkg.sourceFileMD5)
                        .updateColumnValue("downloadTimestamp", pkg.downloadTimestamp);

                updateBuilder.where().eq("groupName", pkg.groupName)
                        .and().eq("artifactId", pkg.artifactId)
                        .and().eq("version", pkg.version);
                updateBuilder.update();

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }

        //TODO: optimize the efficiency of sql operation
        public static List<String> queryResToGeneralStr(List<DownloadedLibPackage> queryRes){
            if(queryRes == null){
                return null;
            }
            List<String> generalRes = new ArrayList<>();
            for(int i = 0; i < queryRes.size(); i++){
                generalRes.add(queryRes.get(i).toGeneralString());
            }
            return generalRes;
        }

        public static List<String> queryResToJarStr(List<DownloadedLibPackage> queryRes){
            if(queryRes == null){
                return null;
            }
            List<String> jarRes = new ArrayList<>();
            for(int i = 0; i < queryRes.size(); i++){
                if(queryRes.get(i).jarFileMD5 == null || queryRes.get(i).jarFileMD5.length() == 0){
                    continue;
                }
                jarRes.add(queryRes.get(i).toJarString());
            }
            return jarRes;
        }

        public static List<String> queryResToDexStr(List<DownloadedLibPackage> queryRes){
            if(queryRes == null){
                return null;
            }
            List<String> dexRes = new ArrayList<>();
            for(int i = 0; i < queryRes.size(); i++){
                if(queryRes.get(i).dexFileMD5 == null || queryRes.get(i).dexFileMD5.length() < 1){
                    continue;
                }
                dexRes.add(queryRes.get(i).toDexString());
            }
            return dexRes;
        }

        public static String queryResToGeneralJson(List<DownloadedLibPackage> queryRes){
            if(queryRes == null){
                return null;
            }
            List<LibPackageGeneral> generalList = new ArrayList<>();
            for(int i = 0; i < queryRes.size(); i++){
                generalList.add(new LibPackageGeneral(queryRes.get(i)));
            }
            Gson gson = new Gson();
            return gson.toJson(generalList);
        }

        public static String queryResToJarJson(List<DownloadedLibPackage> queryRes){
            if(queryRes == null){
                return null;
            }
            List<LibPackageJar> jarList = new ArrayList<>();
            for(int i = 0; i < queryRes.size(); i++){
                jarList.add(new LibPackageJar(queryRes.get(i)));
            }
            Gson gson = new Gson();
            return gson.toJson(jarList);
        }

        public static String queryResToDexJson(List<DownloadedLibPackage> queryRes){
            if(queryRes == null){
                return null;
            }
            List<LibPackageDex> dexList = new ArrayList<>();
            for(int i = 0; i < queryRes.size(); i++){
                dexList.add(new LibPackageDex(queryRes.get(i)));
            }
            Gson gson = new Gson();
            return gson.toJson(dexList);
        }

    }

    public static void main(String[] args){
        List<DownloadedLibPackage> test = DBHelper.queryDownloadedLibPackage("com.android.support","design");
        System.out.println(DBHelper.queryResToGeneralJson(test));
    }

}
