package cn.droidlover.xdroidmvp.fragment;


import android.os.Bundle;
import androidx.annotation.Nullable;
import cn.droidlover.xdroidmvp.mvp1.presenter.BasePresenter;
import cn.droidlover.xdroidmvp.mvp1.view.BaseView;


/**
 * @anthor created by dujie
 * @date 2018/04/27
 * @change
 * @describe 实现MVP架构的基类，使用者只需继承即可
 **/
public abstract class BaseMvpFragmentActivity<V, P extends BasePresenter<V>> extends BaseFragmentActivity implements BaseView {
    public P presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = initPresenter();
        if (presenter != null) {
            //实现View层接口
            presenter.attachView((V) this);
        }
        super.onCreate(savedInstanceState);
    }

    public abstract P initPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
            presenter = null;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
