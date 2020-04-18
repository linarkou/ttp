library(purrr)

readData <- function(filename) {
  dataframe <- read.csv(filename, header = FALSE, sep = ";")
  colnames(dataframe) <- c("Instance", "Result")
  return(dataframe)
}

meanAndMedian <- function(data, suffixName) {
  mean <- aggregate(data$Result, by=list(Instance = data$Instance), FUN=mean)
  median <- aggregate(data$Result, by=list(Instance = data$Instance), FUN=median)
  stats <- merge(mean, median, "Instance")
  colnames(stats)[2:3] <-c(paste("mean", suffixName, sep=" "),
                           paste("median", suffixName, sep=" "))
  return(stats)
}

wilcox <- function(data1, data2, alt) {
  grouppedResults1 <- split(data1$Result, data1$Instance)
  grouppedResults2 <- split(data2$Result, data2$Instance)
  wilcox <- map2(grouppedResults1, grouppedResults2, ~ wilcox.test(.x, .y, alternative = alt))
  print(wilcox)
  wilcoxPvalues <- map(wilcox, ~ .x[["p.value"]])
  res <- do.call(rbind, lapply(wilcoxPvalues, data.frame))
  colnames(res)[1] <- paste("p-value", alt, sep = " ")
  res$Instance <- rownames(res)
  return(res)
}

myHeuristic <- readData("my-heuristic.csv")
cs2sar <- readData("cs2sar.csv")

myHeuristicStats = meanAndMedian(myHeuristic, "my-heuristic")
cs2sarStats = meanAndMedian(cs2sar, "cs2sar")

# wilcoxon
wilcoxLess <- wilcox(myHeuristic, cs2sar, "less")
wilcoxGreater <- wilcox(myHeuristic, cs2sar, "greater")
wilcoxRes <- merge(wilcoxLess, wilcoxGreater, "Instance")

# holm-bonferroni method for hypothesis testing over all instances (p-values)
holmLess <- p.adjust(wilcoxLess$`p-value less`, method = "holm")
holmGreater <- p.adjust(wilcoxGreater$`p-value greater`, method = "holm")

result <- merge(merge(myHeuristicStats, cs2sarStats, "Instance"),
                wilcoxRes, "Instance")
result$`holm less` <- holmLess
result$`holm greater` <- holmGreater

 write.table(result, "results-statistics.csv", sep=";", quote = FALSE, row.names = FALSE)

# write.csv(cs2sarStats, "cs2sar-stats.csv")
# write.csv(myHeuristicStats, "my-heuristic-stats.csv")
