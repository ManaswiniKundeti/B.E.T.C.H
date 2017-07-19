package sai.application.betch.alerts.create_alert;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import sai.application.betch.alerts.AlertsActivityMVP;
import sai.application.betch.home.CurrencyViewModel;
import timber.log.Timber;

import static sai.application.betch.alerts.create_alert.Constants.PRICE_TRIGGER;
import static sai.application.betch.alerts.create_alert.Constants.TIME_TRIGGER;

/**
 * Created by sai on 7/19/17.
 */

public class CreateAlertPresenter implements CreateAlertMVP.Presenter {

    private AlertsActivityMVP.Model model;
    private CreateAlertMVP.View view;

    private CompositeDisposable mNetworkSisposables = new CompositeDisposable();
    private CompositeDisposable mEventDisposables = new CompositeDisposable();

    private CurrencyViewModel mSelectedCurrencyViewModel = null;
    private AlertTime mSelectedAlertTime = null;
    private String mTriggerType = null;
    private String mPriceTrigger = null;
    private boolean isDataValid = false;

    public CreateAlertPresenter(AlertsActivityMVP.Model model) {
        this.model = model;
    }

    @Override
    public void closeDialogClicked() {
        view.closeView();
    }

    @Override
    public void loadCurrencyData() {
        Disposable currencyDisposable = model.currencyData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CurrencyViewModel>() {
                    @Override
                    public void onNext(CurrencyViewModel viewModel) {
                        Timber.d("Fetched currency data for creating alerts");
                        view.updateCurrencyData(viewModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("Completed fetching currency data for creating alerts");
                    }
                });
        mNetworkSisposables.add(currencyDisposable);
    }

    @Override
    public void rxUnsubscribe() {
        mNetworkSisposables.clear();
    }

    @Override
    public void rxDestroy() {
        mNetworkSisposables.dispose();
    }

    @Override
    public void setView(CreateAlertMVP.View view) {
        this.view = view;
    }

    @Override
    public void createAlertClicked() {

    }

    @Override
    public void handleCurrencySelected(Observable<CurrencyViewModel> observable) {
        DisposableObserver<CurrencyViewModel> currencyDisposable = new DisposableObserver<CurrencyViewModel>() {
            @Override
            public void onNext(CurrencyViewModel viewModel) {
                Timber.d("User selecred currency: " + viewModel.toString());
                mSelectedCurrencyViewModel = viewModel;
                validateData();

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(currencyDisposable);

        mEventDisposables.add(currencyDisposable);
    }

    @Override
    public void handleTriggerTypeSelected(Observable<String> observable) {
        DisposableObserver<String> triggerTypeDisposable = new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Timber.d("Trigger type selected : " + s);
                mTriggerType = s;
                view.toggleTriggerLayouts(mTriggerType.equalsIgnoreCase(PRICE_TRIGGER));
                validateData();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(triggerTypeDisposable);
        mEventDisposables.add(triggerTypeDisposable);
    }

    @Override
    public void handlePriceEntered(Observable<String> observable) {
        DisposableObserver<String> priceDisposable = new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Timber.d("Price entered : " + s);
                mPriceTrigger = s;
                validateData();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(priceDisposable);
        mEventDisposables.add(priceDisposable);
    }

    @Override
    public void handleAlertTimeSelected(Observable<AlertTime> observable) {
        DisposableObserver<AlertTime> timeAlertDisposable = new DisposableObserver<AlertTime>() {
            @Override
            public void onNext(AlertTime alertTime) {
                Timber.d("Time entered : " + alertTime.getTimeString());
                mSelectedAlertTime = alertTime;
                validateData();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(timeAlertDisposable);
        mEventDisposables.add(timeAlertDisposable);
    }

    private void validateData() {
        if(mSelectedCurrencyViewModel == null) {
            isDataValid = false;
            view.toggleCreateAlertButtonEnabled(false);
            return;
        }

        if(mTriggerType == null) {
            isDataValid = false;
            view.toggleCreateAlertButtonEnabled(false);
            return;
        }

        if(mTriggerType.equals(PRICE_TRIGGER) && (mPriceTrigger == null || !isValidNumber(mPriceTrigger))) {
            isDataValid = false;
            view.toggleCreateAlertButtonEnabled(false);
            return;
        }
        else if(mTriggerType.equals(TIME_TRIGGER) && mSelectedAlertTime == null) {
            isDataValid = false;
            view.toggleCreateAlertButtonEnabled(false);
            return;
        }

        isDataValid = true;
        view.toggleCreateAlertButtonEnabled(true);
    }

    private boolean isValidNumber(String price) {
        try {
            Double.parseDouble(price);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
