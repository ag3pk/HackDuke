package hack.duke.compliments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ValidateActivity extends Activity{
	
	Button send;
	String message;
	String number;
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
        send.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v){

//                String number = parseNumber(phone_field.getText().toString());
//                if(number.equals("-1")){
//                	//TODO: display error message
//                }
//                else{
//                	Intent nextScreen = new Intent(getApplicationContext(),
//            				ValidateActivity.class);
//            		
//            		nextScreen.putExtra("compliment", message);
//            		nextScreen.putExtra("number", number);
//
//            		// Log.e("n", inputName.getText()+"."+ inputEmail.getText());
//
//            		startActivity(nextScreen);
//                }
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
