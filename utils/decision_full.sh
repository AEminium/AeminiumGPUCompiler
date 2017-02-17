#!/bin/bash

rm -rf bin/*
rm -rf spooned/*

ant fetchruntime
ant compile-compiler
ant compile

export DEBUG_ML=1
export DEBUG=1
export BENCH=1
mkdir -p logs
./utils/aegpu benchmark.BenchmarkDecisions > logs/benchmark_full_exponential.log
./utils/aegpu benchmark.RandomBenchmarkDecisions > logs/benchmark_full_random.log
for (( i = 0; i < 700; i++ )); do
    timeout 300s ./utils/aegpu synthetic.Synthetic$i >> logs/benchmark_full_synthetic.log
done