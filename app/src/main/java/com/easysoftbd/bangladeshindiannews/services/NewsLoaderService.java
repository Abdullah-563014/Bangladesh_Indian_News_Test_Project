package com.easysoftbd.bangladeshindiannews.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.data.network.MyUrl;
import com.easysoftbd.bangladeshindiannews.data.repositories.MyResponse;
import com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news.BreakingNewsFragment;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NewsLoaderService extends Worker {

    private String notificationId="MyNotification";
    private BreakingNewsFragment breakingNewsFragment;
    private MyResponse myResponse;
    private CompositeDisposable compositeDisposable;


    public NewsLoaderService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        if (breakingNewsFragment==null) {
            breakingNewsFragment=new BreakingNewsFragment();
        }
        if (myResponse==null) {
            myResponse=new MyResponse();
        }
        if (compositeDisposable==null) {
            compositeDisposable=new CompositeDisposable();
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        displayNotification("Test title","Test description. Test description. Test description.");
        loadPageDocument("https://www.kalerkantho.com/");
        return null;
    }

    @Override
    public void onStopped() {
        super.onStopped();
        compositeDisposable.dispose();
    }

    public void loadPageDocument(String pageUrl) {
        myResponse.getPageDocument(pageUrl)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.rxjava3.core.Observer<Document>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Document document) {
                        Log.d(Constants.TAG,"document get successfully");
                        setKalerKhantoBreekingNews(document);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(Constants.TAG,"document get failed");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(Constants.TAG,"document get completed");
                    }
                });
    }

    private void setKalerKhantoBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.content.mCustomScrollbar li");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).select("a").attr("href");
                String news = allList.get(i).select("a").text();
                String link = MyUrl.kalerKhanto + temporaryLink;
                if (news.length() >= 15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kalerKhanto);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("কালের কণ্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        Log.d(Constants.TAG, "list size is " + list.size());
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
        }
    }

    private void displayNotification(String title, String description) {
        NotificationManager notificationManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel=new NotificationChannel(notificationId,notificationId,NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),notificationId)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1,builder.build());
    }
}
