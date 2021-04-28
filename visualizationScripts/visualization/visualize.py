import sys
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import cm

# Plot max, min, avg histograms for fields from regression feature table
def deltas_histogram(xscale):
    df = pd.read_csv("visualizations/delta_histogram/delta_hist.csv")
    hists = df.columns.values.tolist()
    hists.remove("edge")
    for hist in hists:
        plt.xlabel(hist)
        plt.ylabel('Frequency')
        plt.title(hist + ' Histogram')
        bins = 50
        if xscale == 'log':
            plt.xscale('log')
            bins = np.logspace(np.log10(0.001), np.log10(1.0), num=50, endpoint=True)
        plt.hist(df[hist], bins=bins)
        plt.savefig(hist+"_histogram_"+ xscale +".png")
        plt.clf()
        plt.cla()
        print(hist + " done!")
    print("delta_hist " + xscale +" done!")

# Plot average of edge densities against time
def edge_level_time_series():
    df = pd.read_csv("visualizations/edge_time_series/plot_edge.csv")
    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Density (vehicles per unit area)')
    plt.title("Edge Weights over Time")

    plt.scatter(df['interval'], df['avg(tot_density)'],s=1)
    plt.savefig("edge_level_time_series_density.png")
    print("plot_edge density done!")

    plt.clf()
    plt.cla()

    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Vehicle Count (vehicles)')
    plt.title("Vehicle Count over Time")

    plt.scatter(df['interval'], df['avg(tot_vehicle_count)'],s=1)
    plt.savefig("edge_level_time_series_vehicle_count.png")
    print("plot_edge vehicle count done!")

    plt.clf()
    plt.cla()

    # First week of data
    df= df[df['interval']<=672]
    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Density (vehicles per unit area)')
    plt.title("Edge Weights over Time (First Week)")

    plt.scatter(df['interval'], df['avg(tot_density)'],s=1)
    plt.savefig("edge_level_time_series_density_first_week.png")
    print("plot_edge density first week done!")

    plt.clf()
    plt.cla()

    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Vehicle Count (vehicles)')
    plt.title("Vehicle Count over Time (First Week)")

    plt.scatter(df['interval'], df['avg(tot_vehicle_count)'],s=1)
    plt.savefig("edge_level_time_series_vehicle_count_first_week.png")
    print("plot_edge vehicle count first week done!")

# Plot streets and avenue densities against time
def road_level_time_series():
    # Streets
    df = pd.read_csv("visualizations/road_time_series/st/plot_st.csv")
    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Density (vehicles per unit area)')
    plt.title('Street-Level Density over Time')

    streets = [str(i+30) for i in range(0,29)]
    colors = [colormap(i) for i in np.linspace(0, 1,len(streets))]
    ax.set_prop_cycle('color', colors)
    for street in streets:
        st_df = df[df['st'].astype('str') == street]
        plt.scatter(st_df['interval'],st_df['agg_density'],label=street,s=1)
    plt.legend(loc='upper left')
    plt.savefig("street_level_time_series_density.png")
    print("plot_street density done!")

    plt.clf()
    plt.cla()

    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Vehicle Count (vehicles)')
    plt.title('Street-Level Vehicle Count over Time')

    colors = [colormap(i) for i in np.linspace(0, 1,len(streets))]
    ax.set_prop_cycle('color', colors)
    for street in streets:
        st_df = df[df['st'].astype('str') == street]
        plt.scatter(st_df['interval'],st_df['agg_vehicle_count'],label=street,s=1)
    plt.legend(loc='upper left')
    plt.savefig("street_level_time_series_vehicle_count.png")
    print("plot_street vehicle count done!")

    plt.clf()
    plt.cla()

    # First week for streets
    df = df[df['interval']<=672]
    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Density (vehicles per unit area)')
    plt.title('Street-Level Density over Time (First Week)')

    streets = [str(i+30) for i in range(0,29)]
    colors = [colormap(i) for i in np.linspace(0, 1,len(streets))]
    ax.set_prop_cycle('color', colors)
    for street in streets:
        st_df = df[df['st'].astype('str') == street]
        plt.scatter(st_df['interval'],st_df['agg_density'],label=street,s=1)
    plt.legend(loc='upper left')
    plt.savefig("street_level_time_series_density_first_week.png")
    print("plot_street density first week done!")

    plt.clf()
    plt.cla()

    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Vehicle Count (vehicles)')
    plt.title('Street-Level Vehicle Count over Time (First Week)')

    colors = [colormap(i) for i in np.linspace(0, 1,len(streets))]
    ax.set_prop_cycle('color', colors)
    for street in streets:
        st_df = df[df['st'].astype('str') == street]
        plt.scatter(st_df['interval'],st_df['agg_vehicle_count'],label=street,s=1)
    plt.legend(loc='upper left')
    plt.savefig("street_level_time_series_vehicle_count_first_week.png")
    print("plot_street vehicle count first week done!")

    plt.clf()
    plt.cla()

    # Avenues
    df = pd.read_csv("visualizations/road_time_series/ave/plot_ave.csv")
    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Density (vehicles per unit area)')
    plt.title('Avenue-Level Density over Time')

    aves = ["9","8","7","6","5","Madison","Park","Lexington"]
    colors = [colormap(i) for i in np.linspace(0, 1,len(aves))]
    ax.set_prop_cycle('color', colors)
    for ave in aves:
        ave_df = df[df['ave'] == ave]
        plt.scatter(ave_df['interval'],ave_df['agg_density'],label=ave,s=1)
    plt.legend(loc='upper left')
    plt.savefig("avenue_level_time_series_density.png")
    print("plot_ave density done!")

    plt.clf()
    plt.cla()

    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Vehicle Count (vehicles)')
    plt.title('Avenue-Level Vehicle Count over Time')

    colors = [colormap(i) for i in np.linspace(0, 1,len(aves))]
    ax.set_prop_cycle('color', colors)
    for ave in aves:
        ave_df = df[df['ave'] == ave]
        plt.scatter(ave_df['interval'],ave_df['agg_vehicle_count'],label=ave,s=1)
    plt.legend(loc='upper left')
    plt.savefig("avenue_level_time_series_vehicle_count.png")
    print("plot_ave vehicle count done!")

    # First week for avenues
    df = df[df['interval']<=672]
    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Density (vehicles per unit area)')
    plt.title('Avenue-Level Density over Time (First Week)')

    aves = ["9","8","7","6","5","Madison","Park","Lexington"]
    colors = [colormap(i) for i in np.linspace(0, 1,len(aves))]
    ax.set_prop_cycle('color', colors)
    for ave in aves:
        ave_df = df[df['ave'] == ave]
        plt.scatter(ave_df['interval'],ave_df['agg_density'],label=ave,s=1)
    plt.legend(loc='upper left')
    plt.savefig("avenue_level_time_series_density_first_week.png")
    print("plot_ave density first week done!")

    plt.clf()
    plt.cla()

    fig, ax = plt.subplots()
    colormap = plt.cm.nipy_spectral
    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Vehicle Count (vehicles)')
    plt.title('Avenue-Level Vehicle Count over Time (First Week)')

    colors = [colormap(i) for i in np.linspace(0, 1,len(aves))]
    ax.set_prop_cycle('color', colors)
    for ave in aves:
        ave_df = df[df['ave'] == ave]
        plt.scatter(ave_df['interval'],ave_df['agg_vehicle_count'],label=ave,s=1)
    plt.legend(loc='upper left')
    plt.savefig("avenue_level_time_series_vehicle_count_first_week.png")
    print("plot_ave vehicle count first week done!")

# Plot histogram of total trip time across all vehicles
def total_trip_time_histogram():
    df = pd.read_csv("visualizations/total_trip_time/total_trip_time.csv")
    plt.xlabel("Total Trip Time (s)")
    plt.ylabel('Frequency')
    plt.title('Total Trip Time Histogram')
    bins = 50
    plt.hist(df["total_trip_time"], bins=bins)
    plt.savefig("total_trip_time_histogram.png")
    print("total_trip_time done!")

if __name__ == "__main__":    
    args = dict()
    for i, arg in enumerate(sys.argv):
        if i!=0:
            args[arg.split("=")[0]] = arg.split("=")[1]
    if args["--type"] == "delta_hist":
        deltas_histogram(xscale=args["--xscale"])
    elif args["--type"] == "plot_edge":
        edge_level_time_series()
    elif args["--type"] == "plot_road":
        road_level_time_series()
    elif args["--type"] == "total_trip_time":
        total_trip_time_histogram()