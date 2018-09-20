package cn.fudan.androiddb.crawler.crawlerEntity;

import cn.fudan.androiddb.crawler.Crawler;
import cn.fudan.androiddb.crawler.dao.AppInfoDao;
import cn.fudan.androiddb.crawler.dao.CrawlerTaskInfoDao;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MiCrawler extends Crawler {
    public static final HashMap<Integer, String> CATEGORIES = new HashMap<>();
    public static final HashMap<Integer, String> SORT = new HashMap<>();

    static {
        CATEGORIES.put(27, "影音视听");
        CATEGORIES.put(5, "实用工具");
        CATEGORIES.put(2, "聊天社交");
        CATEGORIES.put(12, "学习教育");
        CATEGORIES.put(7, "图书阅读");
        CATEGORIES.put(4, "居家生活");
        CATEGORIES.put(3, "旅行交通");
        CATEGORIES.put(9, "时尚购物");
        CATEGORIES.put(6, "摄影摄像");
        CATEGORIES.put(11, "新闻资讯");
        CATEGORIES.put(1, "金融理财");
        CATEGORIES.put(14, "医疗健康");
        CATEGORIES.put(10, "效率办公");
        CATEGORIES.put(8, "体育运动");
        CATEGORIES.put(13, "娱乐消遣");

        SORT.put(0, "热门推荐");
        SORT.put(2, "综合评分");
        SORT.put(1, "更新时间");
    }
    @Override
    public void crawlerTopAppList(CrawlerTaskInfo taskInfo) {
        for (int cid: CATEGORIES.keySet()) {
            int page = 0;
            while (page!= 100){
                try {

                    CloseableHttpClient httpclient = HttpClients.createDefault();
                    HttpGet httpget = new HttpGet("http://app.mi.com/categotyAllListApi?page="+page+"&categoryId="+cid+"&pageSize=30");
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

                ++page;

            }
        }
        CrawlerTaskInfoDao.updateCrawlerTaskInfoTopAppsCrawled(taskInfo.taskId);

    }

    @Override
    public void crawlerAllAppList(CrawlerTaskInfo taskInfo) {
//        //ArrayList<String> list = TextReader.readFileByLines(fileName);
////        CreateSearchTable.createSearchTableIfNotExists(taskInfo.market + "_searchKeyword");
////        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
////        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
////                .setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13").build();
////
////        String html = "";
//        int page = 1;
//        int i = 20699;
//        //int wordTotal = list.size();
//        //System.out.println(wordTotal);
//        while (i < 155073) {
//            AppInfoDao.updateIsSearched(i + 1,taskInfo.market + "_searchKeyword");
//            for (page = 1;page < 4;page++){
//                //System.out.println(list.get(i));
//                System.out.println(page);
////                HttpGet httpget = new HttpGet("http://app.mi.com/searchAll?keywords="+AppInfoDao.retrieveByKeywordId(i + 1,taskInfo.market)+"&typeall=phone&page="+page);
////                //HttpGet httpget = new HttpGet("http://app.mi.com/searchAll?keywords=幸福&typeall=phone&page="+page);
////
////                CloseableHttpResponse response = null;
////                try {
////                    response = httpclient.execute(httpget); //拿到response
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                int status = response.getStatusLine().getStatusCode();
////                if (status == 200) {
////                    HttpEntity entity = response.getEntity();
////                    try {
////                        html = EntityUtils.toString(entity, Charset.defaultCharset());
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    try {
////                        EntityUtils.consume(entity);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
//                    try {
//                        Connection con = Jsoup.connect("http://app.mi.com/searchAll?keywords="+AppInfoDao.retrieveByKeywordId(i + 1,taskInfo.market)+"&typeall=phone&page="+page);
//                        //System.out.println(doc);
//                        con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//                        con.header("Accept-Encoding", "gzip,deflate");
//                        con.header("Accept-Language", "zh-CN,zh;q=0.8");
//                        con.header("Accept-Encoding", "gzip,deflate");
//                        con.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
//                        con.header("Cookie", "JSESSIONID=aaaE-mtGXIlzrcv3iMK6v; __utma=127562001.443440490.1508907661.1508907661.1508907661.1; __utmb=127562001.3.10.1508907661; __utmc=127562001; __utmz=127562001.1508907661.1.1.utmcsr=baidu|utmccn=(organic)|utmcmd=organic");
//                        Document doc = con.get();
//                        Element htmlContent = doc.getElementsByClass("main").first();
//                        //System.out.println(htmlContent);
//                        Element app = htmlContent.getElementsByClass("container").first();
//                        //System.out.println(app);
//                        Element app2 = app.getElementsByClass("main-con").first();
//                        Element app3 = app2.getElementsByClass("applist").first();
//                        //System.out.println(app3);
//                        Elements elements = app3.getElementsByTag("li");
//                        //System.out.println(elements);
//                        for (Element element : elements) {
//                            Element app4 = element.getElementsByTag("a").first();
//                            String link = app4.attr("href");
//                            //System.out.println(link);
//                            MiCrawler.crawlerEach(link,taskInfo);
//
//
//                        }
//                    } catch (Exception e) {
//
//                    //}
//
//
//
//                }
//            }
//
//            ++i;
//        }
        //Spider.create(new MiPageProcessor()).addTask(taskInfo).addUrl("xiaomi").thread(30).run();

    }

    public static void crawlerEach(String link,CrawlerTaskInfo taskInfo){
        try{
            AppInfo appInfo = new AppInfo();
            try {
                Connection con = Jsoup.connect("http://app.mi.com" + link);

                con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                con.header("Accept-Encoding", "gzip,deflate");
                con.header("Accept-Language", "zh-CN,zh;q=0.8");
                con.header("Accept-Encoding", "gzip,deflate");
                con.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
                con.header("Cookie", "JSESSIONID=aaaE-mtGXIlzrcv3iMK6v; __utma=127562001.443440490.1508907661.1508907661.1508907661.1; __utmb=127562001.3.10.1508907661; __utmc=127562001; __utmz=127562001.1508907661.1.1.utmcsr=baidu|utmccn=(organic)|utmcmd=organic");
                Document doc = con.get();
                Element htmlContent = doc.getElementsByClass("main").first();
                Element app = htmlContent.getElementsByClass("app-intro").first();

                Element app2 = app.getElementsByClass("intro-titles").first();
//            Element app8 = app2.getElementsByClass("special-font").first();
//            String categoryName = app8.text();
                //System.out.println(categoryName);
                Element app3 = app2.getElementsByTag("h3").first();
                Element app8 = app2.getElementsByTag("p").first();
                Element app4 = app2.getElementsByClass("app-info-down").first();
                Element app5 = app4.getElementsByTag("a").first();
                String appName = app3.text();
                String authorName = app8.text();
                String appDownloadUrl = "http://app.mi.com"+app5.attr("href");


                Element app6 = app.getElementsByClass("details").first();
                Elements app7 = app6.getElementsByClass("cf");

                for (Element element : app7){
                    Element appElement1 = element.getElementsByTag("li").get(1);
                    String appSize = appElement1.text();
                    Element appElement2 = element.getElementsByTag("li").get(3);
                    String versionName = appElement2.text();
                    Element appElement3 = element.getElementsByTag("li").get(7);
                    String pkgName = appElement3.text();
                    Element appElement4 = element.getElementsByTag("li").get(9);
                    String id = appElement4.text();

                    appInfo.setId(id);
                    appInfo.setVersionName(versionName);
                    appInfo.setPkgName(pkgName);

                    appInfo.setAppSize(appSize);
                }



                //System.out.println("top app crawlered with id: " + id);
                appInfo.setAppName(appName);
                appInfo.setAuthorName(authorName);
                appInfo.setAppDownUrl(appDownloadUrl);
                //System.out.println(appInfo.getAppName());

                try {
                    AppInfoDao.insert(appInfo, taskInfo.tableName);
                    System.out.println(appInfo.getAppName());

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){

        }

    }


    private static AppInfo parseFromJSON(JSONObject jsonObj) {
        AppInfo appInfo = new AppInfo();
        int appid = jsonObj.getInt("appId");
        String id = Integer.toString(appid);
        appInfo.setId(id);
        //appInfo.setApkMd5(jsonObj.getString("apkMd5"));
        appInfo.setAppName(jsonObj.getString("displayName"));
        appInfo.setPkgName(jsonObj.getString("packageName"));
//        int tmp = jsonObj.getInt("appDownCount");
//        String appDownCount = Integer.toString(tmp);
//        appInfo.setAppDownCount(appDownCount);
        appInfo.setAppDownUrl("http://app.mi.com/download/"+id);
        //appInfo.setVersionName(jsonObj.getString("versionName"));
        appInfo.setCategoryName(jsonObj.getString("level1CategoryName"));

        return appInfo;
    }

    private static void parse(String htmlString,CrawlerTaskInfo taskInfo) throws IOException, SQLException {
        Set<String> existingAppIdSet = new HashSet<>();
        JSONObject jsonObject = new JSONObject(htmlString);

        int count = jsonObject.getInt("count");
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < 30; ++i) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                AppInfo tmp = parseFromJSON(jsonObj);
                if (existingAppIdSet.contains(tmp.getId()))
                    continue;
                System.out.println("top app crawlered with id: " +tmp.getId()+tmp.getCategoryName());
                AppInfoDao.insert(tmp,taskInfo.tableName);
                existingAppIdSet.add(tmp.getId());
            }
        }catch (Exception e){

        }
    }
}
