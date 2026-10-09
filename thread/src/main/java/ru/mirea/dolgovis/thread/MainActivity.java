package ru.mirea.dolgovis.thread;

import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import ru.mirea.dolgovis.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String LOG_TAG = "ThreadProject";

    // Данные студента
    private static final String GROUP = "БИСО-01-22";
    private static final int LIST_NUMBER = 10;
    private static final String FAVOURITE_FILM = "Криминальное чтиво";

    private ActivityMainBinding binding;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Thread mainThread = Thread.currentThread();
        binding.textViewInfo.setText("Имя текущего потока: " + mainThread.getName());
        // Меняем имя и выводим в текстовом поле
        mainThread.setName("МОЙ НОМЕР ГРУППЫ: " + GROUP
                + ", НОМЕР ПО СПИСКУ: " + LIST_NUMBER
                + ", МОЙ ЛЮБИМЫЙ ФИЛЬМ: " + FAVOURITE_FILM);
        binding.textViewInfo.append("\nНовое имя потока: " + mainThread.getName());
        Log.d(TAG, "Stack: " + Arrays.toString(mainThread.getStackTrace()));
        Log.d(TAG, "Group: " + mainThread.getThreadGroup());

        binding.buttonMainThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long endTime = System.currentTimeMillis() + 20 * 1000;
                while (System.currentTimeMillis() < endTime) {
                    synchronized (this) {
                        try {
                            wait(endTime - System.currentTimeMillis());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        binding.buttonBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int numberThread = counter.incrementAndGet();
                        Log.d(LOG_TAG, String.format(Locale.getDefault(),
                                "Запущен поток № %d студентом группы № %s номер по списку № %d",
                                numberThread, GROUP, LIST_NUMBER));

                        long endTime = System.currentTimeMillis() + 20 * 1000;
                        while (System.currentTimeMillis() < endTime) {
                            synchronized (this) {
                                try {
                                    wait(endTime - System.currentTimeMillis());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        Log.d(LOG_TAG, "Выполнен поток № " + numberThread);
                    }
                }).start();
            }
        });

        binding.buttonCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAverage();
            }
        });
    }

    private void calculateAverage() {
        String pairsStr = binding.editTextPairs.getText().toString().trim();
        String daysStr = binding.editTextDays.getText().toString().trim();

        if (pairsStr.isEmpty() || daysStr.isEmpty()) {
            binding.textViewResult.setText("Введите оба значения");
            return;
        }

        final int pairs;
        final int days;
        try {
            pairs = Integer.parseInt(pairsStr);
            days = Integer.parseInt(daysStr);
        } catch (NumberFormatException e) {
            binding.textViewResult.setText("Слишком большое число");
            return;
        }

        if (days <= 0) {
            binding.textViewResult.setText("Количество учебных дней должно быть больше 0");
            return;
        }

        binding.buttonCalc.setEnabled(false);
        binding.textViewResult.setText("Идёт подсчёт...");

        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                SystemClock.sleep(3000);
                final double average = (double) pairs / days;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.textViewResult.setText(String.format(Locale.getDefault(),
                                "Среднее количество пар в день: %.2f", average));
                        binding.buttonCalc.setEnabled(true);
                    }
                });
            }
        });
        worker.start();
    }
}
