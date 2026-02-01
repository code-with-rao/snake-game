package com.example.snake;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private TextView scoreTV;
    private TextView finalScoreTV;
    private LinearLayout gameOverLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        gameView = findViewById(R.id.gameView);
        scoreTV = findViewById(R.id.scoreTV);
        finalScoreTV = findViewById(R.id.finalScoreTV);
        gameOverLayout = findViewById(R.id.gameOverLayout);

        Button upBtn = findViewById(R.id.upBtn);
        Button downBtn = findViewById(R.id.downBtn);
        Button leftBtn = findViewById(R.id.leftBtn);
        Button rightBtn = findViewById(R.id.rightBtn);
        Button restartBtn = findViewById(R.id.restartBtn);

        // Set up button listeners for direction control
        upBtn.setOnClickListener(v -> gameView.setDirection(GameView.Direction.UP));
        downBtn.setOnClickListener(v -> gameView.setDirection(GameView.Direction.DOWN));
        leftBtn.setOnClickListener(v -> gameView.setDirection(GameView.Direction.LEFT));
        rightBtn.setOnClickListener(v -> gameView.setDirection(GameView.Direction.RIGHT));

        // Restart game button
        restartBtn.setOnClickListener(v -> {
            gameOverLayout.setVisibility(View.GONE);
            gameView.resetGame();
        });

        // Handle game events via listener
        gameView.setGameListener(new GameView.GameListener() {
            @Override
            public void onScoreUpdated(int score) {
                // Update score on the UI thread
                runOnUiThread(() -> scoreTV.setText("Score: " + score));
            }

            @Override
            public void onGameOver(int score) {
                // Show game over screen on the UI thread
                runOnUiThread(() -> {
                    finalScoreTV.setText("Final Score: " + score);
                    gameOverLayout.setVisibility(View.VISIBLE);
                });
            }
        });
    }
}
