package my.example.connecticons;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity {

	GameView mGameView;
	View mControls;
	
	Game mGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGameView = (GameView)findViewById(R.id.game_view);
		mControls = findViewById(R.id.game_controls);
		mControls.setVisibility(View.GONE);
		mGame = new Game();
		mGameView.init(mGame);
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	public void onBackPressed() {
		mControls.setVisibility(View.VISIBLE);
		//super.onBackPressed();
	}

	public void showHint(View view) {
		mGame.bShowHint = true;
		mControls.setVisibility(View.GONE);
	}
	
	public void doExit(View view) {
		finish();
	}
	
}
