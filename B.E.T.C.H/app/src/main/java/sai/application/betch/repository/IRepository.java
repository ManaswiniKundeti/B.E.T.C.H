package sai.application.betch.repository;


import java.util.List;

import io.reactivex.Observable;
import sai.application.betch.cache.cachemodel.Alert;
import sai.application.betch.network.apimodel.CryptoCurrency;

/**
 * Created by sai on 7/16/17.
 */

public interface IRepository {

    /**
     * This method is used to load data from the network
     */
    Observable<List<CryptoCurrency>> loadDataFromNetwork(int limit);

    /**
     * This method is used to load data from the memory
     */
    Observable<List<CryptoCurrency>>  loadDataFromMemory();


    /**
     * This method is used by the model to load data.
     * This method will internally decide whether to load from memory or network
     */
    Observable<List<CryptoCurrency>>  loadData();

    /* Repository functions for Alert */

    /**
     * This method is used to load alerts from the persistent storage
     * @return
     */
    Observable<Alert> loadAlertsFromCache();

    /**
     * This method is used to save an alert to the persistent storage
     * @param alert
     * @return
     */
    Observable saveAlert(Alert alert);

    /**
     * This method is used to fetch an alert from the persistent storage
     * @param guid
     * @return
     */
    Observable<Alert> getAlert(String guid);

    /**
     * This method is used to delete an alert from the persistent storage
     * @param guid
     * @return
     */
    Observable deleteAlert(String guid);
}
