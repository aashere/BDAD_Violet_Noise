import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('/Users/kristink/Documents/bigdata/gps/gps_convert_car_type/part-00000-f4fccad9-5e36-4ce0-b968-0c833ce035f8-c000.csv', sep=',')

#normalize data, the percentage of numbers of cars is around 98%
#use MaxSpeed feature to compare car and bus
df_max_speed = df.select("id", "type", "maxSpeed").distinct
car_df_max_speed = df_max_speed.where(df['type'] == 'Car').sample(frac=0.25)
bus_df_max_speed = df_max_speed.where(df['type'] == 'Bus')
car_max_speed = car_df['maxSpeed']
bus_max_speed = bus_df['maxSpeed']
plt.hist([car_max_speed, bus_max_speed])
plt.xlabel("MaxSpeed")
plt.ylabel("Frequency")
plt.title('MaxSpeed Comparison Histogram')
plt.savefig("max_speed_comparison_histogram.png")

#use AverageSpeed feature to compare car and bus
car_df = df.where(df['type'] == 'Car').sample(frac=0.0204)
bus_df = df.where(df['type'] == 'Bus')
car_average_speed = car_df['averageSpeed']
bus_average_speed = bus_df['averageSpeed']
plt.hist([car_average_speed, bus_average_speed])
plt.xlabel("AverageSpeed")
plt.ylabel("Frequency")
plt.title('AverageSpeed Comparison Histogram')
plt.savefig("average_speed_comparison_histogram.png")

#use TurnsCount feature to compare car and bus
df_turns_count = df.select("id", "type", "turnsCount").distinct
car_df_turns_count = df_turns_count.where(df['type'] == 'Car').sample(frac=0.0204)
bus_df_turns_count = df_turns_count.where(df['type'] == 'Bus')
car_turns_count = car_df_turns_count['turnsCount']
bus_turns_count = bus_df_turns_count['turnsCount']
plt.hist([car_turns_count, bus_turns_count])
plt.xlabel("TurnsCount")
plt.ylabel("Frequency")
plt.title('TurnsCount Comparison Histogram')
plt.savefig("turns_count_comparison_histogram.png")

#use Hour feature to compare car and bus
bus_df = df.where(df['type'] == 'Bus')
car_hour = car_df['hour']
bus_hour = bus_df['hour']
plt.hist([car_hour, bus_hour])
plt.xlabel("Hour")
plt.ylabel("Frequency")
plt.title('Hour Comparison Histogram')
plt.savefig("hour_comparison_histogram.png")

#use Minute feature to compare car and bus
car_df = df.where(df['type'] == 'Car').sample(frac=0.0204)
bus_df = df.where(df['type'] == 'Bus')
car_min = car_df['minute']
bus_min = bus_df['minute']
plt.hist([car_min, bus_min])
plt.xlabel("Minute")
plt.ylabel("Frequency")
plt.title('Minute Comparison Histogram')
plt.savefig("minute_comparison_histogram.png")

#use StartNodeId feature to compare car and bus
car_df = df.where(df['type'] == 'Car').sample(frac=0.0204)
bus_df = df.where(df['type'] == 'Bus')
car_start_vertex_id = car_df['start_vertex_id']
bus_start_vertex_id = bus_df['start_vertex_id']
plt.hist([car_start_vertex_id, bus_start_vertex_id])
plt.xlabel("StartNodeId")
plt.ylabel("Frequency")
plt.title('StartNodeId Comparison Histogram')
plt.savefig("start_node_id_comparison_histogram.png")

#use StopNodeId feature to compare car and bus
car_df = df.where(df['type'] == 'Car').sample(frac=0.0204)
bus_df = df.where(df['type'] == 'Bus')
car_stop_vertex_id = car_df['stop_vertex_id']
bus_stop_vertex_id = bus_df['stop_vertex_id']
plt.hist([car_start_vertex_id, bus_start_vertex_id])
plt.xlabel("StopNodeId")
plt.ylabel("Frequency")
plt.title('StopNodeId Comparison Histogram')
plt.savefig("stop_node_id_comparison_histogram.png")