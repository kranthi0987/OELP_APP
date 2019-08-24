package mahiti.org.oelp.utils;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Created by RAJ ARYAN on 30/07/19.
 */
public class OTPTimer extends Worker {

    public static final String TASK_DESC = "taskdesc";
    public MutableLiveData<String> countDownTime = new MutableLiveData<>();



    public OTPTimer(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        start30MinTimerForOTP();
        Data data = new Data.Builder()
                .putString(TASK_DESC, countDownTime.getValue())
                .build();
        setOutputData(data);
        return Result.SUCCESS;

    }

    private void start30MinTimerForOTP() {
        countDownTime.setValue("00");
        new CountDownTimer(300000, 10000) {

            public void onTick(long millisUntilFinished) {
                countDownTime.setValue(String.valueOf(millisUntilFinished));
            }

            public void onFinish() {
                countDownTime.setValue("00");
            }
        }.start();
    }
}
