curr_time() {
	echo $(date +%s)
}

while read instance; do
  BEGIN=$(expr $(curr_time))
  java -jar ttplab-1.1.jar $instance ma2b 600 1
  echo $(expr $(expr $(curr_time)) - $BEGIN)
done <ttpInstances.txt
