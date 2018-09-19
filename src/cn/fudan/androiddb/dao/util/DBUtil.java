package cn.fudan.androiddb.dao.util;

import cn.fudan.androiddb.AndroidDBConfig;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class DBUtil {

    private static ConnectionSource libDBConnectionSource = null;
    private static ConnectionSource apkDBConnectionSource = null;


    public static ConnectionSource getLibDBConnectionSource() throws SQLException {
        if (libDBConnectionSource != null)
            return libDBConnectionSource;
        else {
            synchronized (DBUtil.class) {
                libDBConnectionSource = new JdbcConnectionSource(AndroidDBConfig.getConfig(AndroidDBConfig.PROP_KEY_LIBDATABASE_URL));
                ((JdbcConnectionSource) libDBConnectionSource).setUsername(AndroidDBConfig.getConfig(AndroidDBConfig.PROP_KEY_LIBDATABASE_USERNAME));
                ((JdbcConnectionSource) libDBConnectionSource).setPassword(AndroidDBConfig.getConfig(AndroidDBConfig.PROP_KEY_LIBDATABASE_PASSWORD));
            }
            return libDBConnectionSource;
        }
    }

    public static ConnectionSource getApkDBConnectionSource() throws SQLException{
        if(apkDBConnectionSource != null)
            return apkDBConnectionSource;
        else{
            synchronized (DBUtil.class){
                apkDBConnectionSource = new JdbcConnectionSource(AndroidDBConfig.getConfig(AndroidDBConfig.PROP_KEY_APKDATABASE_URL));
                ((JdbcConnectionSource) apkDBConnectionSource).setUsername(AndroidDBConfig.getConfig(AndroidDBConfig.PROP_KEY_APKDATABASE_USERNAME));
                ((JdbcConnectionSource) apkDBConnectionSource).setPassword(AndroidDBConfig.getConfig(AndroidDBConfig.PROP_KEY_APKDATABASE_PASSWORD));
            }
            return apkDBConnectionSource;
        }
    }

}

