package cn.fudan.libdb.dao.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import cn.fudan.libdb.dao.util.DBUtil;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
@DatabaseTable(tableName = "maven_packages_parsed")
public class ParsedLibPackage {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField()
    public String groupName;
    @DatabaseField()
    public String artifactId;
    @DatabaseField()
    public String version;
    @DatabaseField()
    public String timestamp;
    @DatabaseField()
    public int dependencyAnalyze;
    @DatabaseField()
    public int downloadState;
    @DatabaseField(canBeNull = true)
    public String repo;//optional

    @DatabaseField(canBeNull = true)
    public String jarUrl;
    @DatabaseField(canBeNull = true)
    public String sourceJarUrl;
    @DatabaseField(canBeNull = true)
    public String pomUrl;//to get relations between GAVs


    public String toString() {
        return "" + repo + ":" + groupName + ":" + artifactId + ":" + version + ":" + timestamp + ":" + jarUrl + ":" + sourceJarUrl + ":" + pomUrl;
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
                TableUtils.createTableIfNotExists(connectionSource, ParsedLibPackage.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static List<ParsedLibPackage> queryAllParsedLibPackages() {
            init();

            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);

                return dao.queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static List<ParsedLibPackage> queryAllParsedLibPackages(String groupName, String artifactId) {
            init();

            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);

                return dao.queryBuilder()
                        .where().eq("groupName", groupName)
                        .and().eq("artifactId", artifactId).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }


        public static List<ParsedLibPackage> queryAlldependencyUnAnalyzed() {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);

                List<ParsedLibPackage> resList = dao.queryBuilder().where().eq("dependencyAnalyze", 0).query();
                return resList;

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static List<ParsedLibPackage> queryAllToDownload() {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);
                List<ParsedLibPackage> resList = dao.queryBuilder().where().eq("downloadState", 0).query();
                return resList;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static List<ParsedLibPackage> queryAllDownloadSuccess() {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);
                List<ParsedLibPackage> resList = dao.queryBuilder().where().eq("downloadState", 1).query();
                return resList;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static int getParsedPkgId(String groupName, String artifactId, String version) {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);
                ParsedLibPackage res = dao.queryBuilder().where().eq("groupName", groupName).
                        and().eq("artifactId", artifactId).
                        and().eq("version", version).queryForFirst();
                if (res == null) {
                    return -1;
                } else {
                    return res.id;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;
        }

        public static boolean updatedependencyAnalyzeState(int pkgId, int updateState) {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);
                UpdateBuilder<ParsedLibPackage, String> updateBuilder = dao.updateBuilder();
                updateBuilder.updateColumnValue("dependencyAnalyze", updateState);

                updateBuilder.where().eq("id", pkgId);
                updateBuilder.update();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }


        public static boolean updatedependencyAnalyzeState(String groupName, String artifactId, String version, int updateState) {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);
                UpdateBuilder<ParsedLibPackage, String> updateBuilder = dao.updateBuilder();
                updateBuilder.updateColumnValue("dependencyAnalyze", updateState);

                updateBuilder.where().eq("groupName", groupName)
                        .and().eq("artifactId", artifactId)
                        .and().eq("version", version);
                updateBuilder.update();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public static boolean updatedownloadState(String groupName, String artifactId, String version, int downloadState) {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);
                UpdateBuilder<ParsedLibPackage, String> updateBuilder = dao.updateBuilder();
                updateBuilder.updateColumnValue("downloadState", downloadState);

                updateBuilder.where().eq("groupName", groupName)
                        .and().eq("artifactId", artifactId)
                        .and().eq("version", version);
                updateBuilder.update();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }


        public static int getdownloadState(ParsedLibPackage pkg){
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);
                ParsedLibPackage res = dao.queryBuilder().where().eq("groupName", pkg.groupName)
                        .and().eq("artifactId", pkg.artifactId)
                        .and().eq("version", pkg.version).queryForFirst();
                return res.downloadState;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -2;
        }

        public static long countVersionNum(String groupName, String artifactId) {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);
                long versionCount = dao.queryBuilder().where().eq("groupName", groupName)
                        .and().eq("artifactId", artifactId).countOf();
                return versionCount;

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;
        }

        public static int getdependencyAnalyzeState(ParsedLibPackage pkg) {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);

                ParsedLibPackage res = dao.queryBuilder().where().eq("groupName", pkg.groupName)
                        .and().eq("artifactId", pkg.artifactId)
                        .and().eq("version", pkg.version).queryForFirst();
                return res.dependencyAnalyze;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -2;
        }

        public static boolean addParsedLibPackage(ParsedLibPackage pkg) {
            init();

            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);

                dao.create(pkg);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public static boolean hasParsedLibPackage(ParsedLibPackage pkg) {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);

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

        public static ParsedLibPackage getParsedLibPackage(List<ParsedLibPackage> ParsedLibPackages, String groupName, String artifactId, String version) {
            for (ParsedLibPackage ParsedLibPackage : ParsedLibPackages) {
                if (ParsedLibPackage.groupName.equals(groupName)
                        && ParsedLibPackage.artifactId.equals(artifactId)
                        && ParsedLibPackage.version.equals(version))
                    return ParsedLibPackage;
            }

            return null;
        }

        public static boolean updateParsedLibPackage(ParsedLibPackage pkg) {
            init();
            try {
                Dao<ParsedLibPackage, String> dao = DaoManager.createDao(DBUtil.getLibDBConnectionSource(), ParsedLibPackage.class);

                UpdateBuilder<ParsedLibPackage, String> updateBuilder = dao.updateBuilder();
                updateBuilder.updateColumnValue("jarUrl", pkg.jarUrl)
                        .updateColumnValue("sourceJarUrl", pkg.sourceJarUrl)
                        .updateColumnValue("pomUrl", pkg.pomUrl)
                        .updateColumnValue("timestamp", pkg.timestamp)
                        .updateColumnValue("repo", pkg.repo);

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
    }
}

