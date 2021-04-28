import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('/Users/kristink/Documents/bigdata/gps/part-00000-030816d8-9ea7-45ea-b877-6ddf7d9113cc-c000.csv', sep=',')

# feature: MaxSpeed
df_max_speed = df.select("id", "maxSpeed").distinct
df_max_speed['maxSpeed'].plot(kind='hist')
plt.xlabel("MaxSpeed")
plt.ylabel("Frequency")
plt.title('MaxSpeed Histogram')
plt.savefig("max_speed_histogram.png")

# feature: AverageSpeed
df['averageSpeed'].plot(kind='hist')
plt.xlabel("averageSpeed")
plt.ylabel("Frequency")
plt.title('AverageSpeed Histogram')
plt.savefig("average_speed_count_histogram.png")

# feature: TurnsCount
df_turns_count = df.select("id", "turnsCount").distinct
df_turns_count['turnsCount'].plot(kind='hist')
plt.xlabel("TurnsCount")
plt.ylabel("Frequency")
plt.title('TurnsCount Histogram')
plt.savefig("turns_count_histogram.png")

# feature: Hour
df['hour'].plot(kind='hist')
plt.xlabel("Hour")
plt.ylabel("Frequency")
plt.title('Hour Histogram')
plt.savefig("hour_histogram.png")

# feature: Minute
df['minute'].plot(kind='hist')
plt.xlabel("Minute")
plt.ylabel("Frequency")
plt.title('Minute Histogram')
plt.savefig("minute_histogram.png")

# feature: StartNodeId
df['start_vertex_id'].plot(kind='hist')
plt.xlabel("StartNodeId")
plt.ylabel("Frequency")
plt.title('StartNodeId Histogram')
plt.savefig("start_node_id_histogram.png")

# feature: StopNodeId
df['stop_vertex_id'].plot(kind='hist')
plt.xlabel("StopNodeId")
plt.ylabel("Frequency")
plt.title('StopNodeId Histogram')
plt.savefig("stop_node_id_histogram.png")