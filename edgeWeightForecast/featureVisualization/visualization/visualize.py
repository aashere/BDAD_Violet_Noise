import sys
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import cm

# Plot histograms of density, deltas, vehicle counts overall
def histogram_overall(xscale):
    df = pd.read_csv("visualizations/histogram/overall/histogram_overall.csv").dropna()
    hists = df.columns.values.tolist()
    hists.remove("interval")
    hists.remove("edge")
    # Whole simulation
    for hist in hists:
        plt.xlabel(hist)
        plt.ylabel('Frequency')
        plt.title(hist + ' Histogram (Overall)')
        bins = 50
        if xscale == 'log':
            plt.xscale('log')
            bins = np.logspace(np.log10(0.001), np.log10(1.0), num=50, endpoint=True)
        plt.hist(df[hist], bins=bins)
        plt.savefig(hist+"_histogram_"+ xscale +"_overall.png")
        plt.clf()
        plt.cla()
        print(hist + " done!")
    print("histogram_overall " + xscale +" done!")

    # First week
    df = df[df['interval']<=672]
    for hist in hists:
        plt.xlabel(hist)
        plt.ylabel('Frequency')
        plt.title(hist + ' Histogram (Overall, First Week)')
        bins = 50
        if xscale == 'log':
            plt.xscale('log')
            bins = np.logspace(np.log10(0.001), np.log10(1.0), num=50, endpoint=True)
        plt.hist(df[hist], bins=bins)
        plt.savefig(hist+"_histogram_"+ xscale +"_overall_first_week.png")
        plt.clf()
        plt.cla()
        print(hist + " done!")
    print("histogram_overall_first_week " + xscale +" done!")
    

# Plot histograms for avg density, deltas, vehicle counts by edge
def histogram_edge_avg(xscale):
    df = pd.read_csv("visualizations/histogram/edge_avg/histogram_edge_avg.csv").dropna()
    hists = df.columns.values.tolist()
    hists.remove("edge")
    for hist in hists:
        plt.xlabel(hist)
        plt.ylabel('Frequency')
        plt.title(hist + ' Histogram (by Edge)')
        bins = 50
        if xscale == 'log':
            plt.xscale('log')
            bins = np.logspace(np.log10(0.001), np.log10(1.0), num=50, endpoint=True)
        plt.hist(df[hist], bins=bins)
        plt.savefig(hist+"_histogram_"+ xscale +"_by_edge.png")
        plt.clf()
        plt.cla()
        print(hist + " done!")
    print("histogram_edge_avg " + xscale +" done!")

# Plot histograms for avg density, deltas, vehicle counts by interval
def histogram_interval_avg(xscale):
    df = pd.read_csv("visualizations/histogram/interval_avg/histogram_interval_avg.csv").dropna()
    hists = df.columns.values.tolist()
    hists.remove("interval")
    for hist in hists:
        plt.xlabel(hist)
        plt.ylabel('Frequency')
        plt.title(hist + ' Histogram (by Interval)')
        bins = 50
        if xscale == 'log':
            plt.xscale('log')
            bins = np.logspace(np.log10(0.001), np.log10(1.0), num=50, endpoint=True)
        plt.hist(df[hist], bins=bins)
        plt.savefig(hist+"_histogram_"+ xscale +"_by_interval.png")
        plt.clf()
        plt.cla()
        print(hist + " done!")
    print("histogram_interval_avg " + xscale +" done!")

# Plot average of edge densities, deltas, and vehicle counts against time
def edge_time_series():
    df = pd.read_csv("visualizations/edge_time_series/edge_time_series.csv")
    cols = df.columns.values.tolist()
    cols.remove("interval")
    cols.remove("avg(tot_vehicle_count)")
    
    # Whole simulation
    for col in cols:
        plt.xlabel('Time (15 minute intervals)')
        plt.ylabel('Density (vehicles per unit area)')
        plt.title(col+" over Time")

        plt.scatter(df['interval'], df[col],s=1)
        plt.savefig("edge_time_series_"+col+".png")
        print("edge_time_series "+ col +" done!")

        plt.clf()
        plt.cla()

    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Vehicle Count (vehicles)')
    plt.title("avg(tot_vehicle_count) over Time")

    plt.scatter(df['interval'], df['avg(tot_vehicle_count)'],s=1)
    plt.savefig("edge_level_time_series_avg(tot_vehicle_count).png")
    print("edge_time_series  avg(tot_vehicle_count) done!")

    plt.clf()
    plt.cla()

    # First week
    df = df[df['interval']<=672]
    for col in cols:
        plt.xlabel('Time (15 minute intervals)')
        plt.ylabel('Density (vehicles per unit area)')
        plt.title(col+" over Time (First Week)")

        plt.scatter(df['interval'], df[col],s=1)
        plt.savefig("edge_time_series_"+col+"_first_week.png")
        print("edge_time_series "+ col +" first week done!")

        plt.clf()
        plt.cla()

    plt.xlabel('Time (15 minute intervals)')
    plt.ylabel('Vehicle Count (vehicles)')
    plt.title("avg(tot_vehicle_count) over Time (First Week)")

    plt.scatter(df['interval'], df['avg(tot_vehicle_count)'],s=1)
    plt.savefig("edge_level_time_series_avg(tot_vehicle_count)_first_week.png")
    print("edge_time_series  avg(tot_vehicle_count) first week done!")

    plt.clf()
    plt.cla()

# Plot streets and avenue densities against time
def road_time_series():
    # Streets
    df = pd.read_csv("visualizations/road_time_series/st/st_time_series.csv")
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
    df = pd.read_csv("visualizations/road_time_series/ave/ave_time_series.csv")
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
def total_trip_time():
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
    if args["--type"] == "histogram_overall":
        histogram_overall(xscale=args["--xscale"])
    elif args["--type"] == "histogram_edge_avg":
        histogram_edge_avg(xscale=args["--xscale"])
    elif args["--type"] == "histogram_interval_avg":
        histogram_interval_avg(xscale=args["--xscale"])
    elif args["--type"] == "plot_edge":
        edge_time_series()
    elif args["--type"] == "plot_road":
        road_time_series()
    elif args["--type"] == "total_trip_time":
        total_trip_time()