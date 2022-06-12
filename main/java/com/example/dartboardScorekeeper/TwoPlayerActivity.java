package com.example.dartboardScorekeeper;

import static android.graphics.Color.GREEN;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

class state2 {
    // a collection of scores of both players during one instance of the game
    int scoreP1, score1P1, score2P1, score3P1, turnP1;
    int scoreP2, score1P2, score2P2, score3P2, turnP2;
    int turn;
    state2 next, prev;
    public state2(int sp1, int s1p1, int s2p1, int s3p1, int tp1,
                 int sp2, int s1p2, int s2p2, int s3p2, int tp2, int t, state2 n, state2 p) {
        this.scoreP1 = sp1; this.score1P1 = s1p1; this.score2P1 = s2p1; this.score3P1 = s3p1; this.turnP1 = tp1;
        this.scoreP2 = sp2; this.score1P2 = s1p2; this.score2P2 = s2p2; this.score3P2 = s3p2; this.turnP2 = tp2;
        this.turn = t; this.next = n; this.prev = p;
    }
}

class doublyLinkedList2 {
    // a collection of state2s connected using a doubly linked list
    state2 head;
    state2 tail;
    public doublyLinkedList2() {
        this.head = null;
        this.tail = null;
    }
    public void add(state2 s) {
        tail.next = s;
        s.prev = tail;
        tail = s;
    }
    public void end(state2 s) {
        s.next.prev = null;
        s.next = null;
        this.tail = s;
    }
}

public class TwoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    // declare global variables
    Button btnEnter, btnBack, btnForward, btnNo, btnYes;
    EditText source1, source2;
    TextView isDoneText, textP1, textP2;
    Boolean lastRound = false, points, split;
    int scoreP1, score1P1, score2P1, score3P1, turnP1;
    int scoreP2, score1P2, score2P2, score3P2, turnP2;
    int turn = 0;
    String nextThrowLabel;
    state2 curr;
    doublyLinkedList2 history; // history allows user to undo and redo
    ColorStateList defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player);

        // retrieve data from the main activity and initialize the scores and turns
        Intent i = getIntent();
        points = i.getBooleanExtra("points", false);
        split = i.getBooleanExtra("split", false);
        if (points) {
            scoreP1 = 501; score1P1 = 501; score2P1 = 501; score3P1 = 501; scoreP2 = 501; score1P2 = 501; score2P2 = 501; score3P2 = 501;
            String newScore = "Score: 501";
            ((TextView) findViewById(R.id.scoreP1)).setText(newScore);
            ((TextView) findViewById(R.id.scoreP2)).setText(newScore);
        }
        else {scoreP1 = 301; score1P1 = 301; score2P1 = 301; score3P1 = 301; scoreP2 = 301; score1P2 = 301; score2P2 = 301; score3P2 = 301;}
        turnP1 = 0; turnP2 = 0;

        // initialize history to be the starting state of the game
        history = new doublyLinkedList2();
        state2 s = new state2(scoreP1, score1P1, score2P1, score3P1, turnP1, scoreP2, score1P2, score2P2, score3P2, turnP2, turn, null, null);
        history.head = s;
        history.tail = s;
        curr = s;

        // assign OnClickListeners to all the buttons
        (btnEnter = findViewById(R.id.enter)).setOnClickListener(this);
        (btnBack = findViewById(R.id.back)).setOnClickListener(this);
        (btnForward = findViewById(R.id.forward)).setOnClickListener(this);
        (btnNo = findViewById(R.id.n)).setOnClickListener(this);
        (btnYes = findViewById(R.id.y)).setOnClickListener(this);
        source1 = findViewById(R.id.inputP1);
        source2 = findViewById(R.id.inputP2);
        isDoneText = findViewById(R.id.fin);
        textP1 = findViewById(R.id.p1);
        textP2 = findViewById(R.id.p2);

        // disable the back and forward button in the beginning
        btnBack.setEnabled(false);
        btnForward.setEnabled(false);

        // disable player2's input box and highlight player1's text to signify it is player1's turn
        source2.setEnabled(false);
        defaultColor = textP1.getTextColors();
        textP1.setTextColor(GREEN);
    }

    public void onClick(View v) {
        // direct the clicked button to its corresponding method
        if (v.getId() == R.id.enter) reduce(v);
        else if (v.getId() == R.id.back || v.getId() == R.id.n) goBack(v);
        else if (v.getId() == R.id.forward) goForward(v);
        else finish(v);
    }

    public boolean notBusted(int temp) {
        // check if busted
        if (temp < 0 || (temp == 1 && !split)) {
            Toast.makeText(this, "Busted", Toast.LENGTH_SHORT).show();
            nextThrowLabel = "First Throw";
            if (turn / 3 % 2 == 0) ((TextView)findViewById(R.id.throwLabelP1)).setText(nextThrowLabel);
            else ((TextView)findViewById(R.id.throwLabelP2)).setText(nextThrowLabel);
            turn += 3 - turn % 3 - 1;
            return false;
        }
        return true;
    }

    public void updateLabel(int label, int temp, int scoreDisplay) {
        // update the score label to include the new score
        ((TextView)findViewById(label)).setText(nextThrowLabel);
        String newScore = "Score: " + (Integer) temp;
        ((TextView)findViewById(scoreDisplay)).setText(newScore);
    }

    public void switchOff() {
        // switch the layout of the screen to ask if the user is finished
        source1.setEnabled(false);
        source2.setEnabled(false);
        btnEnter.setEnabled(false);
        btnBack.setEnabled(false);
        btnForward.setEnabled(false);
        isDoneText.setVisibility(View.VISIBLE);
        btnNo.setVisibility(View.VISIBLE);
        btnYes.setVisibility(View.VISIBLE);
    }

    public void changeColors(int active, int passive, int player, int notPlayer) {
        // change colors to signify that the player turn changed
        findViewById(active).setEnabled(false);
        findViewById(passive).setEnabled(true);
        ((TextView) findViewById(player)).setTextColor(defaultColor);
        ((TextView) findViewById(notPlayer)).setTextColor(GREEN);
    }

    public void reduce(View v) {
        // find whose turn it is and get that player's score and turn
        int sub, temp;
        int active, passive, player, notPlayer, label, scoreDisplay;
        int s, s1, s2, s3, t;
        if (turn / 3 % 2 == 0) {
            active = R.id.inputP1; passive = R.id.inputP2;
            player = R.id.p1; notPlayer = R.id.p2; label = R.id.throwLabelP1;
            s = scoreP1; s1 = score1P1; s2 = score2P1; s3 = score3P1; t = turnP1;
            scoreDisplay = R.id.scoreP1;
        }
        else {
            active = R.id.inputP2; passive = R.id.inputP1;
            player = R.id.p2; notPlayer = R.id.p1; label = R.id.throwLabelP2;
            s = scoreP2; s1 = score1P2; s2 = score2P2; s3 = score3P2; t = turnP2;
            scoreDisplay = R.id.scoreP2;
        }

        // get input and check if it is valid and clear input box
        try {
            String str = ((EditText)findViewById(active)).getText().toString();
            // check validity
            sub = parseInt(str);
            if (sub < 0 || sub > 60 || sub == 23 || sub == 29 || sub == 31 || sub == 35
                    || sub == 37 || sub == 41 || sub == 43 || sub == 44 || sub == 46 || sub == 47
                    || sub == 49 || sub == 52 || sub == 53 || sub == 55 || sub == 56 || sub == 58
                    || sub == 59) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
                ((EditText)findViewById(active)).setText("");
                return;
            }
        } catch (Exception e) {
            sub = 0;    // if the input is empty, assign it to 0
        }
        ((EditText)findViewById(active)).setText("");

        // check if it is the first throw
        if (t % 3 == 0) {
            nextThrowLabel = "Second Throw:";
            temp = s;
            temp -= sub;
            if (notBusted(temp)) s1 = temp;
            else { temp = s; s1 = s; s2 = s; s3 = s; t = -1; }
        }
        // check if it is the second throw
        else if (t % 3 == 1) {
            nextThrowLabel = "Third Throw:";
            temp = s1;
            temp -= sub;
            if (notBusted(temp)) s2 = temp;
            else { temp = s; s1 = s; s2 = s; s3 = s; t = -1; }
        }
        // check if it is the third throw
        else {
            nextThrowLabel = "First Throw:";
            temp = s2;
            temp -= sub;
            if (notBusted(temp)) { s3 = temp; s = s3; }
            else { temp = s; s1 = s; s2 = s; s3 = s; t = -1; }
        }

        // update global variables with the new scores
        if (turn / 3 % 2 == 0) { scoreP1 = s; score1P1 = s1; score2P1 = s2; score3P1 = s3; turnP1 = ++t; }
        else { scoreP2 = s; score1P2 = s1; score2P2 = s2; score3P2 = s3; turnP2 = ++t; }

        // update history to include the most recent state
        state2 st = new state2(scoreP1, score1P1, score2P1, score3P1, turnP1, scoreP2, score1P2, score2P2, score3P2, turnP2, turn + 1, null, null);
        findViewById(R.id.back).setEnabled(true);
        findViewById(R.id.forward).setEnabled(false);
        if (curr != history.tail) history.end(curr);
        history.add(st);
        curr = history.tail;

        // update throw label and score
        updateLabel(label, temp, scoreDisplay);

        // check if the layout needs to be changed to ask if the user is finished
        if (temp == 0) { switchOff(); return; }

        // change colors if needed
        if (turn / 3 % 2 != ++turn / 3 % 2) changeColors(active, passive, player, notPlayer);
    }

    public void goBack(View v) {
        // set to game layout
        lastRound = false;
        btnEnter.setEnabled(true);
        btnBack.setEnabled(true);
        btnForward.setEnabled(true);
        isDoneText.setVisibility(View.INVISIBLE);
        btnNo.setVisibility(View.INVISIBLE);
        btnYes.setVisibility(View.INVISIBLE);

        // change status of the back and forward button if needed
        if (curr.prev == history.head) v.setEnabled(false);
        findViewById(R.id.forward).setEnabled(true);

        // change curr to be the previous state and reassign the global variables
        curr = curr.prev;
        scoreP1 = curr.scoreP1; score1P1 = curr.score1P1; score2P1 = curr.score2P1; score3P1 = curr.score3P1; turnP1 = curr.turnP1;
        scoreP2 = curr.scoreP2; score1P2 = curr.score1P2; score2P2 = curr.score2P2; score3P2 = curr.score3P2; turnP2 = curr.turnP2;
        turn = curr.turn;

        // find whose turn it is and get that player's score and turn
        int nextTurn = turn;
        int temp;
        int active, passive, player, notPlayer, label, scoreDisplay;
        int s, s1, s2, s3, t;
        if (turn / 3 % 2 == 0) {
            active = R.id.inputP1; passive = R.id.inputP2;
            player = R.id.p1; notPlayer = R.id.p2; label = R.id.throwLabelP1;
            s = scoreP1; s1 = score1P1; s2 = score2P1; s3 = score3P1; t = turnP1;
            scoreDisplay = R.id.scoreP1;
            findViewById(R.id.inputP1).setEnabled(true);
        }
        else {
            active = R.id.inputP2; passive = R.id.inputP1;
            player = R.id.p2; notPlayer = R.id.p1; label = R.id.throwLabelP2;
            s = scoreP2; s1 = score1P2; s2 = score2P2; s3 = score3P2; t = turnP2;
            scoreDisplay = R.id.scoreP2;
            findViewById(R.id.inputP2).setEnabled(true);
        }

        // get the throw label and the score to be displayed
        if (t % 3 == 0) { nextThrowLabel = "First Throw:"; temp = s; }
        else if (t % 3 == 1) { nextThrowLabel = "Second Throw:"; temp = s1; }
        else { nextThrowLabel = "Third Throw:"; temp = s2; }

        // update throw label and score
        updateLabel(label, temp, scoreDisplay);

        // update global variables
        if (turn / 3 % 2 == 0) { scoreP1 = s; score1P1 = s1; score2P1 = s2; score3P1 = s3; turnP1 = t; }
        else { scoreP2 = s; score1P2 = s1; score2P2 = s2; score3P2 = s3; turnP2 = t; }

        // change colors if needed
        if (turn / 3 % 2 != nextTurn / 3 % 2) changeColors(active, passive, player, notPlayer);
    }

    public void goForward(View v) {
        // change status of the back and forward button if needed
        if (curr.next == history.tail) v.setEnabled(false);
        findViewById(R.id.back).setEnabled(true);

        // change curr to be the next state and reassign the global variables
        curr = curr.next;
        scoreP1 = curr.scoreP1; score1P1 = curr.score1P1; score2P1 = curr.score2P1; score3P1 = curr.score3P1; turnP1 = curr.turnP1;
        scoreP2 = curr.scoreP2; score1P2 = curr.score1P2; score2P2 = curr.score2P2; score3P2 = curr.score3P2; turnP2 = curr.turnP2;
        turn = curr.turn;

        // find whose turn it is and get that player's score and turn
        int temp;
        int active, passive, player, notPlayer, label, scoreDisplay;
        int s, s1, s2, s3, t;
        if (turn / 3 % 2 == 0) {
            active = R.id.inputP1; passive = R.id.inputP2;
            player = R.id.p1; notPlayer = R.id.p2; label = R.id.throwLabelP1;
            s = scoreP1; s1 = score1P1; s2 = score2P1; s3 = score3P1; t = turnP1;
            scoreDisplay = R.id.scoreP1;
        }
        else {
            active = R.id.inputP2; passive = R.id.inputP1;
            player = R.id.p2; notPlayer = R.id.p1; label = R.id.throwLabelP2;
            s = scoreP2; s1 = score1P2; s2 = score2P2; s3 = score3P2; t = turnP2;
            scoreDisplay = R.id.scoreP2;
        }

        // get the throw label and the score to be displayed
        if (t % 3 == 0) { nextThrowLabel = "First Throw:"; temp = s; }
        else if (t % 3 == 1) { nextThrowLabel = "Second Throw:"; temp = s1; }
        else { nextThrowLabel = "Third Throw:"; temp = s2;}

        // update throw label and score
        updateLabel(label, temp, scoreDisplay);

        // update global variables
        if (turn / 3 % 2 == 0) { scoreP1 = s; score1P1 = s1; score2P1 = s2; score3P1 = s3; turnP1 = t; }
        else { scoreP2 = s; score1P2 = s1; score2P2 = s2; score3P2 = s3; turnP2 = t; }

        // check if the layout needs to be changed to ask if the user is finished
        if (temp == 0) { switchOff(); return; }

        // update throw label and score of the previous player and check if the score is 0 if needed
        if (turn % 3 == 0) {
            nextThrowLabel = "First Throw:";
            if (turn / 3 %  2 == 0) {
                updateLabel(R.id.throwLabelP2, score3P2, R.id.scoreP2);
                if (score3P2 == 0) { lastRound = true; switchOff(); return; }
            }
            else {
                updateLabel(R.id.throwLabelP1, score3P1, R.id.scoreP1);
                if (score3P1 == 0) { lastRound = true; switchOff(); return; }
            }

            // change colors
            changeColors(active, passive, player, notPlayer);
        }
    }

    public void finish(View v) {
        // exit the activity and display which player won
        if (turn / 3 % 2 == 0 && !lastRound) Toast.makeText(this, "Player 1 Won", Toast.LENGTH_SHORT).show();
        else if (turn / 3 % 2 == 0 && lastRound) Toast.makeText(this, "Player 2 Won", Toast.LENGTH_SHORT).show();
        else if (turn / 3 % 2 == 1 && !lastRound) Toast.makeText(this, "Player 2 Won", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Player 1 Won", Toast.LENGTH_SHORT).show();
        finish();
    }
}