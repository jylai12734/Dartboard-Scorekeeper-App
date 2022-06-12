package com.example.dartboardScorekeeper;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

class state {
    // a collection of scores during one instance of the game
    int score, score1, score2, score3, turn, throwCount;
    state next, prev;
    public state(int s, int s1, int s2, int s3, int t, int tc, state n, state p) {
        this.score = s; this.score1 = s1; this.score2 = s2; this.score3 = s3;
        this.turn = t; this.throwCount = tc; this.next = n; this.prev = p;
    }
}

class doublyLinkedList {
    // a collection of states connected using a doubly linked list
    state head, tail;
    public doublyLinkedList() {
        this.head = null;
        this.tail = null;
    }
    public void add(state s) {
        tail.next = s;
        s.prev = tail;
        tail = s;
    }
    public void end(state s) {
        s.next.prev = null;
        s.next = null;
        this.tail = s;
    }
}

public class OnePlayerActivity extends AppCompatActivity implements View.OnClickListener {
    // declare global variables
    Button btnEnter, btnBack, btnForward, btnNo, btnYes;
    EditText source;
    TextView isDoneText;
    Boolean points, split;
    int score, score1, score2, score3, turn, throwCount;
    String nextThrowLabel;
    state curr;
    doublyLinkedList history; // history allows user to undo and redo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player);

        // retrieve data from the main activity and initialize the score, turn, and throw count
        Intent i = getIntent();
        points = i.getBooleanExtra("points", false);
        split = i.getBooleanExtra("split", false);
        if (points) {
            score = 501; score1 = 501; score2 = 501; score3 = 501;
            String newScore = "Score: 501";
            ((TextView) findViewById(R.id.score)).setText(newScore);
        }
        else {
            score = 301; score1 = 301; score2 = 301; score3 = 301;
        }
        turn = 0; throwCount = 0;

        // initialize history to be the starting state of the game
        history = new doublyLinkedList();
        state s = new state(score, score1, score2, score3, turn, throwCount, null, null);
        history.head = s;
        history.tail = s;
        curr = s;

        // assign OnClickListeners to all the buttons
        (btnEnter = findViewById(R.id.submit)).setOnClickListener(this);
        (btnBack = findViewById(R.id.backButton)).setOnClickListener(this);
        (btnForward = findViewById(R.id.forwardButton)).setOnClickListener(this);
        (btnNo = findViewById(R.id.noButton)).setOnClickListener(this);
        (btnYes = findViewById(R.id.yesButton)).setOnClickListener(this);
        source = findViewById(R.id.input);
        isDoneText = findViewById(R.id.isDoneText);

        // disable the back and forward button in the beginning of the game
        btnBack.setEnabled(false);
        btnForward.setEnabled(false);
    }

    public void onClick(View v) {
        // direct the clicked button to its corresponding method
        if (v.getId() == R.id.submit) reduce(v);
        else if (v.getId() == R.id.backButton || v.getId() == R.id.noButton) goBack(v);
        else if (v.getId() == R.id.forwardButton) goForward(v);
        else finish(v);
    }

    public boolean notBusted(int temp) {
        // check if busted
        if (temp < 0 || (temp == 1 && !split)) {
            Toast.makeText(this, "Busted", Toast.LENGTH_SHORT).show();
            turn = -1;
            nextThrowLabel = "First Throw:";
            return false;
        }
        return true;
    }

    public void updateLabel(int temp) {
        // update the score label to include the new score
        ((TextView)findViewById(R.id.throwCountLabel)).setText(nextThrowLabel);
        String newScore = "Score: " + (Integer) temp;
        ((TextView)findViewById(R.id.score)).setText(newScore);
    }

    public void switchAll() {
        // switch the layout of the screen between the game layout
        // and the one that asks if the user is finished
        source.setEnabled(!source.isEnabled());
        btnEnter.setEnabled(!btnEnter.isEnabled());
        btnBack.setEnabled(!btnBack.isEnabled());
        if (isDoneText.getVisibility() == View.VISIBLE) isDoneText.setVisibility(View.INVISIBLE);
        else isDoneText.setVisibility(View.VISIBLE);
        if (btnNo.getVisibility() == View.VISIBLE) btnNo.setVisibility(View.INVISIBLE);
        else btnNo.setVisibility(View.VISIBLE);
        if (btnYes.getVisibility() == View.VISIBLE) btnYes.setVisibility(View.INVISIBLE);
        else btnYes.setVisibility(View.VISIBLE);
    }

    public void reduce(View v) {
        // get input from the user, clear input box, and update throwCount
        int sub, temp;
        try {
            String s = ((EditText)findViewById(R.id.input)).getText().toString();
            // check if input is valid
            sub = parseInt(s);
            if (sub < 0 || sub > 60 || sub == 23 || sub == 29 || sub == 31 || sub == 35
                    || sub == 37 || sub == 41 || sub == 43 || sub == 44 || sub == 46 || sub == 47
                    || sub == 49 || sub == 52 || sub == 53 || sub == 55 || sub == 56 || sub == 58
                    || sub == 59) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
                ((EditText)findViewById(R.id.input)).setText("");
                return;
            }
        } catch (Exception e) {
            sub = 0;    // if the input is empty, assign it to 0
        }
        ((EditText)findViewById(R.id.input)).setText("");
        throwCount++;

        // check if it is the first throw
        if (turn % 3 == 0) {
            nextThrowLabel = "Second Throw:";
            temp = score;
            temp -= sub;
            if (notBusted(temp)) score1 = temp;
            else { temp = score; score1 = score; score2 = score; score3 = score; }
        }
        // check if it is the second throw
        else if (turn % 3 == 1) {
            nextThrowLabel = "Third Throw:";
            temp = score1;
            temp -= sub;
            if (notBusted(temp)) score2 = temp;
            else { temp = score; score1 = score; score2 = score; score3 = score; }
        }
        // check if it is the third throw
        else {
            nextThrowLabel = "First Throw:";
            temp = score2;
            temp -= sub;
            if (notBusted(temp)) { score3 = temp; score = score3; }
            else { temp = score; score1 = score; score2 = score; score3 = score; }
        }

        // update history to include the most recent state
        state s = new state(score, score1, score2, score3, ++turn, throwCount,null, null);
        btnBack.setEnabled(true);
        btnForward.setEnabled(false);
        if (curr != history.tail) history.end(curr);
        history.add(s);
        curr = history.tail;

        // update score label change the layout of the page to ask if the user is finished if needed
        updateLabel(temp);
        if (temp == 0) switchAll();
    }

    public void goBack(View v) {
        // check if the score of the current state reached 0
        if (curr.score == 0 || curr.score1 == 0 || curr.score2 == 0 || curr.score3 == 0) switchAll();

        // change status of the back and forward button if needed
        if (curr.prev == history.head) v.setEnabled(false);
        btnForward.setEnabled(true);

        // change curr to be the previous state and reassign the global variables
        curr = curr.prev;
        score = curr.score; score1 = curr.score1; score2 = curr.score2; score3 = curr.score3;
        turn = curr.turn; throwCount = curr.throwCount;

        // get the throw label and the score to be displayed
        int temp;
        if (turn % 3 == 0) { nextThrowLabel = "First Throw:"; temp = score; }
        else if (turn % 3 == 1) { nextThrowLabel = "Second Throw:"; temp = score1; }
        else { nextThrowLabel = "Third Throw:"; temp = score2; }

        // update the score label
        updateLabel(temp);
    }

    public void goForward(View v) {
        // change status of the back and forward button if needed
        if (curr.next == history.tail) v.setEnabled(false);
        findViewById(R.id.backButton).setEnabled(true);

        // change curr to be the next state and reassign the global variables
        curr = curr.next;
        score = curr.score; score1 = curr.score1; score2 = curr.score2; score3 = curr.score3;
        turn = curr.turn; throwCount = curr.throwCount;

        // get the throw label and the score to be displayed
        int temp;
        if (turn % 3 == 0) { nextThrowLabel = "First Throw:"; temp = score; }
        else if (turn % 3 == 1) { nextThrowLabel = "Second Throw:"; temp = score1; }
        else { nextThrowLabel = "Third Throw:"; temp = score2; }

        // update score label change the layout of the page to ask if the user is finished if needed
        updateLabel(temp);
        if (temp == 0) switchAll();
    }

    public void finish(View v) {
        // exit the activity if the user finished the game and display the total amount of throws
        String throwCountString = ((Integer) (throwCount)).toString();
        Toast.makeText(this, "You finished in " + throwCountString + " throws", Toast.LENGTH_SHORT).show();
        finish();
    }
}