package cn.fudan.libdb.crawler;

import cn.fudan.libdb.crawler.crawlerEntity.BaiduCrawler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author qiaoying
 * @date 2018/9/16 14:18
 */
public class CrawlerFactory {

    public final static String CRAWLER_BAIDU = "baidu";
    public final static String CRAWLER_QIHOO = "qihoo";
    public final static String CRAWLER_YINGYONGBAO = "yingyongbao";
    public final static String CRAWLER_HUAWEI = "huawei";
    public final static String CRAWLER_GP = "gp";
    public final static String CRAWLER_FDROID = "fdroid";
    public final static String CRAWLER_XIAOMI = "xiaomi";
    public final static String CRAWLER_WANDOUJIA = "wandoujia";
    public final static String CRAWLER_ANZHI = "anzhi";
    public final static String CRAWLER_PP = "PP";
    public final static String CRAWLER_APPSZOOM = "appszoom";


    private static Set<String> ALL_SUPPORT_MARKET = new HashSet<>();
    static {
        ALL_SUPPORT_MARKET.add(CRAWLER_QIHOO);
        ALL_SUPPORT_MARKET.add(CRAWLER_BAIDU);
        ALL_SUPPORT_MARKET.add(CRAWLER_HUAWEI);
        ALL_SUPPORT_MARKET.add(CRAWLER_YINGYONGBAO);
        ALL_SUPPORT_MARKET.add(CRAWLER_GP);
        ALL_SUPPORT_MARKET.add(CRAWLER_FDROID);
        ALL_SUPPORT_MARKET.add(CRAWLER_XIAOMI);
        ALL_SUPPORT_MARKET.add(CRAWLER_WANDOUJIA);
        ALL_SUPPORT_MARKET.add(CRAWLER_PP);
        ALL_SUPPORT_MARKET.add(CRAWLER_ANZHI);
        ALL_SUPPORT_MARKET.add(CRAWLER_APPSZOOM);
    }

    public static Crawler getCrawler(String marketName) {

        if (CRAWLER_BAIDU.equals(marketName))
            return new BaiduCrawler();
//        else if (CRAWLER_QIHOO.equals(marketName))
//            return new QihooCrawler();
//        else if (CRAWLER_YINGYONGBAO.equals(marketName))
//            return new YingyongbaoCrawler();
//        else if (CRAWLER_HUAWEI.equals(marketName))
//            return new HuaweiCrawler();
//        else if (CRAWLER_GP.equals(marketName))
//            return new GooglePlayCrawler();
//        else if (CRAWLER_FDROID.equals(marketName))
//            return new FDroidCrawler();
//        else if (CRAWLER_XIAOMI.equals(marketName))
//            return new MiCrawler();
//        else if (CRAWLER_ANZHI.equals(marketName))
//            return new AnzhiCrawler();//
//        else if (CRAWLER_PP.equals(marketName))
//            return new PPCrawler();
//        else if (CRAWLER_WANDOUJIA.equals(marketName))
//            return new WandoujiaCrawler();
        else
            return null;
    }

    public static boolean supportMarket(String marketName) {
        return ALL_SUPPORT_MARKET.contains(marketName);
    }


}
