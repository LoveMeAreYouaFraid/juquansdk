package cn.droidlover.xdroidmvp.mvp1.presenter;

/**
 * 由于presenter会持有Activity（View）的应用，可能会造成内存泄漏，这个接口负责管理这一过程
 * MVP 架构中管理presenter的生命周期，负责创建销毁presenter
 * Created by haha on 2019/7/22.
 */

public interface IPresenter<V> {
    void attachView(V view);

    void detachView();
}
