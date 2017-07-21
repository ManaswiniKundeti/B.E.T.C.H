package sai.application.betch.jobscheduler;

import android.app.Application;
import android.support.annotation.NonNull;

import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import sai.application.betch.root.App;
import timber.log.Timber;

/**
 * Created by sai on 7/21/17.
 */

public class OneDayJob extends ShowNotificationJob {
    public static final String TAG = "one_day_job";
    private static final long MINUTES = 1440;

    public OneDayJob(Application application) {
        super(application);
        ((App)application).getComponent().inject(this);
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        return super.onRunJob(params);
    }

    public static JobRequest buildJobRequest() {
        Timber.d("Requesting for show notification job");
        return new JobRequest.Builder(TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(MINUTES), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(true)
                .setPersisted(true)
                .build();
    }

    @Override
    protected long getNotificationFrequencyInMinutes() {
        return MINUTES;
    }

    @Override
    public void showNotification(String msg) {
        showNotification(getNotificationTitle(MINUTES), msg);
    }
}
