package cn.droidlover.xdroidmvp.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.droidlover.xdroidmvp.mvp1.presenter.BasePresenter;
import cn.droidlover.xdroidmvp.mvp1.view.BaseView;


/**
 * @anthor created by dujie
 * @date 2018/04/27
 * @change
 * @describe describe
 **/
public abstract class BaseMvpFragment<V, P extends BasePresenter<V>> extends BaseFragment implements BaseView {
    public P presenter;
    public abstract P initPresenter();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = initPresenter();
        if (presenter != null) {
            presenter.attachView((V) this);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
            presenter = null;
        }
    }
}
