#!/bin/bash
php ../Dramagraph/Base.php moliere.sqlite urls_moliere.tsv
php ../Dramagraph/Base.php contexte.sqlite urls_contexte.tsv
php ../Teinte/Build.php
