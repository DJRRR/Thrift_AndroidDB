package cn.fudan.libdb.crawler.dao;


import cn.fudan.libdb.crawler.crawlerEntity.AppInfo;
import cn.fudan.libdb.crawler.util.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiaoying on 2017/8/10.
 */
public class AppInfoDao {
    public static void createTable(String tableName) throws SQLException {
        String sql = "CREATE TABLE "+tableName+"(id varchar(255) NOT NULL, "
        +"appName varchar(255) DEFAULT NULL, pkgName varchar(255) DEFAULT NULL, authorName varchar(255) DEFAULT NULL, "
        +"appDownCount varchar(255) DEFAULT NULL, versionName varchar(255) DEFAULT NULL, "
        +"apkMd5 varchar(255) DEFAULT NULL, categoryName varchar(255) DEFAULT NULL, "
        +"appSize varchar(255) DEFAULT NULL, appDownUrl TEXT DEFAULT NULL, apkDownloaded tinyint(1) DEFAULT NULL, "
        +"apkActualSize varchar(255) DEFAULT NULL, apkActualMd5 varchar(255) DEFAULT NULL, "
        +"apkActualPkgName varchar(255) DEFAULT NULL, apkStoreTag varchar(255) DEFAULT NULL, "
        +"apkStoreKey varchar(255) DEFAULT NULL, PRIMARY KEY (id))";
        DBHelper.executeSQL(sql);
    }
    public static void createSearchTable(String tableName) throws SQLException{
        String sql = "CREATE TABLE if not exists "+tableName+"(id int(11) NOT NULL, "
                +"keyword varchar(255) DEFAULT NULL, isSearched tinyint(1) DEFAULT NULL, PRIMARY KEY (id))";
        System.out.println(sql);
        DBHelper.executeSQL(sql);
    }

    public static void dropTable(String tableName) throws SQLException {
        String sql = "DROP TABLE "+tableName;
        DBHelper.executeSQL(sql);
    }

    public static int insert(AppInfo appInfo, String tableName) throws SQLException {
        String sql = "insert into " +tableName+"(id,appName,pkgName,authorName,appDownCount,versionName,"
        +"apkMd5,categoryName,appSize,appDownUrl,apkDownloaded,apkActualSize,apkActualMd5,"
        +"apkActualPkgName,apkStoreTag,apkStoreKey) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        return DBHelper.executeSQL(sql,
                appInfo.getId(), appInfo.getAppName(), appInfo.getPkgName(), appInfo.getAuthorName(),appInfo.getAppDownCount(), appInfo.getVersionName(),
                appInfo.getApkMd5(), appInfo.getCategoryName(), appInfo.getAppSize(), appInfo.getAppDownUrl(), appInfo.isApkDownloaded(),
                appInfo.getApkActualSize(), appInfo.getApkActualMd5(), appInfo.getApkActualPkgName(), appInfo.getApkStoreTag(), appInfo.getApkStoreKey());
    }
    public static int insertAuthorName(String authorName,String pkgName,String tableName) throws SQLException{
        String sql = "update "+tableName+" set appDownCount='"+authorName+"' where pkgName='"+pkgName+"'";
        System.out.println(sql);
        return DBHelper.executeSQL(sql);
    }
    public static int insertKeyword(int id, String keyword) throws SQLException{
        String sql = "insert into search_keywords2(id,keyword,isSearched) values (?,?,?)";
        System.out.println(sql);
        return DBHelper.executeSQL(sql,id,keyword,0);
    }

    public static int copyTable(String srcTableName, String dstTableName) throws SQLException {
        String sql = "insert into "+dstTableName+" select * from "+srcTableName;
        return DBHelper.executeSQL(sql);
    }
    public static int copyTableForSearch(String dstTableName) throws SQLException {
        String sql = "insert into "+dstTableName+" select * from search_keywords";
        System.out.println(sql);
        return  DBHelper.executeSQL(sql);
    }

    public static int count(String tableName){
        Connection con = DBHelper.getConnection();
        PreparedStatement sta = null;
        ResultSet rs = null;

        try {
            String sql = "select count(*) from "+tableName;
            sta = con.prepareStatement(sql);
            rs = sta.executeQuery();
            rs.first();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBHelper.close(rs,sta, con);
        }
    }

    public static List<String> selectAppIdList(String tableName) {
        List<String> idSet = new ArrayList<>();
        Connection con = null;
        con = DBHelper.getConnection();
        PreparedStatement sta = null;
        ResultSet rs = null;

        try {
            String sql = "select keyword from " + tableName+" ORDER BY id DESC ";
            sta = con.prepareStatement(sql);
            rs = sta.executeQuery();

            while(rs.next()) {
                idSet.add(rs.getString(1));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            DBHelper.close(rs,sta, con);
        }
        return idSet;
    }

    public static List<AppInfo> retrieve(String tableName) {
        List<AppInfo> list = new ArrayList<>();
        Connection con = null;
        con = DBHelper.getConnection();
        PreparedStatement sta = null;
        ResultSet rs = null;

        try {
            String sql = "select * from " + tableName;
            sta = con.prepareStatement(sql);
            rs = sta.executeQuery();

            while(rs.next()) {
                AppInfo appInfo = new AppInfo();
                appInfo.setId(rs.getString("id"));
                appInfo.setAppName(rs.getString("appName"));
                appInfo.setPkgName(rs.getString("pkgName"));
                appInfo.setAppDownCount(rs.getString("appDownCount"));
                appInfo.setVersionName(rs.getString("versionName"));
                appInfo.setApkMd5(rs.getString("apkMd5"));
                appInfo.setCategoryName(rs.getString("categoryName"));
                appInfo.setAppSize(rs.getString("appSize"));
                appInfo.setAppDownUrl(rs.getString("appDownUrl"));
                appInfo.setApkDownloaded(rs.getBoolean("apkDownloaded"));
                appInfo.setApkActualSize(rs.getString("apkActualSize"));
                appInfo.setApkActualMd5(rs.getString("apkActualPkgName"));
                appInfo.setApkActualPkgName(rs.getString("apkStoreTag"));
                appInfo.setApkStoreTag(rs.getString("apkStoreTag"));
                appInfo.setApkStoreKey(rs.getString("apkStoreKey"));

                list.add(appInfo);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            DBHelper.close(rs,sta, con);
        }
        return list;
    }

    public static String retrieveByKeywordId(int id,String market){
        String keyword = null;
        Connection con = null;
        con = DBHelper.getConnection();
        PreparedStatement sta = null;
        ResultSet rs = null;

        try {
            String sql = "select * from "+market+"_searchKeyword where id = "+ id;
            System.out.println(sql);
            sta = con.prepareStatement(sql);
            rs = sta.executeQuery();
            while(rs.next()){
                keyword = rs.getString("keyword");
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            DBHelper.close(rs,sta, con);
        }
        return keyword;
    }

    public static void updateIsSearched(int id,String tableName){
        String sql = "update "+tableName+" set isSearched=1 where id="+id;
        System.out.println(sql);
        try {
            DBHelper.executeSQL(sql);
        } catch (SQLException e) {

            e.printStackTrace();
        }

    }

    public static void updateIsSearched(String keyword, String marketName){

        String sql = "update "+marketName+"_searchKeyword "+"set isSearched=1 where keyword='"+keyword+"'";
        System.out.println(sql);
        try {
            DBHelper.executeSQL(sql);
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

//    public static void updateApkDownloaded(String tableName, ApkStorage.ApkStorageInfo apkStorageInfo, AppInfo appInfo) {
//        String sql = "update "+tableName
//                + " set apkDownloaded=1,"
//                + " apkActualMd5='" + apkStorageInfo.actualMd5+"',"
//                + " apkActualSize='" +apkStorageInfo.actualSize+"',"
//                + (apkStorageInfo.actualPkgName == null ? "" : " apkActualPkgName='" +apkStorageInfo.actualPkgName+"',")
//                + " apkStoreTag='" +apkStorageInfo.storeTag+"',"
//                + " apkStoreKey='" +apkStorageInfo.storeKey+"'"
//                + " where id='"+appInfo.getId()+"'";
//        System.out.println(sql);
//        try {
//            DBHelper.executeSQL(sql);
//        } catch (SQLException e) {
//            System.err.println(appInfo.getId());
//            e.printStackTrace();
//        }
//    }

    public static boolean hasApkDownloaded(String tableName) {
        Connection con = DBHelper.getConnection();
        PreparedStatement sta = null;
        ResultSet rs = null;

        try {
            String sql = "select count(*) from " + tableName +" where apkDownloaded is true";
            rs = con.prepareStatement(sql).executeQuery();

            int rowCount = 0;
            if(rs.next()) {
                rowCount= rs.getInt(1);
            }

            return rowCount == 0 ? false : true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            DBHelper.close(rs,sta, con);
        }

        return false;
    }



    public static void main(String[] args) {
//        Set<String> idSet = AppInfoDao.selectAppIdSet("apps_top_baidu_20170816");
//        for (String id : idSet)
//            System.out.println(id);


    }
}
