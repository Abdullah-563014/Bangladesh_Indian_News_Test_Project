package com.easysoftbd.bangladeshindiannews.data.repositories;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.rxjava3.core.Observable;

public class MyResponse {

    public Observable<Document> getPageDocument(String pageUrl) {
        return Observable.create(emitter -> {
            try {
                Document document = Jsoup.connect(pageUrl).get();
                if (!emitter.isDisposed()) {
                    emitter.onNext(document);
                }
            } catch (Exception e) { //IOException
                e.printStackTrace();
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }finally {
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }


}
