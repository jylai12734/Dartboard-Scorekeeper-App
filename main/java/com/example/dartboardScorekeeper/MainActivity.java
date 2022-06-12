package com.example.dartboardScorekeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // declare global variables
    Button btnPlay;
    SwitchCompat swPlayers, swPoints, swSplit;
    SharedPreferences save;
    SharedPreferences.Editor edit;
    boolean twoPlayers, points, split;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");

        // retrieve the state of the switches from the shared preference
        save = PreferenceManager.getDefaultSharedPreferences(this);
        edit = save.edit();
        twoPlayers = save.getBoolean("twoPlayers", false);
        points = save.getBoolean("points", false);
        split = save.getBoolean("split", false);

        // assign OnClickListeners to all the button and switches
        (btnPlay = findViewById(R.id.start)).setOnClickListener(this);
        (swPlayers = findViewById(R.id.Players)).setOnClickListener(this);
        (swPoints = findViewById(R.id.Points)).setOnClickListener(this);
        (swSplit = findViewById(R.id.Split)).setOnClickListener(this);

        // restore the state of the switches from the last app usage
        swPlayers.setChecked(twoPlayers);
        swPoints.setChecked(points);
        swSplit.setChecked(split);
    }

    @Override
    public void onClick(View v) {
        // direct the clicked button or switch to its corresponding method
        if (v.getId() == R.id.start) startGame(v);
        else if (v.getId() == R.id.Players) setTwoPlayers(v);
        else if (v.getId() == R.id.Points) setPoints(v);
        else setSplit(v);
    }

    public void startGame(View v) {
        // start the game taking into account the states of the switches
        Intent i;
        if (!twoPlayers) i = new Intent(this, OnePlayerActivity.class);
        else i = new Intent(this, TwoPlayerActivity.class);
        i.putExtra("points", points);
        i.putExtra("split", split);
        startActivity(i);
    }

    public void setTwoPlayers(View v) {
        // change whether it is one player or two players
        twoPlayers = !twoPlayers;
        edit.putBoolean("twoPlayers", twoPlayers);
        edit.apply();
    }

    public void setPoints(View v) {
        // change whether the starting point is 301 or 501
        points = !points;
        edit.putBoolean("points", points);
        edit.apply();
    }

    public void setSplit(View v) {
        // change whether the player(s) can split the 11
        split = !split;
        edit.putBoolean("split", split);
        edit.apply();
    }
}