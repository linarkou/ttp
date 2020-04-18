curr_time() {
	echo $(date +%s)
}

while read instance; do
  BEGIN=$(expr $(curr_time))
  java -jar TTP-forDistribution-1.0.jar $instance 1 10000 600 1
  echo $(expr $(expr $(curr_time)) - $BEGIN)
done <ttpInstances.txt
