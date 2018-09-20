package cn.fudan.androiddb.crawler.crawlerEntity;

import cn.fudan.androiddb.crawler.Crawler;

import cn.fudan.androiddb.crawler.dao.AppInfoDao;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by qiaoying on 2017/11/30.
 */
public class AnzhiCrawler extends Crawler {

    public static final HashMap<Integer, Integer> CATEGORIES = new HashMap<Integer, Integer>();
    public static final HashMap<Integer, String> GAMES = new HashMap<Integer, String>();

    static {
//        CATEGORIES.put(49, "金融理财");
//        CATEGORIES.put(82, "电子书");
//        CATEGORIES.put(39, "系统工具");
//        CATEGORIES.put(40, "手机安全");
//        CATEGORIES.put(41, "浏览器");
//        CATEGORIES.put(42, "输入法");
//        CATEGORIES.put(43, "音乐音频");
//        CATEGORIES.put(44, "主题桌面");
//        CATEGORIES.put(45, "视频播放");
//        CATEGORIES.put(46, "摄影美化");
//        CATEGORIES.put(47, "气象交通");
//        CATEGORIES.put(48, "购物支付");
//        CATEGORIES.put(50, "综合服务");
//        CATEGORIES.put(51, "通信聊天");
//        CATEGORIES.put(52, "社交网络");
//        CATEGORIES.put(53, "新闻阅读");
//        CATEGORIES.put(54, "办公学习");
//        CATEGORIES.put(55, "阅读器");

        CATEGORIES.put(49, 569);
        CATEGORIES.put(82, 116);
        CATEGORIES.put(39, 539);
        CATEGORIES.put(40, 37);
        CATEGORIES.put(41, 22);
        CATEGORIES.put(42, 8);
        CATEGORIES.put(43, 190);
        CATEGORIES.put(44, 3682);
        CATEGORIES.put(45, 174);
        CATEGORIES.put(46, 152);
        CATEGORIES.put(47, 232);
        CATEGORIES.put(48, 897);
        CATEGORIES.put(50, 4106);
        CATEGORIES.put(51, 158);
        CATEGORIES.put(52, 575);
        CATEGORIES.put(53, 296);
        CATEGORIES.put(54, 1044);
        CATEGORIES.put(55, 20);

    }

    public static void parse(CrawlerTaskInfo taskInfo,String url){

        Document doc = null;
        try {

            Connection con = Jsoup.connect(url);
            con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            con.header("Accept-Encoding", "gzip,deflate");
            con.header("Accept-Language", "zh-CN,zh;q=0.8");
            con.header("Accept-Encoding", "gzip,deflate");
            con.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
            con.header("Cookie", "UM_distinctid=1600bc9c07a3b1-032bce17105ac3-6010107f-1fa400-1600bc9c07b2b7; PHPSESSID=0552b95c33800defee9019b67865a191; CNZZDATA3216547=cnzz_eid%3D1596345490-1512023566-null%26ntime%3D1512110056; Hm_lvt_b27c6e108bfe7b55832e8112042646d8=1512026260,1512091059,1512110231; Hm_lpvt_b27c6e108bfe7b55832e8112042646d8=1512110495; CKISP=8c40673b3a6d39471ebfad4d7889bc72%7C1512110498");
             doc = con.get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element htmlContent = doc.getElementsByClass("content").first();

            Element element = htmlContent.getElementsByClass("app_detail").first();
            Element element1 = element.getElementsByClass("detail_description").first();
            Element element3 = element1.getElementsByTag("h3").first();
            String appName = element3.text();
            Element element4 = element1.getElementsByClass("app_detail_version").first();
            String versionName = element4.text();
            versionName = versionName.substring(versionName.indexOf("(")+1, versionName.indexOf(")"));
            Element element6 = element1.getElementById("detail_line_ul");

            Element category = element6.getElementsByTag("li").first();

            String categoryName = category.text();
            System.out.println(categoryName);
            Element element5 = element1.getElementsByClass("spaceleft").get(1);
            String appSize = element5.text();
            appSize = appSize.substring(3);
            System.out.println(appSize);

            Element element2 = element.getElementsByClass("detail_down").first();
            Element link = element2.getElementsByTag("a").first();
            String url1 = link.attr("onclick");
            url1 = url1.substring(url1.indexOf("(")+1, url1.indexOf(")"));
            String url2 = "http://www.anzhi.com/dl_app.php?s="+url1+"&n=5";
            //System.out.println(url1);

        AppInfo appInfo = new AppInfo();
        appInfo.setId(url1);
        appInfo.setAppName(appName);
        //appInfo.setPkgName(pkgName);
        appInfo.setVersionName(versionName);
        appInfo.setAppSize(appSize);
        //appInfo.setAppDownCount(download);
        appInfo.setAppDownUrl(url2);
        appInfo.setCategoryName(categoryName);

        try {

                AppInfoDao.insert(appInfo, taskInfo.tableName);



        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public  void crawlerTopAppList(CrawlerTaskInfo taskInfo) {
//        for (int cid:CATEGORIES.keySet()){
//            for (int i = 1;i <= CATEGORIES.get(cid); i++){
//                //url.add("http://www.anzhi.com/sort_"+cid+"_"+i+"_hot.html");
//                try{
//                    Document doc = Jsoup.connect("http://www.anzhi.com/sort_"+cid+"_"+i+"_hot.html").userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13 ").get();
//
//                    Element htmlContent = doc.getElementsByClass("content").first();
//                    Elements elements = htmlContent.getElementsByTag("app_list");
//                    for (Element element : elements) {
//                        Element app2 = element.getElementsByTag("a").first();
//
//                    }
//                }catch (Exception e){
//
//                }
//
//            }
//        }
        for (int cid:CATEGORIES.keySet()){
            for (int i = 1;i <= CATEGORIES.get(cid); i++){

                try{
                    Connection con = Jsoup.connect("http://www.anzhi.com/sort_"+cid+"_"+i+"_hot.html").userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13 ");
                    con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                    con.header("Accept-Encoding", "gzip,deflate");
                    con.header("Accept-Language", "zh-CN,zh;q=0.8");
                    con.header("Accept-Encoding", "gzip,deflate");
                    con.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
                    con.header("Cookie", "UM_distinctid=1600bc9c07a3b1-032bce17105ac3-6010107f-1fa400-1600bc9c07b2b7; PHPSESSID=0552b95c33800defee9019b67865a191; CNZZDATA3216547=cnzz_eid%3D1596345490-1512023566-null%26ntime%3D1512110056; Hm_lvt_b27c6e108bfe7b55832e8112042646d8=1512026260,1512091059,1512110231; Hm_lpvt_b27c6e108bfe7b55832e8112042646d8=1512110495; CKISP=8c40673b3a6d39471ebfad4d7889bc72%7C1512110498");
                    Document doc = con.get();
                    Element htmlContent = doc.getElementsByClass("content").first();

                    Elements elements = htmlContent.getElementsByTag("li");
                    //System.out.println(elements);
                    for (Element element : elements) {
                        Element app = element.getElementsByTag("a").first();

                        String url="http://www.anzhi.com"+app.attr("href");
                        //System.out.println(url);
                        parse(taskInfo,url);

                    }
                }catch (Exception e){

                }

            }
        }


    }

    @Override
    public void crawlerAllAppList(CrawlerTaskInfo taskInfo) {
        for (int i = 1; i < 40; i++){
            try {
                Connection con = Jsoup.connect("http://www.anzhi.com/search.php?keyword="+ AppInfoDao.retrieveByKeywordId(i ,taskInfo.market)+"&page="+i);
                //System.out.println(doc);
                con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                con.header("Accept-Encoding", "gzip,deflate");
                con.header("Accept-Language", "zh-CN,zh;q=0.8");
                con.header("Accept-Encoding", "gzip,deflate");
                con.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
                con.header("Cookie", "UM_distinctid=1600bc9c07a3b1-032bce17105ac3-6010107f-1fa400-1600bc9c07b2b7; PHPSESSID=0552b95c33800defee9019b67865a191; CNZZDATA3216547=cnzz_eid%3D1596345490-1512023566-null%26ntime%3D1512110056; Hm_lvt_b27c6e108bfe7b55832e8112042646d8=1512026260,1512091059,1512110231; Hm_lpvt_b27c6e108bfe7b55832e8112042646d8=1512110495; CKISP=8c40673b3a6d39471ebfad4d7889bc72%7C1512110498");
                Document doc = con.get();
                Element htmlContent = doc.getElementsByClass("content").first();

                Elements elements = htmlContent.getElementsByTag("li");
                //System.out.println(elements);
                for (Element element : elements) {
                    Element app = element.getElementsByTag("a").first();

                    String url="http://www.anzhi.com"+app.attr("href");
                    //System.out.println(url);
                    parse(taskInfo,url);

                }
            } catch (Exception e) {

            }
        }


    }



}
