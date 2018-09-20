package cn.fudan.androiddb.crawler.crawlerEntity;

import cn.fudan.androiddb.crawler.Crawler;
import cn.fudan.androiddb.crawler.dao.AppInfoDao;
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
 * Created by qiaoying on 2017/7/20.
 */
public class HuaweiCrawler extends Crawler {
    Set<String> existingAppIdSet = new HashSet<>();
    public static final HashMap<Integer, String> CATEGORIES = new HashMap<>();
    public static final HashMap<Integer, String> SORT = new HashMap<>();

    static {
        CATEGORIES.put(23, "影音娱乐");
        CATEGORIES.put(24, "实用工具");
        CATEGORIES.put(26, "社交通讯");
        CATEGORIES.put(30, "教育");
        CATEGORIES.put(345, "新闻阅读");
        CATEGORIES.put(33, "拍摄美化");
        CATEGORIES.put(359, "美食");
        CATEGORIES.put(28, "出行导航");
        CATEGORIES.put(361, "旅游住宿");
        CATEGORIES.put(358, "购物比价");
        CATEGORIES.put(362, "商务");
        CATEGORIES.put(363, "儿童");
        CATEGORIES.put(25, "金融理财");
        CATEGORIES.put(31, "运动健康");
        CATEGORIES.put(27, "便捷生活");
        CATEGORIES.put(360, "汽车");
        CATEGORIES.put(29, "主题个性");

        SORT.put(0, "热门推荐");
        SORT.put(2, "综合评分");
        SORT.put(1, "更新时间");
    }

    @Override
    public void crawlerTopAppList(CrawlerTaskInfo taskInfo){

        for (int cid : CATEGORIES.keySet()) {
            for (int sid : SORT.keySet()) {
                try {
//                    System.setProperty("http.proxyHost", "192.168.5.1");
//                    System.setProperty("http.proxyPort", "1080");
                    Document doc = Jsoup.connect("http://appstore.huawei.com/soft/list_" + cid + "_" + sid)
                            .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13 ").get();

                    Elements htmlContent = doc.select("div .list-game-app").select(".dotline-btn").select(".nofloat");

                    //System.out.println(htmlContent);
                    for (Element element : htmlContent) {
                        Element appElement = element.getElementsByClass("app-btn").first();
                        Element appDownload = appElement.getElementsByTag("span").last();
                        String downloadCount = appDownload.text();
                        Element appElement2 = appElement.getElementsByTag("a").first();
                        String tmp = appElement2.attr("onclick");
                        tmp = tmp.substring(tmp.indexOf("(") + 1, tmp.lastIndexOf(")"));
                        //System.out.println(tmp);
                        String temp;
                        temp = tmp.substring(0, tmp.indexOf(","));
                        //System.out.println(temp);
                        String id = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
                        //System.out.println(id);
                        tmp = tmp.substring(temp.length() + 1);

                        temp = tmp.substring(0, tmp.indexOf(","));
                        //System.out.println(temp);
                        String appName = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
                        //System.out.println(appName);
                        tmp = tmp.substring(temp.length() + 1);

                        temp = tmp.substring(0, tmp.indexOf(","));
                        //System.out.println(temp);
                        String softrank = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
                        //System.out.println(softrank);
                        tmp = tmp.substring(temp.length() + 1);

                        temp = tmp.substring(0, tmp.indexOf(","));
                        //System.out.println(temp);
                        String number = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
                        //System.out.println(number);
                        tmp = tmp.substring(temp.length() + 1);

                        temp = tmp.substring(0, tmp.indexOf(","));
                        // System.out.println(temp);
                        String categoryName = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
                        //System.out.println(categoryName);
                        tmp = tmp.substring(temp.length() + 1);


                        temp = tmp.substring(0, tmp.indexOf(","));
                        //System.out.println(temp);
                        String appDownloadUrl = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//                        System.out.println(id);
//                        System.out.println(downloadCount);
//                        if (existingAppIdSet.contains(id))
//                            continue;

                        System.out.println("top app crawlered with id: " + id);
                        AppInfo appInfo = new AppInfo();
                        appInfo.setId(id);
                        appInfo.setAppName(appName);
                        appInfo.setCategoryName(categoryName);
                        appInfo.setAppDownUrl(appDownloadUrl);
                        appInfo.setAppDownCount(downloadCount);

                        try {
                            AppInfoDao.insert(appInfo, taskInfo.tableName);
                            //existingAppIdSet.add(id);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        CrawlerTaskInfoDao.updateCrawlerTaskInfoTopAppsCrawled(taskInfo.taskId);
//        AppBatchInfoDao.updateAppBatchInfo(taskInfo.topAppsTableName);
    }



    public void crawlerAllAppList(CrawlerTaskInfo taskInfo) {

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
//        //ArrayList<String> list = TextReader.readFileByLines(fileName);
////        System.setProperty("http.proxyHost", "192.168.5.1");
////        System.setProperty("http.proxyPort", "1080");
////        CreateSearchTable.createSearchTableIfNotExists(taskInfo.market + "_searchKeyword");
////        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
////        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
////                .setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13").build();
////
////        String html = "";
////        //int pageTotal = 0;
//        int i = 2959;
//        //int wordTotal = list.size();
//        while (i < 155073) {
//            AppInfoDao.updateIsSearched(i + 1,taskInfo.market + "_searchKeyword");
//            try {
//                Connection con = Jsoup.connect("http://app.hicloud.com/search/" + AppInfoDao.retrieveByKeywordId(i + 1,taskInfo.market));
//                con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//                con.header("Accept-Encoding", "gzip,deflate");
//                con.header("Accept-Language", "zh-CN,zh;q=0.8");
//                con.header("Accept-Encoding", "gzip,deflate");
//                con.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
//                con.header("Cookie", "PHPSESSID=v1t31anh1ku5291ve4pon0e4n59vd7c72uun04922cubicp5m8o1; cs6k_langid=zh_cn; _gat_UA-7728030-4=1; cs6k_2f6c74b2aa33f495a5477460e731939caf1da42f718802b1ac7a1bc32da79a7a=s%3A45%3A%22http%3A%2F%2Fapp.hicloud.com%2Fplugin%2Fappstore%2Fsearch%22%3B; __gahuawei=GA1.2.47233297.1508917370; __gahuawei_gid=GA1.2.614504483.1508917370");
//                //System.out.println(doc);
//                Document doc = con.get();
//                Element element1 = doc.getElementsByClass("unit-main").first();
//                Element element2 = element1.getElementsByClass("sres").first();
//                String str = element2.text();
//                str=str.trim();
//                String str2="";
//                if(str != null && !"".equals(str)){
//                    for(int j=0;j<str.length();j++){
//                        if(str.charAt(j)>=48 && str.charAt(j)<=57){
//                            str2+=str.charAt(j);
//                        }
//                    }
//
//                }
//                int total = Integer.parseInt(str2);
//
//                int page = 0;
//                while (page < total/24) {
//                    //HttpGet httpget = new HttpGet("http://app.hicloud.com/search/" + AppInfoDao.retrieveByKeywordId(i + 1,taskInfo.market) + "&page=" + page);
//                    //System.out.println(list.get(i));
////                    System.out.println(page);
////                    CloseableHttpResponse response = null;
////                    try {
////                        response = httpclient.execute(httpget); //拿到response
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
//                    //int status = response.getStatusLine().getStatusCode();
////                    if (status == 200) {
////                        HttpEntity entity = response.getEntity();
////                        try {
////                            html = EntityUtils.toString(entity, Charset.defaultCharset());
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
////                        try {
////                            EntityUtils.consume(entity);
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
//
//                     con = Jsoup.connect("http://app.hicloud.com/search/" + AppInfoDao.retrieveByKeywordId(i + 1,taskInfo.market) + "&page=" + page);
//                    con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//                    con.header("Accept-Encoding", "gzip,deflate");
//                    con.header("Accept-Language", "zh-CN,zh;q=0.8");
//                    con.header("Accept-Encoding", "gzip,deflate");
//                    con.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
//                    con.header("Cookie", "PHPSESSID=v1t31anh1ku5291ve4pon0e4n59vd7c72uun04922cubicp5m8o1; cs6k_langid=zh_cn; _gat_UA-7728030-4=1; cs6k_2f6c74b2aa33f495a5477460e731939caf1da42f718802b1ac7a1bc32da79a7a=s%3A45%3A%22http%3A%2F%2Fapp.hicloud.com%2Fplugin%2Fappstore%2Fsearch%22%3B; __gahuawei=GA1.2.47233297.1508917370; __gahuawei_gid=GA1.2.614504483.1508917370");
//                    //System.out.println(doc);
//                     doc = con.get();
//                        Elements htmlContent = doc.select("div .list-game-app").select(".dotline-btn").select(".nofloat");
//                        //System.out.println(htmlContent);
//                        for (Element element : htmlContent) {
//                            Element appElement = element.getElementsByClass("app-btn").first();
//                            Element appDownload = appElement.getElementsByTag("span").last();
//                            String downloadCount = appDownload.text();
//                            Element appElement2 = appElement.getElementsByTag("a").first();
//                            String tmp = appElement2.attr("onclick");
//                            tmp = tmp.substring(tmp.indexOf("(") + 1, tmp.lastIndexOf(")"));
//                            //System.out.println(tmp);
//                            String temp;
//                            temp = tmp.substring(0, tmp.indexOf(","));
//                            //System.out.println(temp);
//                            String id = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//                            //System.out.println(id);
//                            tmp = tmp.substring(temp.length() + 1);
//
//                            temp = tmp.substring(0, tmp.indexOf(","));
//                            //System.out.println(temp);
//                            String appName = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//                            //System.out.println(appName);
//                            tmp = tmp.substring(temp.length() + 1);
//
//                            temp = tmp.substring(0, tmp.indexOf(","));
//                            //System.out.println(temp);
//                            String softrank = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//                            //System.out.println(softrank);
//                            tmp = tmp.substring(temp.length() + 1);
//
//                            temp = tmp.substring(0, tmp.indexOf(","));
//                            //System.out.println(temp);
//                            String number = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//                            //System.out.println(number);
//                            tmp = tmp.substring(temp.length() + 1);
//
//                            temp = tmp.substring(0, tmp.indexOf(","));
//                            // System.out.println(temp);
//                            String categoryName = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//                            //System.out.println(categoryName);
//                            tmp = tmp.substring(temp.length() + 1);
//
//
//                            temp = tmp.substring(0, tmp.indexOf(","));
//                            //System.out.println(temp);
//                            String appDownloadUrl = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//
//
//                            //System.out.println("top app crawlered with id: " + id);
//                            AppInfo appInfo = new AppInfo();
//                            appInfo.setId(id);
//                            appInfo.setAppName(appName);
//                            appInfo.setCategoryName(categoryName);
//                            appInfo.setAppDownUrl(appDownloadUrl);
//                            appInfo.setAppDownCount(downloadCount);
//
//                            try {
//                                AppInfoDao.insert(appInfo, taskInfo.allAppsTableName);
//                                System.out.println(appInfo.getAppName());
//                            } catch (Exception e) {
//
//                            }
//
//
//                        }
//                    //}
//                    ++page;
//                }
//
//            } catch (Exception e) {
//
//
//            }
//
//
//            ++i;
//        }
        //Spider.create(new HuaweiPageProcessor()).addTask(taskInfo).addUrl("huawei").thread(40).run();
    }


//    private  void discoverMoreApps(Set<String> existingAppIdSet, String startAppId, CrawlerTaskInfo taskInfo){
//        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
//        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
//                .setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13").build();
//
//        String html = "";
//        String URL = "http://appstore.huawei.com:80/app/";
//        HttpGet httpget = new HttpGet(URL + startAppId);
//
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
//            html = EntityUtils.toString(entity, Charset.defaultCharset());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            EntityUtils.consume(entity);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Document doc = Jsoup.parse(html);
//
//        Elements aboutHtmlContent = doc.select("div.app-sweatch");
//        for (Element element : aboutHtmlContent) {
//            Element app = element.getElementsByClass("num").first();
//            String appDownload = app.text();
//            //System.out.println(appDownload);
//            Element aboutAppElement = element.getElementsByClass("sort").first();
//            Element aboutAppElement1 = aboutAppElement.getElementsByTag("a").first();
//            String tmp = aboutAppElement1.attr("onclick");
//            //System.out.println(tmp);
//            tmp = tmp.substring(tmp.indexOf("(") + 1, tmp.lastIndexOf(")"));
//            //System.out.println(tmp);
//            String temp;
//            temp = tmp.substring(0, tmp.indexOf(","));
//            //System.out.println(temp);
//            String id = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//            //System.out.println(id);
//            tmp = tmp.substring(temp.length() + 1);
//
//            temp = tmp.substring(0, tmp.indexOf(","));
//            //System.out.println(temp);
//            String appName = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//            //System.out.println(appName);
//            tmp = tmp.substring(temp.length() + 1);
//
//            temp = tmp.substring(0, tmp.indexOf(","));
//            //System.out.println(temp);
//            String softrank = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//            //System.out.println(softrank);
//            tmp = tmp.substring(temp.length() + 1);
//
//            temp = tmp.substring(0, tmp.indexOf(","));
//            //System.out.println(temp);
//            String number = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//            //System.out.println(number);
//            tmp = tmp.substring(temp.length() + 1);
//
//            temp = tmp.substring(0, tmp.indexOf(","));
//            // System.out.println(temp);
//            String categoryName = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//            //System.out.println(categoryName);
//            tmp = tmp.substring(temp.length() + 1);
//
//
//            temp = tmp.substring(0, tmp.indexOf(","));
//            //System.out.println(temp);
//            String appDownloadUrl = temp.substring(temp.indexOf("'") + 1, temp.lastIndexOf("'"));
//            System.out.println(id);
//
//            if (existingAppIdSet.contains(id))
//                continue;
//
//            System.out.println("discover new app with id:" + id);
//            AppInfo appInfo = new AppInfo();
//            appInfo.setId(id);
//            appInfo.setAppName(appName);
//            appInfo.setAppDownCount(appDownload);
//            appInfo.setCategoryName(categoryName);
//            appInfo.setAppDownUrl(appDownloadUrl);
//            try {
//                AppInfoDao.insert(appInfo, taskInfo.allAppsTableName);
//                existingAppIdSet.add(id);
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }




}
