import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import cm

node_table_path = '../sampleSchema/node_table.csv'
edge_table_path = '../sampleSchema/edge_table.csv'
time_series_path = '../OLAP/gps_detail/part-00000-9d5459a4-cc5a-49ac-a341-40bc2ef458d6-c000.csv'
#TIME_UNITS_PER_DATE_UNIT = 86400
TIME_UNITS_PER_DATE_UNIT=1999

node_df = pd.read_csv(node_table_path)
edge_df = pd.read_csv(edge_table_path)
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
vehicle_df = pd.read_csv(time_series_path)
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
density_df['time'] = (density_df['date']-1)*TIME_UNITS_PER_DATE_UNIT + density_df['time']
density_df.drop(columns=['date'], inplace=True)

def plot_edge_weights(edges=None, parts=None):
    '''
    Use this function to plot edge weights as a function of time.
    Once you generate the png file, make sure to rename it or
    move it to another directory, as another run of this function
    will overwrite file with the same name.

    Parameters:
        edges:  List of edges to plot on graph. Default plots all
                466 edges on same graph.
        parts:  List of partitions. If this parameter is supplied,
                the function will split up the edges according to
                the partitions and plot each group of edges on a 
                separate graph. The partitions are based on 
                max density attained on an edge over the whole
                simulation.

                e.g.    If parts=[0.0, 0.1, 0.2, 0.3]
                        is supplied, the edges will be grouped into
                        max_density intervals: [0.0,0.1), [0.1,0.2),
                        [0.2,0.3). Each of these intervals will be
                        plotted on separate graphs, which will be
                        saved as png files, "max_density_0.0to0.1.png",
                        "max_density_0.1to0.2.png", 
                        and "max_density_0.2to0.3", respectively.
    '''
    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('time')
    plt.ylabel('density')
    
    # Get list of unique edges
    edge_list = density_df['edge'].unique()
    if edges:
        edge_list = edges
    
    colors = [colormap(i) for i in np.linspace(0, 1,len(edge_list))]
    ax.set_prop_cycle('color', colors)
    if parts:
        #Get max density of all edges in edge_list
        max_density = density_df[density_df['edge'].isin(edge_list)].groupby(by=['edge']).agg(max_density=('density','max')).reset_index().sort_values(by='max_density')
        for i in range(0,len(parts)-1):
            left = parts[i]
            right = parts[i+1]
            edge_group = max_density[(max_density['max_density']>=left) & (max_density['max_density']<right)]['edge'].tolist()
            colors = [colormap(i) for i in np.linspace(0, 1,len(edge_group))]
            ax.set_prop_cycle('color', colors)
            for edge in edge_group:
                df_edge = density_df[density_df['edge'] == edge]
                plt.scatter(df_edge['time'], df_edge['density'], label=edge,s=1)
            plt.legend(loc='upper left')
            plt.title("Edge Weights over Time for edges with " + str(left) + " <= Max Density < " + str(right))
            plt.savefig("max_density_"+str(left)+"_to_"+str(right)+".png")
            plt.clf()
            plt.cla()
            plt.xlabel('time')
            plt.ylabel('density')
            fig, ax = plt.subplots()
    else:
        for edge in edge_list:
                df_edge = density_df[density_df['edge'] == edge]
                plt.scatter(df_edge['time'], df_edge['density'], label=edge, s=1)
        plt.legend(loc='upper left')
        plt.title("Edge Weights over Time")
        plt.savefig("edge_weight_plot.png")

def max_density_distribution(edges=None, xscale='linear', num_bins=10):
    plt.xlabel('max density')
    plt.ylabel('frequency')
    plt.title('Max Density Histogram')

    max_density = density_df.groupby(by=['edge']).agg(max_density=('density','max')).reset_index().sort_values(by='max_density')
    if edges:
        max_density = max_density[max_density['edge'].isin(edges)]
    bins = num_bins
    if xscale == 'log':
        plt.xscale('log')
        MIN = 0.001
        MAX = 1.0
        bins = np.logspace(np.log10(MIN),np.log10(MAX), 10)
    plt.hist(max_density['max_density'], bins=bins)
    plt.savefig("max_density_histogram.png")