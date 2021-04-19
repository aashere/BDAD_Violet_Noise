import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import cm

node_df = pd.read_csv('../sampleSchema/node_table.csv')
edge_df = pd.read_csv('../sampleSchema/edge_table.csv')
edge_df.drop(columns=['type'],inplace=True)
edge_df.rename(columns={'id':'edge_id'}, inplace=True)

# Join from and to coordinates onto edge_df
edge_df = pd.merge(edge_df, node_df, left_on='from', right_on='id')
edge_df.drop(columns=['id'],inplace=True)
edge_df.rename(columns={'x':'from_x','y':'from_y'}, inplace=True)

edge_df = pd.merge(edge_df, node_df, left_on='to', right_on='id')
edge_df.drop(columns=['id'],inplace=True)
edge_df.rename(columns={'x':'to_x','y':'to_y'}, inplace=True)
del node_df

# Create new column for edge_length
edge_df['edge_length'] = np.sqrt((edge_df['to_y']-edge_df['from_y'])**2+(edge_df['to_x']-edge_df['from_x'])**2)
edge_df.drop(columns=['from','to','from_x','from_y','to_x','to_y'],inplace=True)

# Get number of cars and number of buses
vehicle_df = pd.read_csv('gps_detail/part-00000-9d5459a4-cc5a-49ac-a341-40bc2ef458d6-c000.csv')
#vehicle_df = pd.read_csv('test_vehicle_data.csv')
num_cars_df = vehicle_df.where(vehicle_df['vehicle_type'] == 'Car').groupby(by=["date", "time", "edge"]).agg(num_cars=('speed','count')).reset_index()
num_bus_df = vehicle_df.where(vehicle_df['vehicle_type'] == 'Bus').groupby(by=["date", "time", "edge"]).agg(num_bus=('speed','count')).reset_index()
del vehicle_df

# Join them together
vehicle_count = pd.merge(num_cars_df, num_bus_df, on=['date','time','edge'], how='outer')
vehicle_count.fillna(value={'num_cars':0,'num_bus':0},inplace=True)
vehicle_count['num_vehicles'] = vehicle_count['num_cars'] + vehicle_count['num_bus']*4
vehicle_count.drop(columns=['num_cars','num_bus'],inplace=True)
del num_cars_df
del num_bus_df

# Join edge lengths onto vehicle_count
density_df = pd.merge(vehicle_count, edge_df, left_on='edge', right_on='edge_id')
density_df.drop(columns=['edge_id'],inplace=True)
del edge_df

# New column for density (num_vehicles/edge_length)
density_df['density'] = density_df['num_vehicles'].astype('float') / density_df['edge_length'].astype('float')
density_df.drop(columns=['num_vehicles','edge_length'],inplace=True)

# Create one time column
density_df['time'] = (density_df['date']-1)*1999 + density_df['time']
density_df.drop(columns=['date'], inplace=True)

def time_plot():
    plt.xlabel('time')
    plt.ylabel('density')
    max_density = density_df.groupby(by=['edge']).agg(max_density=('density','max')).reset_index().sort_values(by='max_density')
    partitions = [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8]
    for i in range(0,len(partitions)-1):
        left = partitions[i]
        right = partitions[i+1]
        edges = max_density[(max_density['max_density']>=left) & (max_density['max_density']<right)]
        for index, row in edges.iterrows():
            df_edge = density_df[density_df['edge'] == row['edge']]
            plt.scatter(df_edge['time'], df_edge['density'], label=row['edge'] ,s=2)
        plt.legend()
        plt.savefig("max_density_"+str(left)+"_to_"+str(right)+".png")
        plt.clf()
        plt.cla()
        plt.xlabel('time')
        plt.ylabel('density')

def max_density_distribution():
    plt.xlabel('max density')
    plt.ylabel('frequency')
    plt.title('Max Density Histogram, Bins=100')

    max_density = density_df.groupby(by=['edge']).agg(max_density=('density','max')).reset_index().sort_values(by='max_density')
    plt.hist(max_density['max_density'], bins=100)
    plt.show()

#time_plot()
max_density_distribution()
