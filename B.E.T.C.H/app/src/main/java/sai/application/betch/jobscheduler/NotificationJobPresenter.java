package sai.application.betch.jobscheduler;

import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import sai.application.betch.cache.cachemodel.Alert;
import sai.application.betch.network.apimodel.CryptoCurrency;
import timber.log.Timber;

/**
 * Created by sai on 7/21/17.
 */

public class NotificationJobPresenter implements NotificationJobMVP.Presenter {

    private NotificationJobMVP.Model model;
    private NotificationJobMVP.Job job;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public NotificationJobPresenter(NotificationJobMVP.Model model) {
        this.model = model;
    }

    @Override
    public void setJob(NotificationJobMVP.Job job) {
        this.job = job;
    }

    @Override
    public void rxUnsubscribe() {
        mCompositeDisposable.dispose();
    }

    @Override
    public void getNotificationMessage(long minutes) {
        Observable<List<CryptoCurrency>> currencyDataObservable = model.getCurrencyData();
        Observable<List<Alert>> alertObservable = model.getActiveTimeAlerts(minutes).toObservable();

        Observable<String> observable = Observable.zip(currencyDataObservable, alertObservable, new BiFunction<List<CryptoCurrency>, List<Alert>, String>() {
            @Override
            public String apply(@NonNull List<CryptoCurrency> cryptoCurrencies, @NonNull List<Alert> alerts) throws Exception {
                String notificationMsg;
                StringBuilder stringBuilder = new StringBuilder();

                if(alerts.size() == 0) {
                    return "";
                }
                Iterator<Alert> alertIterator = alerts.iterator();
                while ((alertIterator.hasNext()))
                {
                    Alert currentAlert = alertIterator.next();
                    for(CryptoCurrency currency : cryptoCurrencies) {
                        if(currentAlert.getCurrencyId().equalsIgnoreCase(currency.getId())) {
                            stringBuilder.append(getNotificationStringForCurrency(currency));
                        }
                    }
                }
                notificationMsg = stringBuilder.toString();
                return notificationMsg;
            }
        });
        final DisposableObserver<String> alertObserver = new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Timber.d("here is the string notification! " + s);
                job.showNotification(s);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e("Error in Price Service " + e.getLocalizedMessage());
            }

            @Override
            public void onComplete() {

            }
        };
        Disposable d = observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(alertObserver);

        mCompositeDisposable.add(d);
    }

    private String getNotificationStringForCurrency(CryptoCurrency currency) {
        return "1 "+ currency.getSymbol() + " = USD " + currency.getPriceUsd()  +"\n";
    }
}
