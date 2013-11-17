package hack.duke.compliments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SentActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sent);

		Intent i = getIntent();
		String message = i.getStringExtra("message");
		TextView output = (TextView) findViewById(R.id.sent_message);
		output.setText(message);
		Button button_close = (Button) findViewById(R.id.button_close);
		button_close.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				android.os.Process.killProcess(android.os.Process.myPid());
				finish();
                System.exit(0);

				
			}
			
		});

	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}
