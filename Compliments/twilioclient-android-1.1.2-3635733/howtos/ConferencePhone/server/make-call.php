<?php
	// A phone number you have previously validated with Twilio
    $twilioPhoneNumber = 'XXXXXXXXXX';
	
	// If the number above isn't changed, send an error immediately.
	if ( $twilioPhoneNumber == 'XXXXXXXXXX' )
	{
		header("Must replace caller ID phone number with an authenticated Twilio number", TRUE, -500);
		return;
	}
    
	// Include the Twilio PHP library
    require 'Services/Twilio.php';
 
    // Twilio REST API version
    $version = "2010-04-01";
 
    // Set our Account SID and AuthToken
    $acctSid = 'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX';
    $authToken = 'YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY';
     
    //HTTP Request for the conference name the user will be connected to
    $conferenceName = $_REQUEST["conferenceName"];
    
    //Initalize the response URL
    $url = "http://companyfoo.com/join-conference.php?ConferenceName=$conferenceName";
     
    //HTTP Request the participants array passed in the URL
 	$participants = $_REQUEST['participants']; 
     
    // Instantiate a new Twilio Rest Client
    $client = new Services_Twilio($acctSid, $authToken, $version);
 	
	foreach ($participants as $participant)
	{
		//Make REST call for each participant
		try 
		{	
			// Initiate a new outbound call to either a Phone Number or 
			// Client.  It's expected that a Client call will have "client:"
			// prepended to the $participant string by the invoker of this 
			// make-call.php URL.
			$call = $client->account->calls->create(
						$twilioPhoneNumber, // The number of the phone initiating the call
						$participant, // The number or client name of the phone or device receiving call
						$url //The URL with the TwimL that will be executed
					);
		  echo 'Started call: ' . $call->sid;
		}
		catch (Exception $e) 
		{
			echo 'Error: ' . $e->getMessage();
		}
	}
?>