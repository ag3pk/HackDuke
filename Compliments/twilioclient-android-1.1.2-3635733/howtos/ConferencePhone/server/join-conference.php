<?php
	$conferenceName = $_REQUEST["ConferenceName"];
	header('Content-type: text/xml');
?>

<Response>
	<Say>Joining Conference <?php echo $conferenceName;?></Say>
	<Dial>
		<Conference><?php echo $conferenceName;?></Conference>
	</Dial>
</Response>