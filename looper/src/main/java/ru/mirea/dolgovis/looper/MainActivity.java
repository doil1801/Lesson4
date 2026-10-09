package ru.mirea.dolgovis.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.dolgovis.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // (1) Обработчик ответов в главном потоке
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("result");
                Log.d(TAG, "Task execute. This is result: " + result);
                binding.textViewResult.setText(result);
                binding.buttonMirea.setEnabled(true);
            }
        };

        // (2) Создаём и запускаем фоновый поток с Looper
        myLooper = new MyLooper(mainThreadHandler);
        myLooper.start();

        binding.buttonMirea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = binding.editTextAge.getText().toString().trim();
                String job = binding.editTextJob.getText().toString().trim();

                if (age.isEmpty() || job.isEmpty()) {
                    binding.textViewResult.setText("Заполните оба поля");
                    return;
                }
                // Handler потока создаётся уже после start(), проверяем на null
                if (myLooper.mHandler == null) {
                    binding.textViewResult.setText("Поток ещё не готов, повторите");
                    return;
                }

                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("AGE", age);
                bundle.putString("JOB", job);
                msg.setData(bundle);
                myLooper.mHandler.sendMessage(msg);

                binding.textViewResult.setText("Сообщение отправлено, ждём " + age + " с...");
                binding.buttonMirea.setEnabled(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Останавливаем Looper, иначе поток будет жить вечно
        if (myLooper != null && myLooper.mHandler != null) {
            myLooper.mHandler.getLooper().quit();
        }
    }
}