package hack.duke.compliments;

import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ValidateActivity extends Activity {

	Button send;
	String message;
	String number;
	String myString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validate);

		Intent i = getIntent();
		message = i.getStringExtra("compliment");
		number = i.getStringExtra("number");

		TextView message_view = (TextView) findViewById(R.id.message_view);
		TextView number_view = (TextView) findViewById(R.id.number_view);

		message_view.setText(message);
		number_view.setText(number);

		send = (Button) findViewById(R.id.button_send);
		send.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// JSONObject obj = new JSONObject();
				// obj.put("number", number);
				// obj.put("message", message);
				String json = "{\"number\":" + number+ ",\"message\":" + message+" - From an anonymous friend via the ComplimentsApp"+"}";
				try {

					// send as http get request
					URL url = new URL(
							"https://4f1d07e1.ngrok.com/Compliments/twilio.php?order="
									+ json);
					System.out.println(url.toString());
					URLConnection conn = url.openConnection();
					conn.connect();

					Intent nextScreen = new Intent(getApplicationContext(),
							SentActivity.class);

					nextScreen.putExtra("message", "Message sent!");

					// Log.e("n", inputName.getText()+"."+
					// inputEmail.getText());

					startActivity(nextScreen);
				} catch (Exception e) {

					Intent nextScreen = new Intent(getApplicationContext(),
							SentActivity.class);
					nextScreen.putExtra("message",
							"Ooops, error in sending your message :(");
					startActivity(nextScreen);
				}

				// }
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
