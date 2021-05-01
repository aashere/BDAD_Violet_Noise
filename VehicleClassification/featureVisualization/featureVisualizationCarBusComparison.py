import glob
import pandas as pd
import matplotlib.pyplot as plt

files = glob.glob("/Users/kristink/Documents/bigdata/featureVisualization/featureData/*.csv")
dfs = [pd.read_csv(f, header = 0, sep = ",") for f in files]
df = pd.concat(dfs, ignore_index=True)

#normalize data, the percentage of numbers of cars is around 97.87%
#use MaxSpeed feature to compare car(blue color) and bus(yellow color)

car_df = df.where(df['type'] == 'Car').sample(frac=0.0218)
bus_df = df.where(df['type'] == 'Bus')
car_max_speed = car_df['maxSpeed']
bus_max_speed = bus_df['maxSpeed']
plt.hist([car_max_speed, bus_max_speed])
plt.xlabel("MaxSpeed")
plt.ylabel("Frequency")
plt.title('MaxSpeed Comparison Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltComparison/max_speed_comparison_histogram.png")


#use AverageSpeed feature to compare car(blue color) and bus(yellow color)
car_average_speed = car_df['averageSpeed']
bus_average_speed = bus_df['averageSpeed']
plt.hist([car_average_speed, bus_average_speed])
plt.xlabel("AverageSpeed")
plt.ylabel("Frequency")
plt.title('AverageSpeed Comparison Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltComparison/average_speed_comparison_histogram.png")

#use TurnsCount feature to compare car(blue color) and bus(yellow color)
car_turns_count = car_df['turnsCount']
bus_turns_count = bus_df['turnsCount']
plt.hist([car_turns_count, bus_turns_count])
plt.xlabel("TurnsCount")
plt.ylabel("Frequency")
plt.title('TurnsCount Comparison Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltComparison/turns_count_comparison_histogram.png")

#use Hour feature to compare car(blue color) and bus(yellow color)
car_hour = car_df['hour']
bus_hour = bus_df['hour']
plt.hist([car_hour, bus_hour])
plt.xlabel("Hour")
plt.ylabel("Frequency")
plt.title('Hour Comparison Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltComparison/hour_comparison_histogram.png")

#use Minute feature to compare car(blue color) and bus(yellow color)
car_min = car_df['minute']
bus_min = bus_df['minute']
plt.hist([car_min, bus_min])
plt.xlabel("Minute")
plt.ylabel("Frequency")
plt.title('Minute Comparison Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltComparison/minute_comparison_histogram.png")

#use StartNode feature to compare car(blue color) and bus(yellow color)
car_start_vertex_id = car_df['start_vertex_id']
bus_start_vertex_id = bus_df['start_vertex_id']
plt.hist([car_start_vertex_id, bus_start_vertex_id])
plt.xlabel("StartNode")
plt.ylabel("Frequency")
plt.title('StartNode Comparison Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltComparison/start_node_comparison_histogram.png")

#use StopNode feature to compare car(blue color) and bus(yellow color)
car_stop_vertex_id = car_df['stop_vertex_id']
bus_stop_vertex_id = bus_df['stop_vertex_id']
plt.hist([car_start_vertex_id, bus_start_vertex_id])
plt.xlabel("StopNode")
plt.ylabel("Frequency")
plt.title('StopNode Comparison Histogram')
plt.savefig("/Users/kristink/Documents/bigdata/featureVisualization/pltComparison/stop_node_comparison_histogram.png")