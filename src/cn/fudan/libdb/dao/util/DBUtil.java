package cn.fudan.libdb.dao.util;

import cn.fudan.libdb.LibDBConfig;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class DBUtil {

    private static ConnectionSource connectionSource = null;

    public static ConnectionSource getConnectionSource() throws SQLException {
        if (connectionSource != null)
            return connectionSource;
        else {
            synchronized (DBUtil.class) {
                connectionSource = new JdbcConnectionSource(LibDBConfig.getConfig(LibDBConfig.PROP_KEY_DATABASE_URL));
                ((JdbcConnectionSource) connectionSource).setUsername(LibDBConfig.getConfig(LibDBConfig.PROP_KEY_DATABASE_USERNAME));
                ((JdbcConnectionSource) connectionSource).setPassword(LibDBConfig.getConfig(LibDBConfig.PROP_KEY_DATABASE_PASSWORD));
            }
            return connectionSource;
        }
    }

}

