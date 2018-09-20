package cn.fudan.androiddb.crawler.crawlerEntity;


import cn.fudan.androiddb.crawler.Crawler;
import cn.fudan.androiddb.crawler.dao.AppInfoDao;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by qiaoying on 2017/7/17.
 */
public class YingyongbaoCrawler extends Crawler {

    public static final HashMap<Integer, String> CATEGORIES = new HashMap<Integer, String>();
    public static final HashMap<Integer, String> GAMES = new HashMap<Integer, String>();
    public static final HashMap<Integer, String> url = new HashMap<>();

    static {
        CATEGORIES.put(100, "children");
        CATEGORIES.put(101, "music");
        CATEGORIES.put(102, "reading");
        CATEGORIES.put(103, "video");
        CATEGORIES.put(104, "camera");
        CATEGORIES.put(105, "amusement");
        CATEGORIES.put(106, "social");
        CATEGORIES.put(107, "life");
        CATEGORIES.put(108, "travel");
        CATEGORIES.put(109, "health");
        CATEGORIES.put(110, "news");
        CATEGORIES.put(111, "education");
        CATEGORIES.put(112, "navigation");
        CATEGORIES.put(113, "office");
        CATEGORIES.put(114, "financing");
        CATEGORIES.put(115, "tool");
        CATEGORIES.put(116, "communication");
        CATEGORIES.put(117, "system");
        CATEGORIES.put(118, "security");
        CATEGORIES.put(119, "desktop_wallpaper");
        CATEGORIES.put(122, "shopping");

        // games
        GAMES.put(121, "network");
        GAMES.put(144, "action");
        GAMES.put(146, "");
        GAMES.put(147, "casual");
        GAMES.put(148, "");
        GAMES.put(149, "");
        GAMES.put(151, "");
        GAMES.put(153, "");

        url.put(0,"MTA");
        url.put(1,"MjA");
        url.put(2,"MzA");
        url.put(3,"NDA");
        url.put(4,"NTA");
    }

    @Override
    public void crawlerTopAppList(CrawlerTaskInfo taskInfo){

        for (int cid: CATEGORIES.keySet()) {
            int pageContext = 0;
            while (pageContext != 4000){
                try {

                    CloseableHttpClient httpclient = HttpClients.createDefault();
                    HttpGet httpget = new HttpGet("http://sj.qq.com/myapp/cate/appList.htm?orgame=1&categoryId=" + cid + "&pageSize=20&pageContext=" +pageContext);
                    CloseableHttpResponse response = null;
                    response = httpclient.execute(httpget); //拿到response

                    HttpEntity entity = response.getEntity();

                    String htmlString = EntityUtils.toString(entity);
                    EntityUtils.consume(entity);
                    try {
                        parse(htmlString,taskInfo);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }

                pageContext = pageContext + 20;

            }
        }
        //CrawlerTaskInfoDao.updateCrawlerTaskInfoTopAppsCrawled(taskInfo.taskId);
    }


    private static AppInfo parseFromJSON(JSONObject jsonObj) {
        AppInfo appInfo = new AppInfo();
        appInfo.setId(jsonObj.getString("pkgName"));
        appInfo.setApkMd5(jsonObj.getString("apkMd5"));
        appInfo.setAppName(jsonObj.getString("appName"));
        appInfo.setPkgName(jsonObj.getString("pkgName"));
        appInfo.setAuthorName(jsonObj.getString("authorName"));
        appInfo.setAppSize(Integer.toString(jsonObj.getInt("fileSize")));
        int tmp = jsonObj.getInt("appDownCount");
        String appDownCount = Integer.toString(tmp);
        appInfo.setAppDownCount(appDownCount);
        appInfo.setAppDownUrl(jsonObj.getString("apkUrl"));
        appInfo.setVersionName(jsonObj.getString("versionName"));
        appInfo.setCategoryName(jsonObj.getString("categoryName"));

        return appInfo;
    }

    private static void parse(String htmlString,CrawlerTaskInfo taskInfo) throws IOException, SQLException {
        try {
            Set<String> existingAppIdSet = new HashSet<>();
            JSONObject jsonObject = new JSONObject(htmlString);

            int count = jsonObject.getInt("count");
            JSONArray jsonArray = jsonObject.getJSONArray("obj");
            for (int i = 0; i < count; ++i) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                AppInfo tmp = parseFromJSON(jsonObj);

                if (existingAppIdSet.contains(tmp.getId()))
                    continue;
                System.out.println("top app crawlered with id: " +tmp.getId());
                AppInfoDao.insert(tmp,taskInfo.tableName);
                existingAppIdSet.add(tmp.getId());
            }
        }catch (Exception e){

        }

    }

//    private void discoverMoreApps(Set<String> existingAppIdSet, String startAppId, CrawlerTaskInfo taskInfo){
//        try {
//            Document doc = Jsoup.connect("http://sj.qq.com/myapp/detail.htm?apkName="+ startAppId).get();
//            AppInfo appInfo = new AppInfo();
//            /**
//             * 第一个APP
//             */
//            Elements htmlContentFirst = doc.getElementsByClass("det-main-container");
//            //System.out.println(htmlContentFirst);
//            for (Element element : htmlContentFirst){
//                Element appElement = element.getElementsByClass("det-name-int").first();
//                //System.out.println(appElement);
//                //String appName = appElement.text();
//                //System.out.println(appName);
//                Element appElement2 = element.getElementsByClass("det-ins-num").first();
//                //System.out.println(appElement);
//                String appDownCount = appElement2.text();                                               //拿到下载量
//                //System.out.println(appDownCount);
//                Element appElement3 = element.getElementsByClass("det-type-box").first();
//                //System.out.println(appElement);
//                Element appElement4 = appElement3.getElementsByTag("a").first();
//                String appCategoryName = appElement4.text();                                            //拿到分类
//                //System.out.println(appCategoryName);
//                Element appElement5 = element.getElementsByClass("det-down-btn").first();
//                //System.out.println(appElement5);
//                //System.out.println(appElement5.attr("appname"));
//                String appName = appElement5.attr("appname");                              //拿到appname
//                String pkgname = appElement5.attr("apk");                                  //拿到pkgname
//                String appDownUrl = appElement5.attr("data-apkurl");
//                System.out.println(pkgname);
//
//                if (existingAppIdSet.contains(pkgname))
//                    continue;
//
//                System.out.println("discover new app with id:" + pkgname);
//
//
//                appInfo.setAppDownUrl(appDownUrl);
//                appInfo.setId(pkgname);
//                appInfo.setAppName(appName);
//                appInfo.setPkgName(pkgname);
//                appInfo.setAppDownCount(appDownCount);
//
//
//                try {
//                    AppInfoDao.insert(appInfo,taskInfo.allAppsTableName);
//                    existingAppIdSet.add(pkgname);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            /**
//             * 相关应用的4个APP
//             */
//            Elements htmlContent = doc.getElementsByClass("det-about-app-box");
//            //Elements elements = htmlContent.getElementsByTag("li");
//            //System.out.println(htmlContent);
//            for (Element element : htmlContent) {
//                Element aboutAppElement1 = element.getElementsByClass("app-right-data").first();
//                Element aboutAppElement2 = aboutAppElement1.getElementsByTag("a").last();
//                String appName = aboutAppElement2.attr("appname");
//                String pkgname = aboutAppElement2.attr("apk");
//                String appDownUrl = aboutAppElement2.attr("ex_url");
//
//                Element app3 = aboutAppElement1.getElementsByClass("down-times").first();
//                String aboutDownCount = app3.text();
//                //System.out.println(aboutDownCount);
//                System.out.println(pkgname);
//
//                if (existingAppIdSet.contains(pkgname))
//                    continue;
//
//                System.out.println("discover new app with id:" + pkgname);
//                appInfo.setId(pkgname);
//                appInfo.setAppDownUrl(appDownUrl);
//
//                appInfo.setAppName(appName);
//                appInfo.setPkgName(pkgname);
//                appInfo.setAppDownCount(aboutDownCount);
//                try {
//                    AppInfoDao.insert(appInfo,taskInfo.allAppsTableName);
//                    existingAppIdSet.add(pkgname);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            /**
//             * 同一开发者的4个APP
//             */
//            Elements htmlContent2 =doc.getElementsByClass("det-samedeve-app-box");
//            //System.out.println(htmlContent2);
//            for (Element element2 : htmlContent2) {
//                Element appElement2 = element2.getElementsByClass("app-right-data").first();
//                //System.out.println(appElement2);
//                Element samedeveAppElement1 = appElement2.getElementsByTag("a").last();
//
//                String appName = samedeveAppElement1.attr("appname");                              //拿到appname
//                String pkgname = samedeveAppElement1.attr("apk");                                  //拿到pkgname
//                String appDownUrl = samedeveAppElement1.attr("ex_url");
//
//                Element app3 = appElement2.getElementsByClass("down-times").first();
//                String samedeveDownCount = app3.text();
//                System.out.println(pkgname);
//
//                if (existingAppIdSet.contains(pkgname))
//                    continue;
//
//                System.out.println("discover new app with id:" + pkgname);
//
//
//                appInfo.setAppDownUrl(appDownUrl);
//                appInfo.setId(pkgname);
//                appInfo.setAppName(appName);
//                appInfo.setPkgName(pkgname);
//                appInfo.setAppDownCount(samedeveDownCount);
//
//                try {
//                    AppInfoDao.insert(appInfo,taskInfo.allAppsTableName);
//                    existingAppIdSet.add(pkgname);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    @Override
    public void crawlerAllAppList(CrawlerTaskInfo taskInfo) {

////        if (taskInfo.currentFetchAllIteration == 0) {
////            //将top的app作为种子
////            if (AppInfoDao.count(taskInfo.allAppsTableName) == 0) {
////                try {
////                    AppInfoDao.copyTable(taskInfo.topAppsTableName, taskInfo.allAppsTableName);
////                } catch (SQLException e) {
////                    e.printStackTrace();
////                }
////            } else {
////                List<AppInfo> topApps = AppInfoDao.retrieve(taskInfo.topAppsTableName);
////                Set<String> existAllAppIdSet = AppInfoDao.selectAppIdSet(taskInfo.allAppsTableName);
////                for (AppInfo appInfo : topApps) {
////                    if (!existAllAppIdSet.contains(appInfo.getId())) {
////                        try {
////                            AppInfoDao.insert(appInfo, taskInfo.allAppsTableName);
////                        } catch (SQLException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                }
////            }
////        }
////
////        taskInfo = CrawlerTaskInfoDao.getCrawlerTaskInfo(taskInfo.taskId);
////        while (taskInfo.currentFetchAllIteration < taskInfo.crawlerAllIteration) {
////            Set<String> existingAppIdSet = AppInfoDao.selectAppIdSet(taskInfo.allAppsTableName);
////            List<AppInfo> list = AppInfoDao.retrieve(taskInfo.allAppsTableName);
////            Date startTime = new Date();
////            int startCount = list.size();
////
////            for (AppInfo appInfo : list) {
////                System.out.println("use this app to discover more: " + appInfo.getId());
////                discoverMoreApps(existingAppIdSet, appInfo.getId(), taskInfo);            //应用宝商城将包名作为种子
////            }
////
////            int endCount = AppInfoDao.count(taskInfo.allAppsTableName);
////            Date endTime = new Date();
////
////            CrawlerTaskIterationInfo crawlerTaskIterationInfo = new CrawlerTaskIterationInfo();
////            crawlerTaskIterationInfo.setTaskId(taskInfo.taskId);
////            crawlerTaskIterationInfo.setIterationIndex(taskInfo.currentFetchAllIteration);
////            crawlerTaskIterationInfo.setStartTime(startTime);
////            crawlerTaskIterationInfo.setEndTime(endTime);
////            crawlerTaskIterationInfo.setFetchAllAppCountBeforeIteration(startCount);
////            crawlerTaskIterationInfo.setFetchAllAppCountAfterIteration(endCount);
////
////            CrawlerTaskIterationInfoDao.addCrawlerTaskIterationInfo(crawlerTaskIterationInfo);
////            CrawlerTaskInfoDao.updateCrawlerTaskCurrentFetchAllIteration(taskInfo.taskId, taskInfo.currentFetchAllIteration + 1);
////            taskInfo = CrawlerTaskInfoDao.getCrawlerTaskInfo(taskInfo.taskId);
////        }
////
////        CrawlerTaskInfoDao.updateCrawlerTaskInfoAllAppsCrawled(taskInfo.taskId);
////    }
//        CreateSearchTable.createSearchTableIfNotExists(taskInfo.market + "_searchKeyword");
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        String htmlString = "";
//        int i = 0;
//        //int wordTotal = list.size();
//        while (i < 155073) {
//            AppInfoDao.updateIsSearched(i + 1,taskInfo.market + "_searchKeyword");
//            for (int j = 0; j < 5; j++){
//                HttpGet httpget1 = new HttpGet("http://sj.qq.com/myapp/searchAjax.htm?kw=" + AppInfoDao.retrieveByKeywordId(i + 1,taskInfo.market)+"&pns="+url.get(j));
//                //HttpGet httpget1 = new HttpGet("http://sj.qq.com/myapp/searchAjax.htm?kw=幸福");
//                //System.out.println(list.get(i));
//                CloseableHttpResponse response = null;
//                try {
//                    response = httpclient.execute(httpget1); //拿到response
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                int status = response.getStatusLine().getStatusCode();
//                if (status == 200) {
//                    HttpEntity entity = response.getEntity();
//                    try {
//                        htmlString = EntityUtils.toString(entity);
//                        System.out.println(htmlString);
//                        EntityUtils.consume(entity);
//                        try {
//                            parseAll(htmlString,taskInfo);
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//
//         ++i;
//        }
        //Spider.create(new YingyongbaoPageProcessor()).addTask(taskInfo).addUrl("yingyongbao").thread(30).run();
    }


    public  void parseAll(String htmlString,CrawlerTaskInfo taskInfo) throws IOException, SQLException {
        try {
        JSONObject jsonObject = new JSONObject(htmlString);
        System.out.println(jsonObject);
        JSONObject jsonObject1 = jsonObject.optJSONObject("obj");
        JSONArray jsonArray = jsonObject1.getJSONArray("appDetails");
        System.out.println(jsonArray);

            for (int i = 0; i <10; ++i) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                AppInfo tmp = parseFromJSON(jsonObj);
                AppInfoDao.insert(tmp,taskInfo.tableName);
                System.out.println(tmp.getAppName());

            }

        }catch (Exception e){

        }

    }
}
