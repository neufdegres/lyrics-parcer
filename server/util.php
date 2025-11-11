<?php

function read_in_file($nomFichier) {
    if (!file_exists($nomFichier) || !is_readable($nomFichier)) {
        return false;
    }

    $fichier = fopen($nomFichier, 'r');
    if (!$fichier) {
        return false;
    }

    $lignes = [];
    while (($ligne = fgets($fichier)) !== false) {
        $ligne = trim($ligne);
        $lignes[] = $ligne;
    }

    fclose($fichier);

    return $lignes;
}

function writte_in_file($texte, $nomFichier) {
    $fichier = fopen($nomFichier, 'w');
    if (!$fichier) {
        return false;
    }

    foreach ($texte as $ligne) {
        fwrite($fichier, $ligne . PHP_EOL);
    }

    fclose($fichier);
    
    return true;
} 

function remove_esc($line) {
    return str_replace("\\", "", $line);
}

function get_code() {
    $letters = 'abcdefghijklmnopqrstuvwxyz';
    $randomString = '';

    for ($i = 0; $i < 3; $i++) {
        $index = rand(0, strlen($letters) - 1);
        $randomString .= $letters[$index];
    }

    return $randomString;
} 

?>