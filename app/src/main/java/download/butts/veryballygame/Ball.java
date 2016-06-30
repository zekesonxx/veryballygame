package download.butts.veryballygame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

public class Ball {
    public enum BallMode {TopLeft, TopRight, BottomLeft, BottomRight}
    public final static double TERMINAL_VELOCITY = 26;
    public final static double PERTICK_SLOWDOWN = 0.4;
    public final static double LIFETIME=600;
    private int life=0;
    private int x;
    private int y;
    private int radius;
    private double xVelocity = 0.1;
    private double yVelocity = 0.1;
    private boolean moving = true;
    private BallMode mode;
    private int color;

    public Ball(int x, int y, int life) {
        Random random = new Random();
        this.x = x;
        this.y = y;
        this.life = life;
        this.radius = random.nextInt(20- 10+ 1) + 10;
        this.color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        this.mode = BallMode.values()[random.nextInt(BallMode.values().length)];
    }

    public int randomColor(Random random) {
        switch (random.nextInt(6-1+1)+1) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.CYAN;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.MAGENTA;
            case 5:
                return Color.YELLOW;
            case 6:
                return Color.RED;
        }
        throw new IllegalArgumentException("what the fuck happened mate");
    }

    public boolean shouldBeDead() {
        return this.life >= LIFETIME;
    }

    public void draw(Canvas canvas) {
        life++;
        Paint paint = new Paint();
        paint.setColor(this.color);
        drawLine(canvas);
        canvas.drawCircle(x, y, radius, paint);
        x += xVelocity;
        y += yVelocity;
        if (this.detectCollision(canvas.getWidth(), canvas.getHeight())) {
            this.bounce(canvas.getWidth(), canvas.getHeight());
        }
    }

    public void drawLine(Canvas canvas) {
        Paint linepaint = new Paint();
        linepaint.setColor(Color.GRAY);
        switch (mode) {
            case TopLeft:
                canvas.drawLine(0, 0, x, y, linepaint);
                this.setxVelocity(getxVelocity()-PERTICK_SLOWDOWN);
                this.setyVelocity(getyVelocity()-PERTICK_SLOWDOWN);
                break;
            case TopRight:
                canvas.drawLine(canvas.getWidth(), 0, x, y, linepaint);
                this.setxVelocity(getxVelocity()+PERTICK_SLOWDOWN);
                this.setyVelocity(getyVelocity()-PERTICK_SLOWDOWN);
                break;
            case BottomLeft:
                canvas.drawLine(0, canvas.getHeight(), x, y, linepaint);
                this.setxVelocity(getxVelocity()-PERTICK_SLOWDOWN);
                this.setyVelocity(getyVelocity()+PERTICK_SLOWDOWN);
            case BottomRight:
                canvas.drawLine(canvas.getWidth(), canvas.getHeight(), x, y, linepaint);
                this.setxVelocity(getxVelocity()+PERTICK_SLOWDOWN);
                this.setyVelocity(getyVelocity()+PERTICK_SLOWDOWN);
        }

    }

    /**
     * Detect a collision within the walls of the playing field
     * @param width
     * @param height
     * @return
     */
    public boolean detectCollision(int width, int height) {
        if (this.x-radius<radius) {
            //top side
            return true;
        } else if (this.y-radius<radius) {
            //left side
            return true;
        } else if (this.x+radius>width) {
            //bottom side
            return true;
        } else if (this.y+radius>height) {
            //right side
            return true;
        }
        return false;
    }

    public void bounce(int canvasWidth, int canvasHeight) {
        boolean hitTop = (this.x-radius<0);
        boolean hitBottom = (this.x+radius>canvasWidth);
        boolean hitLeft = (this.y-radius<0);
        boolean hitRight = (this.y+radius>canvasHeight);
        if (hitTop) {
            //top side
            this.xVelocity = -this.xVelocity;
        } else if (hitBottom) {
            //bottom side
            this.xVelocity = -this.xVelocity;
        }
        if (hitRight) {
            //right side
            this.yVelocity = -this.yVelocity;
        } else if (hitLeft) {
            //left side
            this.yVelocity = -this.yVelocity;
        }

        //make sure we can't ever go outside of the area
        //this prevents the game from glitching out
        int offset = 3*radius;
        if (this.x<0) {
            this.x = offset;
        } else if (this.x>canvasWidth) {
            this.x = canvasWidth-offset;
        }
        if (this.y<0) {
            this.y = offset;
        } else if (this.y>canvasHeight) {
            this.y = canvasHeight-offset;
        }

        //detect possible corner glitching
        int howManyBooleansAreTrue = (hitTop ? 1 : 0) + (hitBottom ? 1 : 0) + (hitRight ? 1 : 0) + (hitLeft ? 1: 0);
        boolean corner = howManyBooleansAreTrue >= 2;
        if (corner) {

        }

    }

    public void moveAwayFrom(int targetX, int targetY) {
        this.moveAwayFrom(targetX, targetY, 1);
    }

    public void moveAwayFrom(int targetX, int targetY, int multiplier) {
        if (targetX < this.x) {
            //to the left of the ball
            this.setxVelocity(this.getxVelocity()*2*multiplier);
        } else {
            //to the right of the ball
            this.setxVelocity(-this.getxVelocity()*multiplier);
        }

        if (targetY < this.y) {
            //above the ball
            this.setyVelocity(this.getyVelocity()*2*multiplier);
        } else {
            //below the ball
            this.setyVelocity(-this.getyVelocity()*multiplier);
        }
    }

    public boolean detectCollision(Ball otherBall) {
        return GameMath.calculateDistance(this.x, this.y, otherBall.getX(), otherBall.getY()) < (radius+otherBall.radius);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(double xVelocity) {
        this.xVelocity = (xVelocity > TERMINAL_VELOCITY ? TERMINAL_VELOCITY : xVelocity);
    }

    public double getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(double yVelocity) {
        this.yVelocity = (yVelocity > TERMINAL_VELOCITY ? TERMINAL_VELOCITY : yVelocity);
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }
}