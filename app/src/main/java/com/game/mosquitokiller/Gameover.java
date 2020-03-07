package com.game.mosquitokiller;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialog;

public class Gameover extends AppCompatDialog implements View.OnClickListener {

    private Button homeButton;
    private Activity activity;
    private int points;

    public Gameover(Activity activity, int theme) {
        super(activity, theme);
        points = theme;
        this.activity = activity;
        setContentView(R.layout.activity_gameover);
        homeButton = findViewById(R.id.button3);
        homeButton.setOnClickListener(this);
        show();
    }

    @Override
    public void onClick(View v) {
        cancel();
        activity.finish();
    }
}
