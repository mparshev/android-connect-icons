package my.example.connecticons;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private final static int TILE_SIZE = 66;
	private final static int FIRST_ICON = R.drawable.alien;
	private final static int LAST_ICON = R.drawable.water_drop_1;
	
	class GameThread extends Thread {

		Paint mBack;
		Paint mPaint;
		Paint mBlink;

		Bitmap[] mBitmaps;

		int mOffX, mOffY;
		
		SurfaceHolder mSurfaceHolder;
		Context mContext;
		
		boolean mRun = false;
		final Object mRunLock = new Object();

		
		public GameThread(SurfaceHolder surfaceHolder, Context context) {
			
			mSurfaceHolder = surfaceHolder;
			mContext = context;

			Resources res = context.getResources();

			mPaint = new Paint();
			mPaint.setColor(res.getColor(R.color.background));
			mBlink = new Paint();
			mBlink.setColor(res.getColor(R.color.background));
			mBack = new Paint();
			mBack.setColor(res.getColor(R.color.background));
			
			int numIcons = LAST_ICON - FIRST_ICON + 1;
			
			mBitmaps = new Bitmap[numIcons];
			for (int i = 0; i < numIcons; i++) {
				mBitmaps[i] = BitmapFactory.decodeResource(res, FIRST_ICON + i);
			}
			
		}
		
		public void init(int w, int h) {
			int cols = w / TILE_SIZE / 2 * 2;
			int rows = h / TILE_SIZE / 2 * 2;
			mOffX = 1 + (w - cols * TILE_SIZE) / 2;
			mOffY = 1 + (h - rows * TILE_SIZE) / 2;
			if(mGame.mRows != rows || mGame.mCols != cols) mGame.init(rows, cols);
		}
		
		@Override
		public void run() {
			while(mRun) {
				Canvas canvas = null;
				try {
					canvas = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						synchronized (mRunLock) {
							if(mRun) doDraw(canvas);
						}
					}
				} finally {
					if( canvas != null)
						mSurfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
		
		public void setRunning(boolean b) {
			synchronized (mRunLock) {
				mRun = b;
			}
		}

		public void setSurfaceSize(int w, int h) {
			synchronized (mSurfaceHolder) {
				init(w, h);
			}
		}
		
		public boolean doTouchEvent(MotionEvent event) {
			synchronized (mSurfaceHolder) {
				int col = ((int) event.getX() - mOffX) / TILE_SIZE;
				int row = ((int) event.getY() - mOffY) / TILE_SIZE;
				return mGame.touch(row, col);
			}
		}
		
		private static final int BLINK_RATE = 1000;
		
		public void doDraw(Canvas canvas) {
			long time = System.currentTimeMillis();
			mBlink.setAlpha(( time % BLINK_RATE < BLINK_RATE / 2) ? 64 : 255);
			
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBack);
			
			for (int row = 0; row < mGame.mRows; row++) {
				for (int col = 0; col < mGame.mCols; col++) {
					int img = mGame.mTiles[row][col];
					if (img < 0) continue;
					Paint paint = mPaint;
					if(row == mGame.selRow && col == mGame.selCol) paint = mBlink;
					if(mGame.bShowHint && row == mGame.hintRow0 && col == mGame.hintCol0) paint = mBlink;
					if(mGame.bShowHint && row == mGame.hintRow1 && col == mGame.hintCol1) paint = mBlink;
					canvas.drawBitmap(mBitmaps[img], 
							mOffX + TILE_SIZE * col, 
							mOffY + TILE_SIZE * row,
							paint);
				}
			}
			
		}
		
	}

	
	GameThread thread;

	Game mGame;
	
	public GameView(Context context) {
		super(context);
	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(Game game) {

		mGame = game;
		
		getHolder().addCallback(this);

		setFocusable(true);
		setFocusableInTouchMode(true);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return thread.doTouchEvent(event);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		thread = new GameThread(getHolder(), getContext());
		
		thread.setRunning(true);
		thread.start();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while(retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// Do nothing
			}
		}
		thread = null;
	}
	
}
