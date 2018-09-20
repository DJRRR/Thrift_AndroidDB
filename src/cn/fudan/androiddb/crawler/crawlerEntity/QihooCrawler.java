package cn.fudan.androiddb.crawler.crawlerEntity;

import cn.fudan.androiddb.crawler.Crawler;
import cn.fudan.androiddb.crawler.dao.AppInfoDao;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by qiaoying on 2017/7/26.
 */
public class QihooCrawler extends Crawler {

    public static final HashMap<Integer, String> CATEGORIES = new HashMap<Integer, String>();
    public static final HashMap<Integer, String> GAMES = new HashMap<Integer, String>();

    static {
        CATEGORIES.put(11, "系统安全");
        CATEGORIES.put(12, "通讯社交");
        CATEGORIES.put(14, "影音技术");
        CATEGORIES.put(15, "新闻阅读");
        CATEGORIES.put(16, "生活休闲");
        CATEGORIES.put(17, "办公商务");
        CATEGORIES.put(18, "主题壁纸");
        CATEGORIES.put(102139, "金融理财");
        CATEGORIES.put(102228, "摄影摄像");
        CATEGORIES.put(102230, "购物优惠");
        CATEGORIES.put(102231, "地图旅游");
        CATEGORIES.put(102232, "教育学习");
        CATEGORIES.put(102233, "健康医疗");

        GAMES.put(19, "休闲益智");
        GAMES.put(20, "动作冒险");
        GAMES.put(51, "体育竞速");
        GAMES.put(52, "飞行射击");
        GAMES.put(53, "经营策略");
        GAMES.put(54, "棋牌天地");
        GAMES.put(100451, "网络游戏");
        GAMES.put(101587, "角色扮演");
        GAMES.put(102238, "儿童游戏");
    }

    @Override
    public void crawlerTopAppList(CrawlerTaskInfo taskInfo) {
        Set<String> existingAppIdSet = new HashSet<>();
        for (int cid : CATEGORIES.keySet()) {

            int page = 1, finishPage = 50;
            while (page <= finishPage) {
                try {

                    CloseableHttpClient httpclient = HttpClients.createDefault();
                    String htmlString = "";

                    HttpGet httpget = new HttpGet("http://zhushou.360.cn/list/index/cid/" + cid + "/order/download/?page=" + page);
                    //HttpGet httpget = new HttpGet("http://zhushou.360.cn/list/index/cid/11/order/download/?page=1" );
                    CloseableHttpResponse response = null;
                    response = httpclient.execute(httpget); //拿到response

                    HttpEntity entity = response.getEntity();

                    htmlString = EntityUtils.toString(entity);
                    EntityUtils.consume(entity);


                    Document doc = Jsoup.parse(htmlString);
                    Element content = doc.getElementById("iconList");
                    Elements elements = content.getElementsByTag("li");
                    for (Element element : elements) {
                        Element name = element.getElementsByTag("h3").first();
                        String appName = name.text();

                        Element app = element.getElementsByTag("span").first();
                        String appDownCount = app.text();
                        Element link = element.getElementsByTag("a").last();
                        //System.out.println(link);
                        String id = link.attr("sid");
                        String href = link.attr("href");
                        String appDownUrl = href.substring(href.indexOf("url=") + 4);
                        System.out.println(appName);
                        String categoryName = CATEGORIES.get(cid);
                        if (existingAppIdSet.contains(id))
                            continue;

                        System.out.println("top app crawlered with id: " + id);
                        AppInfo appInfo = new AppInfo();
                        appInfo.setId(id);
                        appInfo.setAppName(appName);
                        appInfo.setAppDownCount(appDownCount);
                        appInfo.setAppDownUrl(appDownUrl);
                        appInfo.setCategoryName(categoryName);

                        try {
                            AppInfoDao.insert(appInfo, taskInfo.tableName);
                            existingAppIdSet.add(id);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                    }
                    ++page;
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }


        }
//        CrawlerTaskInfoDao.updateCrawlerTaskInfoTopAppsCrawled(taskInfo.taskId);
//        AppBatchInfoDao.updateAppBatchInfo(taskInfo.topAppsTableName);
    }

    //    public static void crawlGameList() throws IOException {
//        for (int cid : GAMES.keySet()) {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("applist/" + cid + ".txt"));
//            int page = 1;
//            while (page != 2) {
//                try {
//                    Document doc = Jsoup.connect("http://zhushou.360.cn/list/index/cid/" + cid + "/official/1/?page=" + page).get();
//                    Element content = doc.getElementById("iconList");
//                    Elements elements = content.getElementsByTag("li");
//                    for (Element element : elements) {
//                        /*Element name = element.getElementsByTag("h3").first();
//                        System.out.print(name.text());
//                        System.out.print('|');*/
//                        Element link = element.getElementsByTag("a").last();
//                        String href = link.attr("href");
//                        writer.write(href.substring(href.indexOf("url=") + 4));
//                        writer.newLine();
//                    }
//                    ++page;
//                } catch (IOException e) {
//                    System.err.println(e.getMessage());
//                }
//            }
//
//            int count = 0;
//            while (count != 1) {
//                try {
//                    Document doc = Jsoup.connect("http://zhushou.360.cn/list/index/cid/" + cid + "/official/1/?page=2").get();
//                    Element content = doc.getElementById("iconList");
//                    Elements elements = content.getElementsByTag("li");
//                    for (Element element : elements) {
//                        Element link = element.getElementsByTag("a").last();
//                        String href = link.attr("href");
//                        writer.write(href.substring(href.indexOf("url=") + 4));
//                        writer.newLine();
//                        ++count;
//                        if (count == 1)
//                            break;
//                    }
//                } catch (IOException e) {
//                    System.err.println(e.getMessage());
//                }
//            }
//
//            writer.close();
//        }
//    }



//    private static void discoverMoreApps(Set<String> existingAppIdSet, String startAppId, CrawlerTaskInfo taskInfo){
//
//        CloseableHttpClient httpclient = HttpClients.custom().setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13").build();
//        String html = "";
//        HttpGet httpget = new HttpGet("http://openbox.mobilem.360.cn/Guessyoulike/detail?softId=" + startAppId + "&start=0&count=30");
//        CloseableHttpResponse response = null;
//        try {
//            response = httpclient.execute(httpget); //拿到response
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        HttpEntity entity = response.getEntity();
//
//        try {
//            html = EntityUtils.toString(entity);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            EntityUtils.consume(entity);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        JSONObject jsonObject = new JSONObject(html);
//        JSONArray jsonArray = jsonObject.getJSONArray("apps");
//        //System.out.println(jsonArray);
//
//        for (int i = 0; i < 29; i++) {
//            JSONObject jsonObj = jsonArray.getJSONObject(i);
//            AppInfo appInfo1 = parseFromJSON(jsonObj);
//            if (existingAppIdSet.contains(appInfo1.getId()))
//                continue;
//            try {
//                AppInfoDao.insert(appInfo1, taskInfo.allAppsTableName);
//                existingAppIdSet.add(appInfo1.getId());
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void crawlerAllAppList(CrawlerTaskInfo taskInfo) {
//        //ArrayList<String> list = TextReader.readFileByLines(fileName);
//        CreateSearchTable.createSearchTableIfNotExists(taskInfo.market + "_searchKeyword");
//        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
//        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
//                .setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13").build();
//
//        String html = "";
//        int pageTotal = 0;
//        int i = 52688;
//        //int wordTotal = list.size();
//        while (i < 57461) {
//            AppInfoDao.updateIsSearched(i + 1,taskInfo.market + "_searchKeyword");
//            HttpGet httpget1 = new HttpGet("http://zhushou.360.cn/search/index/?kw=" + AppInfoDao.retrieveByKeywordId(i + 1,taskInfo.market));
//            //System.out.println(list.get(i));
//            CloseableHttpResponse response = null;
//            try {
//                response = httpclient.execute(httpget1); //拿到response
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int status = response.getStatusLine().getStatusCode();
//            if (status == 200) {
//                HttpEntity entity = response.getEntity();
//                try {
//                    html = EntityUtils.toString(entity, Charset.defaultCharset());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    EntityUtils.consume(entity);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//                try {
//                    Document doc = Jsoup.parse(html);
//                    //System.out.println(doc);
//                    //doc.getElementsByClass("no-result").first();
//                    Element htmlContent = doc.getElementsByClass("title_tr").first();
//                    //System.out.println(htmlContent);
//                    Element tmp = htmlContent.getElementsByTag("span").first();
//
//                    pageTotal = Integer.parseInt(tmp.text());
//
//
//                    System.out.println(pageTotal / 10);
//                    int page = 0;
//                    while(page < (pageTotal/15)){
//                        HttpGet httpget = new HttpGet("http://zhushou.360.cn/search/index/?kw="+AppInfoDao.retrieveByKeywordId(i + 1,taskInfo.market)+"&page="+page);
//                        //System.out.println(list.get(i));
//                        System.out.println(page);
//                        response = null;
//                        try {
//                            response = httpclient.execute(httpget); //拿到response
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        status = response.getStatusLine().getStatusCode();
//                        if (status == 200){
//                            entity = response.getEntity();
//                            try {
//                                html = EntityUtils.toString(entity, Charset.defaultCharset());
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            try {
//                                EntityUtils.consume(entity);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//                            doc = Jsoup.parse(html);
//                            //System.out.println(doc);
//                            htmlContent = doc.getElementsByClass("SeaCon").first();
//                            Elements elements = htmlContent.getElementsByTag("li");
//                            //System.out.println(elements);
//                            for (Element element : elements) {
//                                Element app1 = element.getElementsByTag("h3").first();
//                                Element app2 = app1.getElementsByTag("a").first();
//                                String appName = app2.attr("title");
//                                System.out.println(appName);
//                                Element app3 = element.getElementsByClass("sdlft").first();
//                                Element app4 = app3.getElementsByClass("downNum").first();
//                                String download = app4.text();
//                                Element app5 = element.getElementsByClass("download").first();
//                                Element app6 = app5.getElementsByTag("a").first();
//                                String id = app6.attr("sid");
//                                String appDownUrl = app6.attr("href");
//                                AppInfo appInfo = new AppInfo();
//                                //appInfo.setId(id);
//                                appInfo.setAppName(appName);
//                                appInfo.setAppDownUrl(appDownUrl);
//                                appInfo.setId(id);
//                                appInfo.setAppDownCount(download);
//
//
//                                try {
//                                    AppInfoDao.insert(appInfo, taskInfo.allAppsTableName);
//                                    System.out.println(appInfo.getAppName());
//
//                                } catch (Exception e) {
//
//                                }
//
//                            }
//                        }
//                        ++page;
//                    }
//
//                } catch (Exception e) {
//
//                }
//            }
//
//
//            ++i;
//        }
////        if (taskInfo.currentFetchAllIteration == 0) {
////            //将top的app作为种子
////            if (AppInfoDao.count(taskInfo.allAppsTableName) == 0) {
////                try {
////                    AppInfoDao.copyTable(taskInfo.topAppsTableName, taskInfo.allAppsTableName);
////                } catch (SQLException e) {
////                    e.printStackTrace();
////                }
////            }
////
////            else {
////                List<AppInfo> topApps = AppInfoDao.retrieve(taskInfo.topAppsTableName);
////                Set<String> existAllAppIdSet = AppInfoDao.selectAppIdSet(taskInfo.allAppsTableName);
////                for (AppInfo appInfo : topApps) {
////                    if (! existAllAppIdSet.contains(appInfo.getId())) {
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
////        while (taskInfo.currentFetchAllIteration < taskInfo.crawlerAllIteration){
////            Set<String> existingAppIdSet = AppInfoDao.selectAppIdSet(taskInfo.allAppsTableName);
////            List<AppInfo> list = AppInfoDao.retrieve(taskInfo.allAppsTableName);
////            Date startTime = new Date();
////            int startCount = list.size();
////
////            for(AppInfo appInfo : list) {
////                System.out.println("use this app to discover more: " + appInfo.getId());
////                discoverMoreApps(existingAppIdSet, appInfo.getId(), taskInfo);
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
        //Spider.create(new QihooPageProcessor()).addTask(taskInfo).addUrl("qihoo").thread(30).run();
    }


//    private static AppInfo parseFromJSON(JSONObject jsonObj){
//        AppInfo appInfo = new AppInfo();
//        appInfo.setId(jsonObj.getString("soft_id"));
//
//        appInfo.setAppName(jsonObj.getString("soft_name"));
//        appInfo.setPkgName(jsonObj.getString("pname"));
//        appInfo.setAppDownUrl(jsonObj.getString("download_urls"));
//        appInfo.setAppDownCount(jsonObj.getString("download_times"));
//        appInfo.setAppSize(jsonObj.getString("apk_sizes"));
//        appInfo.setApkMd5(jsonObj.getString("apk_md5"));
//        return appInfo;
//    }

}


















