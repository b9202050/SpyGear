package ntu.embedded.spycar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoyStickView extends View implements Runnable {
    // Constants
    private final double RAD = 57.2957795;
    public final static long DEFAULT_LOOP_INTERVAL = 100; // 100 ms
    public final static int FRONT = 3;
    public final static int FRONT_RIGHT = 4;
    public final static int RIGHT = 5;
    public final static int RIGHT_BOTTOM = 6;
    public final static int BOTTOM = 7;
    public final static int BOTTOM_LEFT = 8;
    public final static int LEFT = 1;
    public final static int LEFT_FRONT = 2;

    private Thread mThread = new Thread(this);
    private long mLoopInterval = DEFAULT_LOOP_INTERVAL;
    private int mTouchPosX = 0; // Touch x position
    private int mTouchPosY = 0; // Touch y position
    private double mCenterX = 0; // Center view x position
    private double mCenterY = 0; // Center view y position
    private Paint mMainCircle;
    private Paint m2ndCircle;
    private Paint mButton;
    private Paint mHorizontalLine;
    private Paint mVerticalLine;
    private int mJoystickRadius;
    private int mButtonRadius;
    private int mLastAngle = 0;
    private int mLastPower = 0;

    private boolean mEnable = false;

    private OnJoystickMoveListener mOnJoystickMoveListener; // Listener
    public interface OnJoystickMoveListener {
        void onValueChanged(int angle, int power, int direction);
    }

    public JoyStickView(Context context) {
        super(context);
    }

    public JoyStickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystickView();
    }

    public JoyStickView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        initJoystickView();
    }

    protected void initJoystickView() {
        mMainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMainCircle.setColor(Color.WHITE);
        mMainCircle.setStyle(Paint.Style.FILL_AND_STROKE);

        m2ndCircle = new Paint();
        m2ndCircle.setColor(Color.GREEN);
        m2ndCircle.setStyle(Paint.Style.STROKE);

        mVerticalLine = new Paint();
        mVerticalLine.setStrokeWidth(5);
        mVerticalLine.setColor(Color.RED);

        mHorizontalLine = new Paint();
        mHorizontalLine.setStrokeWidth(2);
        mHorizontalLine.setColor(Color.BLACK);

        mButton = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButton.setColor(Color.RED);
        mButton.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onFinishInflate() {
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEnable = enabled;
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        // before measure, get the center of view
        mTouchPosX = (int) getWidth() / 2;
        mTouchPosY = (int) getWidth() / 2;
        int d = Math.min(xNew, yNew);
        mButtonRadius = (int) (d / 2 * 0.25);
        mJoystickRadius = (int) (d / 2 * 0.75);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // setting the measured values to resize the view to a certain width and
        // height
        int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));

        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result = 0;

        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        mCenterX = (getWidth()) / 2;
        mCenterY = (getHeight()) / 2;

        // painting the main circle
        canvas.drawCircle((int) mCenterX, (int) mCenterY, mJoystickRadius,
                mMainCircle);
        // painting the secondary circle
        canvas.drawCircle((int) mCenterX, (int) mCenterY, mJoystickRadius / 2,
                m2ndCircle);
        // paint lines
        canvas.drawLine((float) mCenterX, (float) mCenterY, (float) mCenterX,
                (float) (mCenterY - mJoystickRadius), mVerticalLine);
        canvas.drawLine((float) (mCenterX - mJoystickRadius), (float) mCenterY,
                (float) (mCenterX + mJoystickRadius), (float) mCenterY,
                mHorizontalLine);
        canvas.drawLine((float) mCenterX, (float) (mCenterY + mJoystickRadius),
                (float) mCenterX, (float) mCenterY, mHorizontalLine);

        // painting the move mButton
        canvas.drawCircle(mTouchPosX, mTouchPosY, mButtonRadius, mButton);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // ignore all touch event until enabled
        if (!mEnable) return true;

        mTouchPosX = (int) event.getX();
        mTouchPosY = (int) event.getY();
        double abs = Math.sqrt((mTouchPosX - mCenterX) * (mTouchPosX - mCenterX)
                + (mTouchPosY - mCenterY) * (mTouchPosY - mCenterY));
        if (abs > mJoystickRadius) {
            mTouchPosX = (int) ((mTouchPosX - mCenterX) * mJoystickRadius / abs + mCenterX);
            mTouchPosY = (int) ((mTouchPosY - mCenterY) * mJoystickRadius / abs + mCenterY);
        }
        invalidate();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mTouchPosX = (int) mCenterX;
            mTouchPosY = (int) mCenterY;
            mThread.interrupt();
            if (mOnJoystickMoveListener != null)
                mOnJoystickMoveListener.onValueChanged(getAngle(), getPower(),
                        getDirection());
        }
        if (mOnJoystickMoveListener != null
                && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mThread != null && mThread.isAlive()) {
                mThread.interrupt();
            }
            mThread = new Thread(this);
            mThread.start();
            if (mOnJoystickMoveListener != null)
                mOnJoystickMoveListener.onValueChanged(getAngle(), getPower(),
                        getDirection());
        }
        return true;
    }

    private int getAngle() {
        if (mTouchPosX > mCenterX) {
            if (mTouchPosY < mCenterY) {
                return mLastAngle = (int) (Math.atan((mTouchPosY - mCenterY)
                        / (mTouchPosX - mCenterX))
                        * RAD + 90);
            } else if (mTouchPosY > mCenterY) {
                return mLastAngle = (int) (Math.atan((mTouchPosY - mCenterY)
                        / (mTouchPosX - mCenterX)) * RAD) + 90;
            } else {
                return mLastAngle = 90;
            }
        } else if (mTouchPosX < mCenterX) {
            if (mTouchPosY < mCenterY) {
                return mLastAngle = (int) (Math.atan((mTouchPosY - mCenterY)
                        / (mTouchPosX - mCenterX))
                        * RAD - 90);
            } else if (mTouchPosY > mCenterY) {
                return mLastAngle = (int) (Math.atan((mTouchPosY - mCenterY)
                        / (mTouchPosX - mCenterX)) * RAD) - 90;
            } else {
                return mLastAngle = -90;
            }
        } else {
            if (mTouchPosY <= mCenterY) {
                return mLastAngle = 0;
            } else {
                if (mLastAngle < 0) {
                    return mLastAngle = -180;
                } else {
                    return mLastAngle = 180;
                }
            }
        }
    }

    private int getPower() {
        return (int) (100 * Math.sqrt((mTouchPosX - mCenterX)
                * (mTouchPosX - mCenterX) + (mTouchPosY - mCenterY)
                * (mTouchPosY - mCenterY)) / mJoystickRadius);
    }

    private int getDirection() {
        if (mLastPower == 0 && mLastAngle == 0) {
            return 0;
        }
        int a = 0;
        if (mLastAngle <= 0) {
            a = (mLastAngle * -1) + 90;
        } else if (mLastAngle > 0) {
            if (mLastAngle <= 90) {
                a = 90 - mLastAngle;
            } else {
                a = 360 - (mLastAngle - 90);
            }
        }

        int direction = (int) (((a + 22) / 45) + 1);

        if (direction > 8) {
            direction = 1;
        }
        return direction;
    }

    public void setOnJoystickMoveListener(OnJoystickMoveListener listener,
                                          long repeatInterval) {
        this.mOnJoystickMoveListener = listener;
        this.mLoopInterval = repeatInterval;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            post(new Runnable() {
                public void run() {
                    if (mOnJoystickMoveListener != null)
                        mOnJoystickMoveListener.onValueChanged(getAngle(),
                                getPower(), getDirection());
                }
            });
            try {
                Thread.sleep(mLoopInterval);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
