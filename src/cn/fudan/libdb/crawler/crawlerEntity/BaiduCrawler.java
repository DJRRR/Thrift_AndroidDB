package cn.fudan.libdb.crawler.crawlerEntity;

import cn.fudan.libdb.crawler.Crawler;
import cn.fudan.libdb.crawler.dao.AppInfoDao;
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
 * Created by qiaoying on 2017/7/27.
 */
public class BaiduCrawler extends Crawler {

    Set<String> existingAppIdSet = new HashSet<>();
    public static final HashMap<Integer, String> CATEGORIES = new HashMap<Integer, String>();
    public static final HashMap<Integer, String> GAMES = new HashMap<Integer, String>();

    static {
        CATEGORIES.put(501, "系统工具");
        CATEGORIES.put(502, "主题壁纸");
        CATEGORIES.put(503, "社交通讯");
        CATEGORIES.put(504, "生活实用");
        CATEGORIES.put(505, "资讯阅读");
        CATEGORIES.put(506, "影音播放");
        CATEGORIES.put(508, "拍摄美化");
        CATEGORIES.put(507, "办公学习");
        CATEGORIES.put(509, "旅游出行");
        CATEGORIES.put(510, "理财购物");
    }

    /**
     * 爬top信息的crawlerTopApps
     * @throws IOException
     */
    @Override
    public void crawlerTopAppList(CrawlerTaskInfo crawlerTaskInfo)  {
        System.out.println("执行crawler"+crawlerTaskInfo.market + "_"+crawlerTaskInfo.taskId);
        //crawlerIndexPage(taskInfo);
        for (int cid:CATEGORIES.keySet()) {
            int page = 1;
            while (page != 9) {
                try {
                      //获取首页的app
                    Document doc = Jsoup.connect("http://shouji.baidu.com/software/" + cid + "/list_" + page + ".html").userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13 ").get();

                    Element htmlContent = doc.getElementsByClass("app-bd").first();
                    Elements elements = htmlContent.getElementsByTag("li");
                    for (Element element : elements) {
                        Element app2 = element.getElementsByTag("a").first();
                        String id = app2.attr("href");
                        Element appElement1 = element.getElementsByClass("down-size").first();
                        Element app1 = appElement1.getElementsByTag("span").first();
                        String download = app1.text();
                        //System.out.println(app1.text());
                        Element appElement = element.getElementsByClass("down-btn").first();
                        Element app = appElement.getElementsByTag("span").first();
                        String appName = app.attr("data_name");
                        String pkgName = app.attr("data_package");
                        String versionName = app.attr("data_versionname");
                        String appDownUrl = app.attr("data_url");
                        String appSize = app.attr("data_size");
                        String categoryName = CATEGORIES.get(cid);

                        System.out.println("top app crawlered with id: " + id);
                        AppInfo appInfo = new AppInfo();
                        appInfo.setId(id);
                        appInfo.setAppName(appName);
                        appInfo.setPkgName(pkgName);
                        appInfo.setVersionName(versionName);
                        appInfo.setAppSize(appSize);
                        appInfo.setAppDownCount(download);
                        appInfo.setAppDownUrl(appDownUrl);
                        appInfo.setCategoryName(categoryName);

                        //输出app信息
                        try {
                            AppInfoDao.insert(appInfo, crawlerTaskInfo.tableName);
                            existingAppIdSet.add(id);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        System.out.println(appInfo.toString());
                    }
                    ++ page;
                }catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }

    }



//    private  void crawlerIndexPage(CrawlerTaskInfo taskInfo){
//
//        try {
//            Document doc = Jsoup.connect("http://shouji.baidu.com").get();
//            //System.out.println(doc);
//            Elements htmlContent = doc.getElementsByClass("app-bd");
//            Elements elements = htmlContent.select("li");
//            for (Element element :elements){
//                Element app2 = element.getElementsByTag("a").first();
//                String id = app2.attr("href");
//                Element appElement1 = element.getElementsByClass("down-size").first();
//                Element app1 = appElement1.getElementsByTag("span").first();
//                String download = app1.text();
//
//                Element appElement = element.getElementsByClass("down-btn").first();
//                Element app = appElement.getElementsByTag("span").first();
//                String appName = app.attr("data_name");
//                String pkgName = app.attr("data_package");
//                String versionName = app.attr("data_versionname");
//                String appDownUrl = app.attr("data_url");
//                String appSize = app.attr("data_size");
//                System.out.println(appName);
//                if (existingAppIdSet.contains(id))
//                    continue;
//
//                System.out.println("top app crawlered with id: " + id);
//                AppInfo appInfo = new AppInfo();
//                appInfo.setId(id);
//                appInfo.setAppName(appName);
//                appInfo.setPkgName(pkgName);
//                appInfo.setVersionName(versionName);
//                appInfo.setAppSize(appSize);
//                appInfo.setAppDownCount(download);
//                appInfo.setAppDownUrl(appDownUrl);
//
//                try {
//                    AppInfoDao.insert(appInfo, taskInfo.topAppsTableName);
//                    existingAppIdSet.add(id);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public void crawlerAllAppList(CrawlerTaskInfo crawlerTaskInfo) {

    }
}
