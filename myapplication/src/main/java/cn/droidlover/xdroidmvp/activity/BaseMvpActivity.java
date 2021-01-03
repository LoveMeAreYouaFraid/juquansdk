package cn.droidlover.xdroidmvp.activity;
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
public abstract class BaseMvpActivity<V, P extends BasePresenter<V>> extends BaseActivity implements BaseView {
    public P presenter;
    //通过一个hander触发loadingDialog的显示隐藏状态
    public static final int SHOW_LOADING = 0x02;
    public static final int HIDE_LOADING = 0x03;

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
