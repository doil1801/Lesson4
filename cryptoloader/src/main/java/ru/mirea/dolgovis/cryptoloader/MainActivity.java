package ru.mirea.dolgovis.cryptoloader;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.snackbar.Snackbar;

import java.security.InvalidParameterException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import ru.mirea.dolgovis.cryptoloader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    public final String TAG = this.getClass().getSimpleName();
    private final int LoaderID = 1234;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton(v);
            }
        });
    }

    public void onClickButton(View view) {
        String phrase = binding.editTextPhrase.getText().toString();
        if (phrase.isEmpty()) {
            binding.textViewCipher.setText("Введите фразу");
            return;
        }

        SecretKey key = generateKey();
        byte[] cipherText = encryptMsg(phrase, key);

        // Показываем шифртекст (для отчёта)
        binding.textViewCipher.setText("Шифртекст (Base64):\n"
                + Base64.encodeToString(cipherText, Base64.DEFAULT));

        // 2. Передаём шифртекст и ключ в Loader через Bundle
        Bundle bundle = new Bundle();
        bundle.putByteArray(MyLoader.ARG_WORD, cipherText);
        bundle.putByteArray(MyLoader.ARG_KEY, key.getEncoded());

        // restartLoader, чтобы при повторных нажатиях данные обновлялись
        LoaderManager.getInstance(this).restartLoader(LoaderID, bundle, this);
    }


    public static SecretKey generateKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            return kg.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encryptMsg(String message, SecretKey secret) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            return cipher.doFinal(message.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secret) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            return new String(cipher.doFinal(cipherText), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == LoaderID) {
            Snackbar.make(binding.getRoot(), "onCreateLoader: " + id,
                    Snackbar.LENGTH_SHORT).show();
            return new MyLoader(this, args);
        }
        throw new InvalidParameterException("Invalid loader id");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (loader.getId() == LoaderID) {
            Log.d(TAG, "onLoadFinished: " + data);
            Snackbar.make(binding.getRoot(), "Расшифровано: " + data,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        Log.d(TAG, "onLoaderReset");
    }
}