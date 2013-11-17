package hack.duke.compliments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TextView compliment1 = (TextView) findViewById(R.id.compliment1);
		TextView compliment2 = (TextView) findViewById(R.id.compliment2);
		TextView compliment3 = (TextView) findViewById(R.id.compliment3);
		compliment1.setText("Compliment One.", TextView.BufferType.NORMAL);
		compliment2.setText("Compliment Two.", TextView.BufferType.NORMAL);
		compliment3.setText("Compliment Three.", TextView.BufferType.NORMAL);
		compliment1.setOnClickListener(this);
		compliment2.setOnClickListener(this);
		compliment3.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View v) {
		Intent nextScreen = new Intent(getApplicationContext(),
				PhoneActivity.class);

		// Sending data to another Activity
		// nextScreen.putExtra("name", inputName.getText().toString());
		//nextScreen.putExtra("compliment", inputEmail.getText().toString());

		// Log.e("n", inputName.getText()+"."+ inputEmail.getText());

		startActivity(nextScreen);
	}
}
