package com.game.mosquitokiller;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Html.ImageGetter {

    private static final String BASE_URL = "";
    private Button startButton;
    private LinearLayout nameinput;
    private Button save;
    private int score;
    private String highscoresHtml = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameinput = findViewById(R.id.nameinput);
        save = findViewById(R.id.button2);
        save.setOnClickListener(this);
        nameinput.setVisibility(View.INVISIBLE);
        startButton = findViewById(R.id.button);
        startButton.setOnClickListener(this);
//        resetHighscore();
    }


    @Override
    protected void onResume() {
        super.onResume();
        showHighscore();
        internetHighscores("",0);
    }

    private int readHighscore() {
        SharedPreferences sharedPreferences = getSharedPreferences("GAME", 0);
        return sharedPreferences.getInt("HIGHSCORE", 0);
    }

    private void internetHighscores(final String name, final int points) {
        (new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "&name=" + URLEncoder.encode(name, "utf-8") + "&points=" + points + "&max=100");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                InputStreamReader inputReader = new InputStreamReader(connection.getInputStream(), "UTF8");
                BufferedReader reader = new BufferedReader(inputReader, 2000);
                List<String> highscoreList = new ArrayList<>();
                String line;
                do {
                    line = reader.readLine();
                    highscoreList.add(line);
                } while (line != null);
                for (String s : highscoreList) {
                    highscoresHtml += "<b>" + s.replace(",", "<b> <font color='red'></font><br>");
                }
            } catch (IOException e) {
                e.printStackTrace();
                highscoresHtml = "Wrong: " + e.getMessage();
            }
            runOnUiThread(() -> {
                TextView tv = findViewById(R.id.highscore);
                tv.setText(Html.fromHtml(highscoresHtml, this, null));
            });
        })).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            startActivityForResult(new Intent(this, GameActivity.class), 1, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else if (v.getId() == R.id.button2) {
            writeScore();
            internetHighscores(readHighscoreName(), readHighscore());
            nameinput.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode > readHighscore()) {
            nameinput.setVisibility(View.VISIBLE);
            score = resultCode;
        }
    }

    private void writeScore() {
        EditText editText = findViewById(R.id.name);
        String name = editText.getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("HIGHSCORE", score);
        editor.putString("HIGHSCORE_NAME", name);
        editor.commit();
        showHighscore();
    }

    private String readHighscoreName() {
        SharedPreferences preferences = getSharedPreferences("GAME", 0);
        return preferences.getString("HIGHSCORE_NAME", "");
    }

    private void showHighscore() {
        int highscore = readHighscore();
        String highscoreText = highscore > 0 ? readHighscoreName() + ": " + highscore : "---";
        TextView score = findViewById(R.id.highscore);
        score.setText(highscoreText);
    }

    private void resetHighscore() {
        SharedPreferences sharedPreferences = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("HIGHSCORE", 0);
        editor.putString("HIGHSCORE_NAME", "");
        editor.commit();
        showHighscore();
    }

    @Override
    public Drawable getDrawable(String source) {
        return null;
    }
}
