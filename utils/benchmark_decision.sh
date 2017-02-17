#!/bin/bash

rm -rf bin/*
rm -rf spooned/*

ant fetchruntime
ant compile-compiler
ant compile
#./utils/aegpuc src

export DEBUG_ML=1
export DEBUG=1
export BENCH=1
mkdir -p logs
#./utils/aegpu benchmark.BenchmarkDecisions > logs/benchmark.log
./utils/aegpu benchmark.RandomBenchmarkDecisions > logs/random_benchmark.log
