package com.game.mosquitokiller.engine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.mosquitokiller.Gameover;
import com.game.mosquitokiller.MainActivity;
import com.game.mosquitokiller.R;

import java.util.Date;
import java.util.Random;

public class GameEngine implements View.OnClickListener, Runnable {

    private static final long MAXAGE_PILL = 2000;
    private int round;
    private Boolean gameRunning;
    private int points;
    private int pills;
    private int eatenPills;
    private int time;
    private float size;
    private Random geRandom = new Random();
    private float random;
    private double possiblity;
    private ViewGroup playground;
    private Handler handler = new Handler();
    private Activity activity;


    public GameEngine(Activity activity) {
        this.activity = activity;
        gameRunning = true;
        round = 0;
        points = 0;
        this.size = activity.getResources().getDisplayMetrics().density;
        playground = activity.findViewById(R.id.playground);
    }

    public void startRound() {
        round++;
        pills = round * 10;
        eatenPills = 0;
        time = 10;
        refreshScreen();
        handler.postDelayed(this, 1000);
    }

    public void countDown() {
        time--;
        random = geRandom.nextFloat();
        possiblity = pills * 1.5;
        if (possiblity > 1) {
            showAPill();
            if (random < possiblity - 1) {
                showAPill();
            }
        } else {
            if (random < possiblity) {
                showAPill();
            }
        }
        removePills();
        refreshScreen();
        if (!checkGameOver() && !checkRoundOver()) {
            handler.postDelayed(this, 1000);
        }
    }

    private boolean checkRoundOver() {
        if (eatenPills >= pills) {
            startRound();
            return true;
        }
        return false;
    }

    private boolean checkGameOver() {
        if (time == 0 && eatenPills < pills) {
            gameOver();
            return true;
        }
        return false;
    }

    private void gameOver() {
        gameRunning = false;
        activity.setResult(points);
        Dialog dialog = new Gameover(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.show();
    }

    private void removePills() {
        int count = 0;
        while (count < playground.getChildCount()) {
            ImageView pill = (ImageView) playground.getChildAt(count);
            Date birthdate = (Date) pill.getTag(R.id.birthdate);
            long age = (new Date()).getTime() - birthdate.getTime();
            if (age > MAXAGE_PILL) {
                playground.removeView(pill);
            } else {
                count++;
            }
        }
    }

    private void showAPill() {
        int width = playground.getWidth();
        int height = playground.getHeight();
        int pill_width = Math.round(this.size * 50);
        int pill_height = Math.round(this.size * 42);
        int left = geRandom.nextInt(width - pill_width);
        int up = geRandom.nextInt(height - pill_height);
        ImageView pill = new ImageView(activity);
        pill.setImageResource(R.drawable.pill);
        pill.setOnClickListener(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(pill_width, pill_height);
        params.leftMargin = left;
        params.topMargin = up;
        params.gravity = Gravity.TOP + Gravity.LEFT;
        playground.addView(pill, params);
        pill.setTag(R.id.birthdate, new Date());
    }

    private void refreshScreen() {
        TextView textPunkte = activity.findViewById(R.id.points);
        textPunkte.setText(Integer.toString(points));

        TextView textRunde = activity.findViewById(R.id.round);
        textRunde.setText(Integer.toString(round));

        TextView textZeit = activity.findViewById(R.id.time);
        textZeit.setText(Integer.toString(time));

        TextView textHits = activity.findViewById(R.id.hits);
        textHits.setText(Integer.toString(eatenPills));

        FrameLayout frameHits = activity.findViewById(R.id.bar_hits);
        FrameLayout frameTime = activity.findViewById(R.id.bar_time);

        ViewGroup.LayoutParams layoutHits = frameHits.getLayoutParams();
        layoutHits.width = Math.round(size * 300 * Math.min(eatenPills, pills) / pills);

        ViewGroup.LayoutParams layoutTime = frameTime.getLayoutParams();
        layoutTime.width = Math.round(size * time * 300 / 60);
    }

    @Override
    public void onClick(View view) {
        eatenPills++;
        points += 100;
        refreshScreen();
        playground.removeView(view);
    }

    @Override
    public void run() {
        countDown();
    }
}
