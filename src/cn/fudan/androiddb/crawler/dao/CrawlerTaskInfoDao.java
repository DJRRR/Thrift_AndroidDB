package cn.fudan.libdb.crawler.dao;

import cn.fudan.libdb.crawler.crawlerEntity.CrawlerTaskInfo;
import cn.fudan.libdb.crawler.util.OrmLiteHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author qiaoying
 * @date 2018/9/17 14:57
 */
public class CrawlerTaskInfoDao {
    private static boolean init_tested = false;
    public static synchronized void init () {
        try {
            if (! init_tested) {
                createTableIfNotExists(OrmLiteHelper.getConnectionSource());
                init_tested = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableIfNotExists(ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, CrawlerTaskInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addCrawlerTaskInfo(CrawlerTaskInfo crawlerTaskInfo) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            if (dao.queryBuilder().where().eq("taskId", crawlerTaskInfo.taskId).countOf() > 0) {
                return true;
            }
            else {
                dao.create(crawlerTaskInfo);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateCrawlerTaskInfoIterationCount(int taskId, int newIterationCount) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            UpdateBuilder<CrawlerTaskInfo, String> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue("crawlerAllIteration", newIterationCount);
            updateBuilder.where().eq("taskId", taskId);
            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean updateCrawlerTaskWorkerProcessStartCount(int taskId, int workerProcessStartCount) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            UpdateBuilder<CrawlerTaskInfo, String> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue("workerProcessStartCount", workerProcessStartCount);
            updateBuilder.where().eq("taskId", taskId);
            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean updateCrawlerTaskCurrentWorkTime(int taskId, long currentWorkTime) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            UpdateBuilder<CrawlerTaskInfo, String> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue("currentWorkTime", currentWorkTime);
            updateBuilder.where().eq("taskId", taskId);
            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean updateCrawlerTaskStartDate(int taskId, Date startDate) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            UpdateBuilder<CrawlerTaskInfo, String> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue("startDate", startDate);
            updateBuilder.where().eq("taskId", taskId);
            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean updateCrawlerTaskCurrentFetchAllIteration(int taskId, int currentIterationCount) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            UpdateBuilder<CrawlerTaskInfo, String> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue("currentFetchAllIteration", currentIterationCount);
            updateBuilder.where().eq("taskId", taskId);
            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean updateCrawlerTaskInfoTopAppsCrawled(int taskId) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            UpdateBuilder<CrawlerTaskInfo, String> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue("topAppsCrawled", true);
            updateBuilder.where().eq("taskId", taskId);
            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean updateCrawlerTaskInfoAllAppsCrawled(int taskId) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            UpdateBuilder<CrawlerTaskInfo, String> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue("allAppsCrawled", true);
            updateBuilder.where().eq("taskId", taskId);
            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean updateCrawlerTaskInfoTopAppsFetched(int taskId) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            UpdateBuilder<CrawlerTaskInfo, String> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue("topAppsFetched", true);
            updateBuilder.where().eq("taskId", taskId);
            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean updateCrawlerTaskInfoAllAppsFetched(int taskId) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            UpdateBuilder<CrawlerTaskInfo, String> updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue("allAppsFetched", true);
            updateBuilder.where().eq("taskId", taskId);
            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static int getTaskId(String tableName){
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            CrawlerTaskInfo result = dao.queryBuilder()
                    .where().eq("tableName", tableName)
                    .queryForFirst();
            if (result == null){
                System.out.println("查询为空");
            }

            if (result != null) {
                return result.taskId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static CrawlerTaskInfo getCrawlerTaskInfo(int taskId) {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            List<CrawlerTaskInfo> result = dao.queryBuilder()
                    .where().eq("taskId", taskId)
                    .query();
            if (result == null || result.size() == 0){
                System.out.println("查询为空");
            }

            if (result != null && result.size() > 0) {
                return result.get(0);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<CrawlerTaskInfo> getAllCrawlerTaskInfo() {
        init();

        try {
            Dao<CrawlerTaskInfo, String> dao = DaoManager.createDao(OrmLiteHelper.getConnectionSource(), CrawlerTaskInfo.class);

            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
