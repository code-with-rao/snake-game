package com.example.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {

    // Game constants
    private static final int GRID_SIZE = 20; // Number of blocks in a row/column
    private static final long MOVE_DELAY = 150; // Speed of the snake in ms

    // Game state
    private List<Point> snake;
    private Point fruit;
    private Direction currentDirection = Direction.RIGHT;
    private boolean gameOver = false;
    private int score = 0;

    // Painting tools
    private Paint snakePaint;
    private Paint fruitPaint;
    private Paint gridPaint;

    // Game loop
    private Handler handler = new Handler();
    private Runnable moveRunnable = new Runnable() {
        @Override
        public void run() {
            if (!gameOver) {
                moveSnake();
                invalidate(); // Redraw the view
                handler.postDelayed(this, MOVE_DELAY);
            }
        }
    };

    // Callback for game events
    public interface GameListener {
        void onScoreUpdated(int score);
        void onGameOver(int score);
    }

    private GameListener listener;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        snakePaint = new Paint();
        snakePaint.setColor(Color.GREEN);
        snakePaint.setStyle(Paint.Style.FILL);

        fruitPaint = new Paint();
        fruitPaint.setColor(Color.RED);
        fruitPaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint();
        gridPaint.setColor(Color.DKGRAY);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);

        resetGame();
    }

    public void setGameListener(GameListener listener) {
        this.listener = listener;
    }

    public void resetGame() {
        snake = new ArrayList<>();
        // Start with 3 blocks in the middle
        snake.add(new Point(GRID_SIZE / 2, GRID_SIZE / 2));
        snake.add(new Point(GRID_SIZE / 2 - 1, GRID_SIZE / 2));
        snake.add(new Point(GRID_SIZE / 2 - 2, GRID_SIZE / 2));

        currentDirection = Direction.RIGHT;
        score = 0;
        gameOver = false;
        spawnFruit();
        
        handler.removeCallbacks(moveRunnable);
        handler.postDelayed(moveRunnable, MOVE_DELAY);
        
        if (listener != null) {
            listener.onScoreUpdated(score);
        }
    }

    private void spawnFruit() {
        Random random = new Random();
        int x, y;
        boolean onSnake;
        do {
            x = random.nextInt(GRID_SIZE);
            y = random.nextInt(GRID_SIZE);
            onSnake = false;
            for (Point p : snake) {
                if (p.x == x && p.y == y) {
                    onSnake = true;
                    break;
                }
            }
        } while (onSnake);
        fruit = new Point(x, y);
    }

    public void setDirection(Direction newDirection) {
        // Prevent reverse direction
        if ((currentDirection == Direction.UP && newDirection != Direction.DOWN) ||
            (currentDirection == Direction.DOWN && newDirection != Direction.UP) ||
            (currentDirection == Direction.LEFT && newDirection != Direction.RIGHT) ||
            (currentDirection == Direction.RIGHT && newDirection != Direction.LEFT)) {
            currentDirection = newDirection;
        }
    }

    private void moveSnake() {
        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);

        switch (currentDirection) {
            case UP: newHead.y--; break;
            case DOWN: newHead.y++; break;
            case LEFT: newHead.x--; break;
            case RIGHT: newHead.x++; break;
        }

        // Check wall collisions
        if (newHead.x < 0 || newHead.x >= GRID_SIZE || newHead.y < 0 || newHead.y >= GRID_SIZE) {
            endGame();
            return;
        }

        // Check self collisions
        for (Point p : snake) {
            if (p.x == newHead.x && p.y == newHead.y) {
                endGame();
                return;
            }
        }

        snake.add(0, newHead);

        // Check fruit collision
        if (newHead.x == fruit.x && newHead.y == fruit.y) {
            score++;
            if (listener != null) {
                listener.onScoreUpdated(score);
            }
            spawnFruit();
        } else {
            snake.remove(snake.size() - 1); // Remove tail if no fruit eaten
        }
    }

    private void endGame() {
        gameOver = true;
        if (listener != null) {
            listener.onGameOver(score);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int cellSize = Math.min(viewWidth, viewHeight) / GRID_SIZE;
        
        // Center the grid
        int offsetX = (viewWidth - cellSize * GRID_SIZE) / 2;
        int offsetY = (viewHeight - cellSize * GRID_SIZE) / 2;

        // Draw snake
        for (Point p : snake) {
            canvas.drawRect(
                offsetX + p.x * cellSize + 1,
                offsetY + p.y * cellSize + 1,
                offsetX + (p.x + 1) * cellSize - 1,
                offsetY + (p.y + 1) * cellSize - 1,
                snakePaint
            );
        }

        // Draw fruit
        canvas.drawRect(
            offsetX + fruit.x * cellSize + 1,
            offsetY + fruit.y * cellSize + 1,
            offsetX + (fruit.x + 1) * cellSize - 1,
            offsetY + (fruit.y + 1) * cellSize - 1,
            fruitPaint
        );
        
        // Optional: Draw grid lines for better visibility
        for (int i = 0; i <= GRID_SIZE; i++) {
            canvas.drawLine(offsetX, offsetY + i * cellSize, offsetX + GRID_SIZE * cellSize, offsetY + i * cellSize, gridPaint);
            canvas.drawLine(offsetX + i * cellSize, offsetY, offsetX + i * cellSize, offsetY + GRID_SIZE * cellSize, gridPaint);
        }
    }
}
