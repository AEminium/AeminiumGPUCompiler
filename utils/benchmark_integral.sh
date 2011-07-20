./utils/aegpuc src 

for i in `seq 1 30`;
do
    ./utils/aegpu benchmark.Integral >> utils/benchmark_integral.log
done



