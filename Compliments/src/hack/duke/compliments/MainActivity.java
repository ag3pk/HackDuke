package hack.duke.compliments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

		(new LoadComplimentsTask()).execute("","",null);
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

		TextView compliment = (TextView) v;
		// Sending data to another Activity
		// nextScreen.putExtra("name", inputName.getText().toString());
		nextScreen.putExtra("compliment", compliment.getText().toString());

		// Log.e("n", inputName.getText()+"."+ inputEmail.getText());

		startActivity(nextScreen);
	}

	public class LoadComplimentsTask extends AsyncTask<String, String, String[]> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String[] doInBackground(String... params) {

			String[] complimentChoices = new String[3];

			URL url = null;
			URLConnection conn = null;
			try {
				url = new URL("http://4f1d07e1.ngrok.com/Compliments/sqlPhp.php");
				conn = url.openConnection();
			} catch (Exception e1) {
				complimentChoices[0] = "ErrorURL";
				return complimentChoices;
			}


			String line;
			ArrayList<String> compliments = new ArrayList<String>();
			BufferedReader response = null;
			InputStreamReader reader = null;
  

			try {
				InputStream i = conn.getInputStream();
				reader = new InputStreamReader(i);
			} catch (IOException e) {
				complimentChoices[0] = "ErrorIn";
				return complimentChoices;
			} 



			response = new BufferedReader(reader);

			try {

				String l = response.readLine().replace('[', ' ');
				l = l.replace(']', ' ');
				String[] linesSeparated = l.split(",");
				ArrayList<Integer> used = new ArrayList<Integer>();
				for (int i = 0; i < 3; i++) {
					int rand = (int)(Math.random()*linesSeparated.length);
					if (!used.contains(rand)) {
						complimentChoices[i] = linesSeparated[rand];
						used.add(rand);
					}
					else 
						i--;
				}
			}
			catch (Exception e1) {
				complimentChoices[0] = "Error";
				return complimentChoices;
			}
			return complimentChoices;

		}


		public void onPostExecute(String[] complimentChoices) {
			TextView compliment1 = (TextView) findViewById(R.id.compliment1);
			TextView compliment2 = (TextView) findViewById(R.id.compliment2);
			TextView compliment3 = (TextView) findViewById(R.id.compliment3);

			compliment1.setText(complimentChoices[0], TextView.BufferType.NORMAL);
			compliment2.setText(complimentChoices[1], TextView.BufferType.NORMAL);
			compliment3.setText(complimentChoices[2], TextView.BufferType.NORMAL);

		}
	}
}
