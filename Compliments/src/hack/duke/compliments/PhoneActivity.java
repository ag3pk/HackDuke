package hack.duke.compliments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneActivity extends Activity {
	
	EditText phone_field;
	Button validate;
	String message;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone);
		
        Intent i = getIntent();
        message = i.getStringExtra("compliment");
        phone_field = (EditText) findViewById(R.id.phone_number);
        validate = (Button) findViewById(R.id.button_validate);
        validate.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v){

                String number = parseNumber(phone_field.getText().toString());
                if(number.equals("-1")){
                	//TODO: display error message
                }
                else{
                	Intent nextScreen = new Intent(getApplicationContext(),
            				ValidateActivity.class);
            		
            		nextScreen.putExtra("compliment", message);
            		nextScreen.putExtra("number", number);

            		// Log.e("n", inputName.getText()+"."+ inputEmail.getText());

            		startActivity(nextScreen);
                }
        	}
        });

	}
	
	private String parseNumber(String s){
		s.replace("[^1-9]+","");
		if(s.length() == 10 || (s.length() == 11 && s.charAt(0) == '1')){
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
