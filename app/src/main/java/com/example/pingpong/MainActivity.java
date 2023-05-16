package com.example.pingpong;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    private Paint textopaint;
    private float mPlayerPaddleX, mPlayerPaddleY, mPlayerPaddleWidth, mPlayerPaddleHeight;
    private float mEnemyPaddleX, mEnemyPaddleY, mEnemyPaddleWidth, mEnemyPaddleHeight;

    private float mBallX, mBallY, mBallRadius, mBallSpeedX, mBallSpeedY;
    private float mScreenWidth, mScreenHeight;
    private static final int PADDLE_COLOR = Color.YELLOW;
    private static final int BALL_COLOR = Color.RED;

    private int jogadorScore;

    private int inimigoScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSurfaceView = new SurfaceView(this);
        setContentView(mSurfaceView);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        mSurfaceView.setFocusable(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);


        textopaint = new Paint();
        textopaint.setAntiAlias(true);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
    }




    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape mode
            mPlayerPaddleWidth = mScreenWidth * 0.1f;
            mPlayerPaddleHeight = mScreenHeight * 0.05f;
            mPlayerPaddleX = (mScreenWidth - mPlayerPaddleWidth) / 2f;
            mPlayerPaddleY = mScreenHeight * 0.9f;

            mEnemyPaddleWidth = mScreenWidth * 0.1f;
            mEnemyPaddleHeight = mScreenHeight * 0.05f;
            mEnemyPaddleX = (mScreenWidth - mEnemyPaddleWidth) / 2f;
            mEnemyPaddleY = mScreenHeight * 0.1f;
        } else {
            // Portrait mode
            mPlayerPaddleWidth = mScreenWidth * 0.2f;
            mPlayerPaddleHeight = mScreenHeight * 0.05f;
            mPlayerPaddleX = (mScreenWidth - mPlayerPaddleWidth) / 2f;
            mPlayerPaddleY = mScreenHeight * 0.9f;

            mEnemyPaddleWidth = mScreenWidth * 0.2f;
            mEnemyPaddleHeight = mScreenHeight * 0.05f;
            mEnemyPaddleX = (mScreenWidth - mEnemyPaddleWidth) / 2f;
            mEnemyPaddleY = mScreenHeight * 0.1f;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        //PEGA A ALTURA E LAGURA DA TELA
        mScreenWidth = mSurfaceView.getWidth();
        mScreenHeight = mSurfaceView.getHeight();

        //TAMANHO DA BOLA
        mBallRadius = mScreenWidth * 0.03f;
        mBallX = mScreenWidth / 2f;
        mBallY = mScreenHeight / 2f;

        //VELOCIDADE DA BOLA
        mBallSpeedX = mScreenWidth * 0.009f;;
        mBallSpeedY = mScreenHeight * 0.009f;;

        //tamanho barra player
        mPlayerPaddleWidth = mScreenWidth * 0.2f;
        mPlayerPaddleHeight = mScreenHeight * 0.05f;
        mPlayerPaddleX = (mScreenWidth - mPlayerPaddleWidth) / 2f;
        mPlayerPaddleY = mScreenHeight * 0.9f;

        //tamanho barra inimigo
        mEnemyPaddleWidth = mScreenWidth * 0.2f;
        mEnemyPaddleHeight = mScreenHeight * 0.05f;
        mEnemyPaddleX = (mScreenWidth - mEnemyPaddleWidth) / 2f;
        mEnemyPaddleY = mScreenHeight * 0.1f;

        startGameLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Do nothing
    }

    private void startGameLoop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    update();
                    draw();
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        Log.e("Ping Pong", "Game Loop interrompido!", e);
                    }
                }
            }
        }).start();
    }


    private void draw() {
        Canvas canvas = mSurfaceHolder.lockCanvas();

        if (canvas != null) {

            Paint linePaint = new Paint();
            linePaint.setColor(Color.WHITE);
            linePaint.setStrokeWidth(10);
            canvas.drawLine(0, mScreenHeight/2, mScreenWidth, mScreenHeight/2, linePaint);
            canvas.drawColor(Color.GRAY);

            //DESENHA A BARRA DE AMBOS
            mPaint.setColor(PADDLE_COLOR);
            canvas.drawRect(mPlayerPaddleX, mPlayerPaddleY, mPlayerPaddleX + mPlayerPaddleWidth, mPlayerPaddleY + mPlayerPaddleHeight, mPaint);
            canvas.drawRect(mEnemyPaddleX, mEnemyPaddleY, mEnemyPaddleX + mEnemyPaddleWidth, mEnemyPaddleY + mEnemyPaddleHeight, mPaint);

            //DESENHA BOLA
            mPaint.setColor(BALL_COLOR);
            canvas.drawCircle(mBallX, mBallY, mBallRadius, mPaint);

            // DESENHA A PONTUAÇÃO
            textopaint.setTextSize(40f);
            textopaint.setColor(Color.WHITE);
            canvas.drawText("Player Score: " + jogadorScore, mScreenWidth * 0.1f, mScreenHeight * 0.95f, textopaint);
            canvas.drawText("Enemy Score: " + inimigoScore, mScreenWidth * 0.1f, mScreenHeight * 0.05f + 40f, textopaint);

            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }


    private void update() {
        mBallX += mBallSpeedX;
        mBallY += mBallSpeedY;

        if (mBallX - mBallRadius < 0) {
            mBallX = mBallRadius;
            mBallSpeedX = -mBallSpeedX;
            inimigoScore ++;
            //Log.d("SCORE", "INIMIGO FEZ 1 PONTO:"+inimigoScore);


        } else if (mBallX + mBallRadius > mScreenWidth) {
            mBallX = mScreenWidth - mBallRadius;
            mBallSpeedX = -mBallSpeedX;
        }

        if (mBallY - mBallRadius < 0) {
            mBallY = mBallRadius;
            mBallSpeedY = -mBallSpeedY;
            jogadorScore ++;
            //Log.d("SCORE", "JOGADOR FEZ 1 PONTO:"+jogadorScore);

        } else if (mBallY + mBallRadius > mScreenHeight) {
            mBallY = mScreenHeight - mBallRadius;
            mBallSpeedY = -mBallSpeedY;
        }

        if (mBallY + mBallRadius >= mPlayerPaddleY) {
            float paddleLeft = mPlayerPaddleX;
            float paddleRight = mPlayerPaddleX + mPlayerPaddleWidth;
            float paddleTop = mPlayerPaddleY;
            float paddleBottom = mPlayerPaddleY + mPlayerPaddleHeight;

            if (mBallX + mBallRadius >= paddleLeft && mBallX - mBallRadius <= paddleRight) {
                mBallY = paddleTop - mBallRadius;
                mBallSpeedY = -mBallSpeedY;
            } else if (mBallY + mBallRadius >= paddleTop && mBallY - mBallRadius <= paddleBottom) {
                if (mBallX + mBallRadius >= paddleLeft && mBallX < mPlayerPaddleX) {
                    mBallX = paddleLeft - mBallRadius;
                    mBallSpeedX = -mBallSpeedX;
                } else if (mBallX - mBallRadius <= paddleRight && mBallX > mPlayerPaddleX + mPlayerPaddleWidth) {
                    mBallX = paddleRight + mBallRadius;
                    mBallSpeedX = -mBallSpeedX;
                }
            }
        }

        if (mBallY - mBallRadius <= mEnemyPaddleY + mEnemyPaddleHeight) {
            float paddleLeft = mEnemyPaddleX;
            float paddleRight = mEnemyPaddleX + mEnemyPaddleWidth;
            float paddleTop = mEnemyPaddleY;
            float paddleBottom = mEnemyPaddleY + mEnemyPaddleHeight;

            if (mBallX + mBallRadius >= paddleLeft && mBallX - mBallRadius <= paddleRight) {
                mBallY = paddleBottom + mBallRadius;
                mBallSpeedY = -mBallSpeedY;
            } else if (mBallY - mBallRadius <= paddleBottom && mBallY + mBallRadius >= paddleTop) {
                if (mBallX + mBallRadius >= paddleLeft && mBallX < mEnemyPaddleX) {
                    mBallX = paddleLeft - mBallRadius;
                    mBallSpeedX = -mBallSpeedX;
                } else if (mBallX - mBallRadius <= paddleRight && mBallX > mEnemyPaddleX + mEnemyPaddleWidth) {
                    mBallX = paddleRight + mBallRadius;
                    mBallSpeedX = -mBallSpeedX;
                }
            }
        }
        float enemyPaddleCenterX = mEnemyPaddleX + mEnemyPaddleWidth / 2f;
        float ballCenterX = mBallX;

        if (ballCenterX > enemyPaddleCenterX) {
            mEnemyPaddleX += 5;
        } else if (ballCenterX < enemyPaddleCenterX) {
            mEnemyPaddleX -= 5;
        }

        if (mEnemyPaddleX < 0) {
            mEnemyPaddleX = 0;
        } else if (mEnemyPaddleX + mEnemyPaddleWidth > mScreenWidth) {
            mEnemyPaddleX = mScreenWidth - mEnemyPaddleWidth;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mPlayerPaddleX = event.getX() - mPlayerPaddleWidth / 2f;
                break;
        }
        return true;

    }
}