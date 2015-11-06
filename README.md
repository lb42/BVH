# BVH
Depo des fichiers TEI-XML de la Bibliotheque Virtuelle des Humanistes

Le Makefile est essentiel. Pour rappel:

* creer d'abord dossiers *Corpus* et *finalCorpus*
* modifier le Makefile si besoin est pour avoir les chemins appropries
* modifier le fichier *theFiles* pour specifier les fichiers a traiter
* faire **make copy** pour transferer tous les textes du dossier source au celui de travail
* faire **make cleanup** pour appliquer la fds *cleanup.xsl*; les sorties sont dans *finalCorpus*
* faire **make checkClean** pour valider chacun des fichiers independemment contre le schema _bvh.rng_
* faire **make drive** pour creer un driver file permettant la validation du corpus integralement
* faire **make checkLinks** pour tester la validite des pointeurs dans tout le corpus
