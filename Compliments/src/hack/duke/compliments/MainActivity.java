package hack.duke.compliments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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
		try {
			String[] complimentChoices = new String[3];
			String json = "";

			URL url = new URL("http://140db729.ngrok.com/Compliments/sqlPhp.php");
			URLConnection conn = url.openConnection();

			BufferedReader response = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			ArrayList<String> compliments = new ArrayList<String>();
			while ((line = response.readLine()) != null) {
				compliments.add(line);
			}
			ArrayList<Integer> used = new ArrayList<Integer>();
			ArrayList<String> possibleCompliments = new ArrayList<String>();
			for (int i =0; i < 3; i++) {
				int rand = (int)(Math.random()*compliments.size());
				if (!used.contains(rand)) {
					complimentChoices[i] = compliments.get(rand);
					used.add(rand);
				}
				else {
					i--;
				}
			}

			compliment1.setText(complimentChoices[0], TextView.BufferType.NORMAL);
			compliment2.setText(complimentChoices[1], TextView.BufferType.NORMAL);
			compliment3.setText(complimentChoices[2], TextView.BufferType.NORMAL);
			compliment1.setOnClickListener(this);
			compliment2.setOnClickListener(this);
			compliment3.setOnClickListener(this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

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

		TextView compliment = (TextView) v;
		// Sending data to another Activity
		// nextScreen.putExtra("name", inputName.getText().toString());
		nextScreen.putExtra("compliment", compliment.getText().toString());

		// Log.e("n", inputName.getText()+"."+ inputEmail.getText());

		startActivity(nextScreen);
	}
}
