<?php
$servername = "SERVER_NAME";
$username = "USER_NAME";
$password = "PASSWORD";
$dbname = "DB_NAME";
// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
     	 die("Connection failed: " . $conn->connect_error);
}
$ver = $_GET['class'];
if(strpos($ver, ';') !== false) {
	die("Hacking is not allowed");
}
if($ver == "-") {
	$query = "SELECT * FROM `versions`";
	$result = $conn->query($query);
	if ($result->num_rows > 0) {
		while($row = $result->fetch_assoc()) {
	      		echo $row["ClassCode"]."\n".$row["Name"]."\n";
	        }
	} else {
	           echo "0 results";
        }
	$conn->close();
} else if($ver!="") {
	$query = "SELECT * FROM `versions` WHERE `ClassCode`='".$ver."'";
	$result = $conn->query($query);
	if($result->num_rows > 0 ) {
		$row = $result->fetch_assoc();
		echo $row["Version"];
	} else {
		echo "-1";
	}
}

