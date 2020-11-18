<?php
$test = ".csv";
$filename = $_GET['class'];
$strlen = strlen($filename);
$testlen = strlen($test);
if ($testlen >=  $strlen) die("Hacking is not allowed");
$ends = true;
for($i=1;$i <= 4; $i++) {
	if($filename[$strlen-$i] != $test[$testlen-$i]) {
		$ends = false;
		break;
	}
}
if($ends) {
	$myfile = fopen($filename, "r") or die("Unable to open file!");
	echo nl2br(fread($myfile,filesize($filename)));
	fclose($myfile);
} else {
	die("Hacking is not allowed");
}
