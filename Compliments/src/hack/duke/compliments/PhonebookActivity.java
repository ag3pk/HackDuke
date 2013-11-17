package hack.duke.compliments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PhonebookActivity extends Activity implements TextWatcher {

	EditText contact;
	ListView phonebook;
	String message;
	ArrayAdapter<String> adapter;
	private List<String> namenumber = new ArrayList<String>();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phonebook);

		Intent i = getIntent();
		message = i.getStringExtra("compliment");
		contact = (EditText) findViewById(R.id.text_name);
		contact.addTextChangedListener(this);

		phonebook = (ListView) findViewById(R.id.list_phonebook);
		phonebook.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			    TextView txt = (TextView) arg1;
			    String[] content = txt.getText().toString().split(":");
				String number = PhoneActivity.parseNumber(content[1]);
				if (number.equals("-1")) {
					//valid_number.setText("Phone number format not valid");
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

	}

	public void afterTextChanged(Editable s) {
		String name = s.toString();
		namenumber = new ArrayList<String>();
		Log.e("n", name);
		if (name.length() > 0) {
			Cursor c = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);
			String contactName, contactTelNumber = "";
			String contactID;

			// You only need to find these indices once
			int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
			int hasNumberIndex = c
					.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
			int nameIndex = c
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

			// This is simpler than calling getCount() every iteration
			while (c.moveToNext()) {
				contactName = c.getString(nameIndex);
				if (contactName.toLowerCase().contains(name.toLowerCase())) {
					contactID = c.getString(idIndex);

					// If this is an integer ask for an integer
					if (c.getInt(hasNumberIndex) > 0) {
						Cursor pCur = getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = ?",
										new String[] { contactID }, null);
						while (pCur.moveToNext()) {
							contactTelNumber = pCur
									.getString(pCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

							// Store the "name: number" string in our list
							namenumber.add(contactName + ": "
									+ contactTelNumber);
						}
					}
				}
			}

			// Find the ListView, create the adapter, and bind them
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, namenumber);
			phonebook.setAdapter(adapter);
			c.close();
		}

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
