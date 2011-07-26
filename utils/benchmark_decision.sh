#!/bin/bash

rm -rf bin/*
rm -rf spooned/*

ant fetchruntime
ant compile-compiler
./utils/aegpuc src

export FORCE_GPU=1
echo "GPU NOW"
./utils/aegpu benchmark.BenchmarkDecisions

unset FORCE_GPU
export FORCE_CPU=1
echo "CPU NOW"
./utils/aegpu benchmark.BenchmarkDecisions

