package ru.mirea.dolgovis.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class MyLooper extends Thread {

    private static final String TAG = "MyLooper";
    public volatile Handler mHandler;
    private final Handler mainHandler;

    public MyLooper(Handler mainThreadHandler) {
        this.mainHandler = mainThreadHandler;
    }

    @Override
    public void run() {
        Log.d(TAG, "run");

        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String ageStr = msg.getData().getString("AGE");
                String job = msg.getData().getString("JOB");
                Log.d(TAG, "get message: age=" + ageStr + ", job=" + job);

                int age;
                try {
                    age = Integer.parseInt(ageStr);
                } catch (NumberFormatException e) {
                    age = 0;
                }

                SystemClock.sleep(age * 1000L);

                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("result", String.format(
                        "Вам %d лет, профессия: %s. Задержка составила %d с.",
                        age, job, age));
                message.setData(bundle);

                mainHandler.sendMessage(message);
            }
        };

        Looper.loop();
    }
}