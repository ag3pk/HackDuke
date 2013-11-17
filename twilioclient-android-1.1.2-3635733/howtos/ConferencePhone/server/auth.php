<?php 
require "Services/Twilio/Capability.php";

$accountSid = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; 
$authToken = "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY";

// The app outgoing connections will use: 
$appSid = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

$capability = new Services_Twilio_Capability($accountSid, $authToken);

// This would allow outgoing connections to $appSid: 
$capability->allowClientOutgoing($appSid);

// This would return a token to use with Twilio based on // the account and capabilities defined above 
$token = $capability->generateToken();

echo $token; 

?>