package cn.fudan.androiddb.crawler.util;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Created by yuanxzhang on 06/08/2017.
 */
public class OrmLiteHelper {
    public static final String DATEBASE_APP_CRAWLER_URL = "jdbc:mysql://10.141.209.138:6603/appcrawler?useSSL=false&autoReconnect=true";
    public static final String DATEBASE_APP_CRAWLER_USERNAME = "appcrawler";
    public static final String DATEBASE_APP_CRAWLER_PASSWORD = "appcrawler";

    private static ConnectionSource connectionSource = null;

    public static ConnectionSource getConnectionSource() throws SQLException {
        if (connectionSource != null)
            return connectionSource;
        else {
            synchronized (OrmLiteHelper.class) {
                connectionSource = new JdbcPooledConnectionSource(DATEBASE_APP_CRAWLER_URL);
                ((JdbcConnectionSource) connectionSource).setUsername(DATEBASE_APP_CRAWLER_USERNAME);
                ((JdbcConnectionSource) connectionSource).setPassword(DATEBASE_APP_CRAWLER_PASSWORD);
            }
            return connectionSource;
        }
    }
}
