package ru.mirea.dolgovis.cryptoloader;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {

    public static final String ARG_WORD = "word";
    public static final String ARG_KEY = "key";

    private byte[] cryptText;
    private byte[] key;

    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);
        if (args != null) {
            cryptText = args.getByteArray(ARG_WORD);
            key = args.getByteArray(ARG_KEY);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad(); // без этого loadInBackground не запустится
    }

    @Override
    public String loadInBackground() {
        // имитация долгой операции
        SystemClock.sleep(2000);

        if (cryptText == null || key == null) {
            return null;
        }
        // Восстановление ключа и дешифровка
        SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES");
        return MainActivity.decryptMsg(cryptText, originalKey);
    }
}