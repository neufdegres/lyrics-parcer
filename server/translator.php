<?php

include_once 'util.php';

$json = file_get_contents('php://input');
$request = json_decode($json, true);  

if (!isset($request['lines']) || $request['lines'] == NULL) { echo '{"code":"1", "lines":[]}'; exit;}

$lines = $request['lines'];

if (!is_array($lines) || count($lines) == 0) { echo '{"code":"2", "lines":[]}'; exit;}

$res = [];
$raw = '/usr/bin/python3 translator.py "';

$code = get_code();
$filename_in = sprintf("tmp/trans_in_0%s.txt", $code);
$filename_out = sprintf("tmp/trans_out_0%s.txt", $code);

if (!writte_in_file($lines, $filename_in)) { echo '{"code":"3", "lines":[]}'; exit;}

$command = escapeshellcmd($raw.$filename_in."\" \"".$filename_out."\"");
$output = shell_exec($command);

// if ($output != "0") {echo '{"code":"'.$output.'", "lines":[]}'; exit;}
if ($output != "0") { echo '{"code":"4", "lines":[]}'; exit;}

$res = read_in_file($filename_out);

print('{"code":"0", "lines":'.json_encode($res).'}');

?>