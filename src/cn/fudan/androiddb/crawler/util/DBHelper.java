package cn.fudan.androiddb.crawler.util;

import java.sql.*;

/**
 * Created by qiaoying on 2017/7/14.
 */
public class DBHelper {
    public static String driver = "com.mysql.jdbc.Driver";
    public static String url = "jdbc:mysql://10.141.209.138:6603/appcrawler?useSSL=false&autoReconnect=true";
    public static String user = "appcrawler";
    public static String password ="appcrawler";
//    public static String driver = "com.mysql.jdbc.Driver";
//    public static String url = "jdbc:mysql://localhost:3306/appcrawler?useSSL=false&autoReconnect=true";
//    public static String user = "root";
//    public static String password ="123456";

    public static Connection getConnection(){
        Connection con = null;

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);

        }catch (ClassNotFoundException e) {
                e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public static int executeSQL(String sql, Object...args) throws SQLException {
        Connection con = getConnection();
        con.setAutoCommit(false);
        PreparedStatement sta = null;
        int rows = 0;
        try {
            sta = con.prepareStatement(sql);
            for (int i = 0; i < args.length; i++){
                sta.setObject(i+1, args[i]);
            }

                rows = sta.executeUpdate();

            //rows = sta.executeUpdate();
            if (rows > 0){
                System.out.println("operate successfully");
            } else {
                System.out.println("fail!");
            }
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(sta , con);
        }

        return rows;
    }

    public static void close(ResultSet rs, PreparedStatement sta, Connection con) {
        try {
            if(rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(sta != null) {
                    sta.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void close(PreparedStatement sta, Connection con) {
        close(null, sta, con);
    }
}
