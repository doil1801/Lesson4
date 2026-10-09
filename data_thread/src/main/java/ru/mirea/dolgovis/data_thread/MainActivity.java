package ru.mirea.dolgovis.data_thread;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import ru.mirea.dolgovis.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Текстовое описание различий (требование задания)
        binding.tvDescription.setText(
                "runOnUiThread(Runnable) - метод Activity. Если вызван из UI-потока, "
                        + "Runnable выполняется сразу. Если из другого потока - Runnable "
                        + "ставится в очередь UI-потока.\n\n"
                        + "View.post(Runnable) - всегда ставит Runnable в конец очереди "
                        + "сообщений UI-потока. Выполнится, когда до него дойдёт очередь, "
                        + "то есть немедленно, но не раньше завершения текущих задач.\n\n"
                        + "View.postDelayed(Runnable, long) - то же, что post, но "
                        + "выполнение откладывается на указанное число миллисекунд.\n\n"
                        + "Во всех трёх случаях Runnable исполняется в главном (UI) потоке, "
                        + "поэтому в нём можно менять элементы интерфейса.");

        final Runnable runn1 = new Runnable() {
            @Override
            public void run() {
                log("runn1 (runOnUiThread)");
            }
        };

        final Runnable runn2 = new Runnable() {
            @Override
            public void run() {
                log("runn2 (post)");
            }
        };

        final Runnable runn3 = new Runnable() {
            @Override
            public void run() {
                log("runn3 (postDelayed 2000 мс)");
            }
        };

        startTime = SystemClock.elapsedRealtime();
        binding.tvInfo.setText("Старт: 0.0 с\n");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    runOnUiThread(runn1);

                    TimeUnit.SECONDS.sleep(1);
                    binding.tvInfo.postDelayed(runn3, 2000);
                    binding.tvInfo.post(runn2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    // Дописывает строку в TextView со временем от старта
    private void log(String name) {
        double sec = (SystemClock.elapsedRealtime() - startTime) / 1000.0;
        String line = String.format(java.util.Locale.US, "%.1f с: %s\n", sec, name);
        binding.tvInfo.append(line);
        Log.d(TAG, line.trim());
    }
}