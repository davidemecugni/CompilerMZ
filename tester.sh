#!/bin/bash

if test $# -gt 2
then
  echo "Errore: numero di argomenti non valido"
  exit 1
fi
if test "$1" = '-h' -o "$1" = '--help'
then
  echo "Uso: tester.sh [file_input file_output]"
  echo "Genera il file output.asm a partire dal file input.mz"
  echo "Genera il file oggetto output.o e il file eseguibile output"
  echo "Se non specificati"
  echo "fausto.md -> output.asm nella cartella Risorse"
  echo "Attenzione funziona solo su Linux e se il progetto è importato in IdeaProjects!"
  exit 0
fi

if test $# -eq 0
then
  echo "Sto lavorando su fausto.md -> output.asm"
  in="$HOME/IdeaProjects/CompilerMZ/Risorse/fausto.mz"
  out="$HOME/IdeaProjects/CompilerMZ/Risorse/output.asm"
else
  in="$1"
  out="$2"
fi
cd ./target/classes || exit 2
java org.compiler.CompilerMZ $in $out
if test $? -ne 0
then
	echo "Errore: mi fermo"
	exit 3
fi
cd ~/IdeaProjects/CompilerMZ/Risorse/ || exit 4
echo "----   Fine Java   ----"
nasm -felf64 output.asm -o output.o
echo "Codice assemblato!"
ld output.o -o output
echo "Codice linkato!"
./output
echo "File eseguito! Codice di ritorno $?"

