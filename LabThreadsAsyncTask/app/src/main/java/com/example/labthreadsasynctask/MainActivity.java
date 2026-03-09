package com.example.labthreadsasynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView txtStatus;
    private ProgressBar progressBar;
    private ImageView img;

    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = findViewById(R.id.txtStatus);
        progressBar = findViewById(R.id.progressBar);
        img = findViewById(R.id.img);

        Button btnLoadThread = findViewById(R.id.btnLoadThread);
        Button btnCalcAsync = findViewById(R.id.btnCalcAsync);
        Button btnToast = findViewById(R.id.btnToast);

        mainHandler = new Handler(Looper.getMainLooper());

        btnToast.setOnClickListener(v ->
                Toast.makeText(getApplicationContext(),
                        "UI réactive", Toast.LENGTH_SHORT).show()
        );

        btnLoadThread.setOnClickListener(v -> loadImageWithThread());

        btnCalcAsync.setOnClickListener(v ->
                new HeavyCalcTask().execute());
    }

    private void loadImageWithThread() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        txtStatus.setText("Statut : chargement image (Thread)...");

        new Thread(() -> {

            for (int i = 1; i <= 100; i++) {

                try {
                    Thread.sleep(30); // ralentit pour voir la progression
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int progress = i;

                // mise à jour ProgressBar sur UI thread
                mainHandler.post(() -> progressBar.setProgress(progress));
            }

            // charger l'image
            Bitmap bitmap = BitmapFactory.decodeResource(
                    getResources(),
                    R.mipmap.ic_launcher
            );

            mainHandler.post(() -> {

                img.setImageBitmap(bitmap);
                progressBar.setVisibility(View.INVISIBLE);
                txtStatus.setText("Statut : image chargée (Thread)");

            });

        }).start();
    }
    private class HeavyCalcTask extends AsyncTask<Void, Integer, Long> {

        @Override
        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            txtStatus.setText("Statut : calcul lourd (AsyncTask)...");

        }

        @Override
        protected Long doInBackground(Void... voids) {

            long result = 0;

            for (int i = 1; i <= 100; i++) {

                result += i;

                try {
                    Thread.sleep(50); // ralentit pour voir la ProgressBar
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress(i);
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progressBar.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(Long result) {

            progressBar.setVisibility(View.INVISIBLE);
            txtStatus.setText(
                    "Statut : calcul terminé résultat = " + result
            );

        }
    }
}