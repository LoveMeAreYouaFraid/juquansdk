package cn.droidlover.xdroidmvp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.reflect.Field;
import java.util.Map;
import butterknife.ButterKnife;
import cn.droidlover.xdroidmvp.R;
import cn.droidlover.xdroidmvp.util.InputUtil;


/**
 * @anthor created by haha
 * @date 2019/7/22
 * @change
 * @describe describe
 **/
public abstract class BaseActivity extends RxAppCompatActivity {
    /**
     * 是否可以隐藏软键盘
     */
    private boolean isShouldHideInput = true;
    /**
     * 标记目前界面是否已经调用过setcontentView这个方法，如果子类已经有调用，父类就不调用
     */
    private boolean initContentView = false;
    /**
     * 作用同 initContentView
     */
    private boolean initViews = false;

    public abstract int getLayoutId();

    //上下文对象
    protected Activity self = this;

    public abstract void initViews(Bundle savedInstanceState);

    private Boolean isNotch = false;// 是否为刘海屏
    private int type;

    //返回按钮的点击事件
    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.back) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparency();
        if (!initContentView) {
            setContentView(getLayoutId());
            setInitContentView(true);
        }
        //绑定初始化ButterKnife
        ButterKnife.bind(this);
        View backView = findViewById(R.id.back);
        //给返回按钮添加点击事件
        if (backView != null) {
            backView.setOnClickListener(backListener);
        }
        if (!initViews) {
            initViews(savedInstanceState);
            setInitViews(true);
        }
    }

    public void setInitContentView(boolean init) {
        initContentView = init;
    }

    public void setInitViews(boolean init) {
        initViews = init;
    }

    public boolean isInitContentView() {
        return initContentView;
    }

    public boolean isInitViews() {
        return initViews;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isShouldHideInput) {
                // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
                View v = getCurrentFocus();
                if (InputUtil.isShouldHideInput(v, ev)) {
                    InputUtil.hideSoftInput(BaseActivity.this, v.getWindowToken());
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否开启 点击输入法之外的地方 自动隐藏软键盘功能，默认是开启的
     *
     * @param hide
     */
    public void setShouldHideInput(boolean hide) {
        this.isShouldHideInput = hide;
    }

    public void startActivity(Class<?> clazz) {
        try {
            Intent intent = new Intent(this, clazz);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startActivity(Class<?> clazz, int requestCode) {
        try {
            Intent intent = new Intent(this, clazz);
            startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //带bundle的intent跳转
    protected void startActivity(Class<?> activity, Bundle bundle) {
        Intent intent = new Intent(this, activity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    public void startActivity(Class<?> clazz, Bundle bundle, int requestCode) {
        try {
            Intent intent = new Intent(this, clazz);
            intent.putExtras(bundle);
            startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //普通参数类型的intent跳转这里是用了一个map集合
    protected void startActivity(Class<?> activity, Map<String, String> data) {
        Intent intent = new Intent(this, activity);
        for (String key : data.keySet()) {
            intent.putExtra(key, data.get(key));
        }
        startActivity(intent);
    }


    //普通参数类型的intent跳转这里是用了一个map集合
    protected void startActivity(Class<?> activity, Map<String, String> data, int requestCode) {
        Intent intent = new Intent(this, activity);
        for (String key : data.keySet()) {
            intent.putExtra(key, data.get(key));
        }
        startActivityForResult(intent, requestCode);
    }

    //activity头部的title
    protected void setTitle(String title) {
        if (findViewById(R.id.txtTitle) != null) {
            TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
            txtTitle.setText(title);
        }
    }

    //解決层侵式状态栏为灰色透明的问题设置为透明色
    private void setStatusBarTransparency() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();

    }

    // 重写activity的onDestroy（）方法，停止该页面的glide的加载请求
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
