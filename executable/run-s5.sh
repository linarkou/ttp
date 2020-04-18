curr_time() {
	echo $(date +%s)
}

while read instance; do
  BEGIN=$(expr $(curr_time))
  java -jar thief-1.0.jar $instance s5 600 1
  echo $(expr $(expr $(curr_time)) - $BEGIN)
done <ttpInstances.txt
