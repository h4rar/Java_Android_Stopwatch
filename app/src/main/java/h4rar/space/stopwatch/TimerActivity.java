package h4rar.space.stopwatch;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity {
    private long startTimeSecond;
    private long startTimeMinutes;
    private boolean timerRunning;
    private long timeInMillis;
    private long endTime;

    private Button buttonStartPause;
    private Button buttonReset;
    private EditText seconds;
    private EditText minutes;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        seconds = findViewById(R.id.timer_edittext__seconds);
        minutes = findViewById(R.id.timer_edittext__minutes);
        buttonStartPause = findViewById(R.id.timer_button__start_pause);
        buttonReset = findViewById(R.id.timer_button__reset);

        setEditTextZero();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);//Показывает клавиатуру
        seconds.requestFocus();
        seconds.selectAll();

        buttonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    private void startTimer() {

        if (seconds.length() > 0 && minutes.length() > 0) {
            startTimeSecond = Long.parseLong(seconds.getText().toString());
            startTimeMinutes = Long.parseLong(minutes.getText().toString());

            if (startTimeSecond != 0 || startTimeMinutes != 0) {
                timeInMillis = startTimeSecond * 1000 + startTimeMinutes * 60000;

                //чтобы при перевороте время не "терялось" сначала
                //добавляю к currentTimeMillis текущее время телефона, азатем вычитаю
                endTime = System.currentTimeMillis() + timeInMillis;

                countDownTimer = new CountDownTimer(timeInMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeInMillis = millisUntilFinished;
                        updateCountDownText();
                    }

                    @Override
                    public void onFinish() {
                        timerRunning = false;
                        updateButtons();
                    }
                }.start();

                timerRunning = true;
                updateButtons();
            }
        }
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        updateButtons();
    }

    private void resetTimer() {
        setEditTextZero();
        updateButtons();
        buttonReset.setVisibility(View.INVISIBLE);
    }

    private void updateCountDownText() {
        int minutes_time = (int) (timeInMillis / 1000) / 60;
        int seconds_time = (int) (timeInMillis / 1000) % 60;
        String minutesTimeFormatted = String.format(Locale.getDefault(), "%02d", minutes_time);
        String secondsTimeFormatted = String.format(Locale.getDefault(), "%02d", seconds_time);
        seconds.setText(secondsTimeFormatted);
        minutes.setText(minutesTimeFormatted);
    }

    private void updateButtons() {
        if (timerRunning) {
            buttonStartPause.setText(R.string.pause);
            buttonReset.setVisibility(View.INVISIBLE);
        } else {
            buttonStartPause.setText(R.string.start);
            buttonStartPause.setVisibility(View.VISIBLE);

            if (timeInMillis > 1000) {
                buttonReset.setVisibility(View.VISIBLE);
            } else {
                buttonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setEditTextZero() {
        String minutesTimeFormatted = String.format(Locale.getDefault(), "%02d", 0);
        String secondsTimeFormatted = String.format(Locale.getDefault(), "%02d", 0);
        seconds.setText(secondsTimeFormatted);
        minutes.setText(minutesTimeFormatted);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("millis", timeInMillis);
        outState.putBoolean("timerRunning", timerRunning);
        outState.putLong("endTime", endTime);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        timeInMillis = savedInstanceState.getLong("millis");
        timerRunning = savedInstanceState.getBoolean("timerRunning");
        updateButtons();

        if (timerRunning) {
            endTime = savedInstanceState.getLong("endTime");
            timeInMillis = endTime - System.currentTimeMillis();
            startTimer();
        }
    }

    public void selectMinutes(View view) {
        minutes.selectAll();
    }

    public void selectSeconds(View view) {
        seconds.selectAll();
    }
}
