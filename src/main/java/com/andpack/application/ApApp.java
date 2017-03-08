package com.andpack.application;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.andframe.$;
import com.andframe.api.DialogBuilder;
import com.andframe.api.pager.status.RefreshLayouter;
import com.andframe.api.view.ViewQuery;
import com.andframe.api.view.Viewer;
import com.andframe.application.AfApp;
import com.andframe.application.AfAppSettings;
import com.andframe.exception.AfExceptionHandler;
import com.andpack.annotation.statusbar.StatusBarTranslucent;
import com.andpack.impl.ApDialogBuilder;
import com.andpack.impl.ApExceptionHandler;
import com.andpack.impl.ApRefreshLayout;
import com.andpack.impl.ApViewQuery;
import com.lzy.imagepicker.ImagePicker;
import com.nostra13.universalimageloader.cache.disc.impl.ext.ApDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 *
 * Created by SCWANG on 2016/9/6.
 */
public class ApApp extends AfApp {

    private RefWatcher mRefWatcher;
    private ApDiskCache mImageDiskCache;

    public static ApApp getApp() {
        return (ApApp)get();
    }

    public ApDiskCache getImageDiskCache() {
        return mImageDiskCache;
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    //<editor-fold desc="初始化">

    @Override
    protected void initApp() throws Exception {
        super.initApp();
        initLeakCanary();
        initImageLoader(this);
        initImagePicker(this);
        initRecovery(this);
    }

    private void initRecovery(Context context) {
//        Class<? extends Activity> activityClass = getMainActivityClass();
//        if (activityClass == null) {
//            return;
//        }
//        Recovery.getInstance()
//                .debug(isDebug())
//                .recoverInBackground(false)
//                .recoverStack(true)
//                .mainPage(activityClass)
//                .callback(new RecoveryCallback() {
//                    @Override
//                    public void stackTrace(String stackTrace) {
//
//                    }
//
//                    @Override
//                    public void cause(String cause) {
//
//                    }
//
//                    @Override
//                    public void exception(String throwExceptionType, String throwClassName, String throwMethodName, int throwLineNumber) {
//
//                    }
//
//                    @Override
//                    public void throwable(Throwable throwable) {
//
//                    }
//                })
//                .init(context);
    }

    protected Class<? extends Activity> getMainActivityClass() {
        return null;
    }

    private void initLeakCanary() {
        try {
            if (!LeakCanary.isInAnalyzerProcess(this) && isDebug()) {
                mRefWatcher = LeakCanary.install(this);
            } else {
                mRefWatcher = RefWatcher.DISABLED;
            }
        } catch (Exception ex) {
            AfExceptionHandler.handle(ex, "初始化内存泄漏检测");
        }
    }


    private void initImagePicker(Context context) {
        ImagePicker picker = ImagePicker.getInstance();
        picker.setImageLoader(new com.lzy.imagepicker.loader.ImageLoader() {
            @Override
            public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
                $.query(imageView).image(path);
            }
            @Override
            public void clearMemoryCache() {
            }
        });
    }

    protected void initImageLoader(Context context) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        //config.threadPriority(Thread.NORM_PRIORITY - 2);
        //config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.defaultDisplayImageOptions(builder.build());
        config.memoryCacheSizePercentage(12);
        /**
         * 初始化图片 SD卡缓存
         */
        try {
            File imageDir = new File(getExternalCacheDir(),"uil-image");
            config.diskCache(mImageDiskCache = new ApDiskCache(imageDir, new Md5FileNameGenerator(), 50 * 1024 * 1024));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageLoader.getInstance().init(config.build());
    }
    //</editor-fold>

    //<editor-fold desc="重写组件">
    @Override
    public ViewQuery<? extends ViewQuery> newViewQuery(Viewer view) {
        return new ApViewQuery(view);
    }

    @Override
    public DialogBuilder newDialogBuilder(Context context) {
        return new ApDialogBuilder(context);
    }

    @Override
    public AfExceptionHandler newExceptionHandler() {
        return new ApExceptionHandler();
    }

    @NonNull
    @Override
    public RefreshLayouter newRefreshLayouter(Context context) {
        return new ApRefreshLayout(context);
    }

    public ApUpdateService newUpdateService() {
        return new ApUpdateService(this) {
            @Override
            protected ServiceVersionInfo infoFromService(String version) throws Exception {
                return null;
            }
        };
    }

    @Override
    public AfAppSettings newAppSetting() {
        return new ApAppSettings(this);
    }

    public StatusBarTranslucent defaultStatusBarTranslucent() {
        return new StatusBarTranslucent(){
            @Override
            public Class<? extends Annotation> annotationType() {
                return StatusBarTranslucent.class;
            }
            @Override
            public float value() {
                return 0.0f;
            }
            @Override
            public int color() {
                return android.R.color.black;
            }
        };
    }

    //</editor-fold>

}