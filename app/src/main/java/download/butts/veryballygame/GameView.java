package download.butts.veryballygame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private List<Ball> balls;
    private Random random;
    private boolean safeToAdd = true;
    private boolean fingerDown = false;
    private int fingerX = 0;
    private int fingerY = 0;
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        random = new Random();

        thread = new GameThread(getHolder(), this);

        setFocusable(true);
        balls = new ArrayList<>();
        balls.add(new Ball(130, 130, randomNumber(50, 150)));
        balls.add(new Ball(130, -130, randomNumber(50, 150)));
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }

    }
    public int randomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            fingerDown = false;
        }
        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_MOVE) {
            return false;
        }
        fingerDown = true;
        fingerX = (int) event.getX();
        fingerY = (int) event.getY();
        while (!safeToAdd) {}
        safeToAdd = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            balls.add(new Ball((int) event.getX(), (int) event.getY(), randomNumber(50, 150)));
        }
        safeToAdd = true;
        return true;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        while (!safeToAdd) {}
        safeToAdd = false;
        canvas.drawColor(Color.BLACK);
        for (Ball ball : balls) {
            ball.draw(canvas);
        }

        for (Ball ball : balls) {

        }

        List<Ball> copy = new ArrayList<>(balls);
        for (int i = 0; i < balls.size(); i++) {
            Ball a = balls.get(i);
            if (a.shouldBeDead()) {
                copy.remove(a);
                for (int k = 0;k<randomNumber(-1, 5);k++) {
                    copy.add(new Ball(randomNumber(0, canvas.getWidth()), randomNumber(0, canvas.getHeight()), randomNumber(50, 450)));
                }
            }
            if (fingerDown) {
                //copy.add(new Ball(fingerX, fingerY, randomNumber(50, 450)));
                a.setX(fingerX);
                a.setY(fingerY);
            }
            a.draw(canvas);
            for (int j = i+1; j < balls.size(); j++) {
                Ball b = balls.get(j);
                if (a.detectCollision(b)) {
                    a.moveAwayFrom(b.getX(), b.getY());
                    b.moveAwayFrom(a.getX(), a.getY());
                }
            }
        }
        balls = copy;

        if (balls.size() > 256) {
            Log.i("testing", "full of balls! removing some.");
            for (int i=balls.size();balls.size()>200;i--) {
                balls.remove(i-1);
            }
        }
        Paint textpaint = new Paint();
        textpaint.setColor(Color.WHITE);
        textpaint.setTextSize(45);
        canvas.drawText("balls: ".concat(String.valueOf(balls.size())), 0, 50, textpaint);
        safeToAdd = true;

    }
}

