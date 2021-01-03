package cn.droidlover.xdroidmvp.router;

/**
 * 路由配置
 */
public class RouterPath {
    /**
     * live 直播模块
     */

    public class Live {
        //模块名称
        public static final String Live = "/Live/";
        //live首页fragment
        public static final String LIVE_HOME = Live + "fragment";
        //直播页面
        public static final String LIVE_SHOW = Live + "liveShow";
        //开启直播页面
        public static final String BEGIN_TO_LIVE = Live + "BeginTolive";

        public static final String ALIVE_CREATE = Live + "CreateLive";

        public static final String ALIVE_RANKING = Live + "LiveRanking";

        public static final String ALIVE_PAY = Live + "AlivePay";


    }

    /**
     * 商城模块
     */
    public class Mall {
        //模块名称
        public static final String Mall = "/Mall/";
        public static final String MALL_PRODUCT_DETAIL = Mall+ "productDetail";
    }
    /**
     * 商城模块
     */
    public class Video {
        //模块名称
        public static final String Video = "/Video/";
        public static final String VIDEO_SHORT_SHARE = Video+ "ShareVideo";
    }
}
