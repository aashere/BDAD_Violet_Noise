import sys
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import cm

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
    plt.xlabel('Time (s)')
    plt.ylabel('Density (vehicles per unit length)')
    
    # Get list of unique edges
    edge_list = unique_edges
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
            plt.xlabel('Time (s)')
            plt.ylabel('Density (vehicles per unit length)')
            fig, ax = plt.subplots()
    else:
        for edge in edge_list:
                df_edge = density_df[density_df['edge'] == edge]
                plt.scatter(df_edge['time'], df_edge['density'], label=edge, s=1)
        plt.legend(loc='upper left')
        plt.title("Edge Weights over Time")
        plt.savefig("edge_weight_plot.png")

def plot_road_edge_weights():
    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('Time (s)')
    plt.ylabel('Density (vehicles per unit length)')
    plt.title('Street-Level Density over Time')
    
    print(density_df['edge'].str.split(pat="(?<!g)(to)(?!n)").str[0])

    streets = [str(i+30) for i in range(0,29)]
    for street in streets:
        #street_edges = density_df[density_df['edge'].str.split(pat="(?<!g)(to)(?!n)")]
        pass
    #colors = [colormap(i) for i in np.linspace(0, 1,len(edge_group))]

    '''
    plt.savefig("street_edge_weight_plot.png")
    plt.clf()
    plt.cla()

    aves = ["9","8","7","6","5","Madison","Park","Lexington"]
    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('Time (s)')
    plt.ylabel('Density (vehicles per unit length)')
    plt.title('Avenue-Level Density over Time')

    colors = [colormap(i) for i in np.linspace(0, 1,len(edge_group))]
    plt.savefig("avenue_edge_weight_plot.png")
    '''

def max_density_histogram(edges=None, xscale='linear', min_xlog=0.001, max_xlog=1.0, num_bins=10):
    '''
        Use this function to generate a histogram of the max density
        each edge attains over the whole simulation.

        Parameters:
            edges:      List of edges to use for histogram. Default is
                        all edges.
            xscale:     Scale for x axis. Log scale used if 'log' passed in.
                        Default is linear scale.
            min_xlog:   Minimum value on x axis for log scale. Default 0.001.
            max_xlog:   Maximum value on x axis for log scale. Default 1.0.
            num_bins:   Number of bins for histogram. Default is 10.
    '''
    plt.xlabel('Max Density (vehicles per unit length)')
    plt.ylabel('Frequency')
    plt.title('Max Density Histogram')

    max_density = density_df.groupby(by=['edge']).agg(max_density=('density','max')).reset_index().sort_values(by='max_density')
    if edges:
        max_density = max_density[max_density['edge'].isin(edges)]
    bins = num_bins
    if xscale == 'log':
        plt.xscale('log')
        bins = np.logspace(np.log10(min_xlog), np.log10(max_xlog), num=10, endpoint=True)
    plt.hist(max_density['max_density'], bins=bins)
    plt.savefig("max_density_histogram.png")

def total_trip_time_histogram():
    pass

def deltas_histogram():
    pass

if __name__ == "__main__":    
    pass