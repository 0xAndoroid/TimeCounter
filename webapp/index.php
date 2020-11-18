<?php
$oldVersion = $_GET['ver'];
$newVersion = "1.0";
if($oldVersion != $newVersion) {
    echo "NOT_OK<br>";
    $myfile = fopen("a11_2020.csv", 'r');
    $file = fread($myfile,filesize("a11_2020.csv"));
    echo nl2br(($file));
    fclose($myfile);
} else {
    echo "OK";
}