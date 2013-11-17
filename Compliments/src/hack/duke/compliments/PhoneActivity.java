package hack.duke.compliments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneActivity extends Activity {

	EditText phone_field;
	TextView valid_number;
	Button validate;
	String message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone);

		Intent i = getIntent();
		message = i.getStringExtra("compliment");
		phone_field = (EditText) findViewById(R.id.phone_number);
		valid_number = (TextView) findViewById(R.id.text_legit_number);
		validate = (Button) findViewById(R.id.button_validate);
		Button phonebook = (Button) findViewById(R.id.button_contacts);
		validate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String number = parseNumber(phone_field.getText().toString());
				if (number.equals("-1")) {
					valid_number.setText("Phone number format not valid");
				} else {
					Intent nextScreen = new Intent(getApplicationContext(),
							ValidateActivity.class);

					nextScreen.putExtra("compliment", message);
					nextScreen.putExtra("number", number);

					// Log.e("n", inputName.getText()+"."+
					// inputEmail.getText());

					startActivity(nextScreen);
				}
			}
		});

		phonebook.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent nextScreen = new Intent(getApplicationContext(),
						PhonebookActivity.class);

				nextScreen.putExtra("compliment", message);

				startActivity(nextScreen);
			}

		});

	}

	public static String parseNumber(String s) {
		s = s.replaceAll("[^0-9]", "");

		if (s.length() == 10 || (s.length() == 11 && s.charAt(0) == '1')) {
			return s;
		}
		return "-1";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
