#!/bin/bash

rm -rf bin/*
rm -rf spooned/*

ant fetchruntime
ant compile-compiler
./utils/aegpuc src

export DEBUG=1
export BENCH=1
mkdir -p logs
./utils/aegpu benchmark.BenchmarkDecisions > logs/benchmark.log
./utils/aegpu benchmark.RandomBenchmarkDecisions > logs/random_benchmark.log
