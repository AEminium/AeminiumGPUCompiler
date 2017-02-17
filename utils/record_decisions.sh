for (( i = 0; i < 700; i++ )); do
	 DEBUG=1 DEBUG_ML=1 BENCH=1 timeout 300s java -cp lib/AeminiumGPU.jar:bin synthetic.Synthetic$i >> decision_300.log
done

