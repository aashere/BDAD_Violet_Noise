import glob
import pandas as pd
import matplotlib.pyplot as plt

files = glob.glob("/Users/kristink/Documents/bigdata/featureVisualization/featureData/*.csv")
dfs = [pd.read_csv(f, header = 0, sep = ",") for f in files]
df = pd.concat(dfs, ignore_index=True)

# feature: MaxSpeed
df['maxSpeed'].plot(kind='hist')
plt.xlabel("MaxSpeed")
plt.ylabel("Frequency")
plt.title('MaxSpeed Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltWhole/max_speed_histogram.png")

# feature: AverageSpeed
df['averageSpeed'].plot(kind='hist')
plt.xlabel("averageSpeed")
plt.ylabel("Frequency")
plt.title('AverageSpeed Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltWhole/average_speed_count_histogram.png")

# feature: TurnsCount
df['turnsCount'].plot(kind='hist')
plt.xlabel("TurnsCount")
plt.ylabel("Frequency")
plt.title('TurnsCount Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltWhole/turns_count_histogram.png")

# feature: Hour
df['hour'].plot(kind='hist')
plt.xlabel("Hour")
plt.ylabel("Frequency")
plt.title('Hour Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltWhole/hour_histogram.png")

# feature: Minute
df['minute'].plot(kind='hist')
plt.xlabel("Minute")
plt.ylabel("Frequency")
plt.title('Minute Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltWhole/minute_histogram.png")

# feature: StartNode
df['start_vertex_id'].plot(kind='hist')
plt.xlabel("StartNodeId")
plt.ylabel("Frequency")
plt.title('StartNode Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltWhole/start_node_histogram.png")

# feature: StopNode
df['stop_vertex_id'].plot(kind='hist')
plt.xlabel("StopNodeId")
plt.ylabel("Frequency")
plt.title('StopNode Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltWhole/stop_node_histogram.png")