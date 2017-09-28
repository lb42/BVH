<?php
return array(
  "srcdir" => dirname( __FILE__ ),
  "destdir" => ".",
  "cmdup" => "git pull &2>1", // commande pour faire tomber le site et ses modules de de sources XML
  "pass" => "", // Mot de passe obligatoire
  "srcglob" => array( "critique/*.xml" ), // pour mise à jour de la polémique
  "sqlite" => "moliere-critique.sqlite", // pour les métadonnées de la polémique
  "formats" => "article, toc, epub, kindle",
  "title" => 'Molière',
);
?>
