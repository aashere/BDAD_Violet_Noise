import pandas as pd
import matplotlib.pyplot as plt
#import seaborn as sns

df = pd.read_csv('gps_detail/part-00000-9d5459a4-cc5a-49ac-a341-40bc2ef458d6-c000.csv')
df_agg = df.groupby(by=["date", "time", "edge"]).agg(num_cars=('speed','count'),
                                                     avg_apeed=('speed','mean')).reset_index()

def time_plot(edge):
    df_edge = df_agg.loc[df_agg['edge'] == edge]
    fig, ax1 = plt.subplots(figsize=(20,16))
    color = 'tab:red'
    ax1.set_xlabel('time')
    ax1.set_ylabel('avg_apeed', color=color)
    ax1.plot(df_edge['time'], df_edge['avg_apeed'], color=color)
    ax1.tick_params(axis='y', labelcolor=color)

    ax2 = ax1.twinx()  # instantiate a second axes that shares the same x-axis

    color = 'tab:blue'
    ax2.set_ylabel('num_cars', color=color)  # we already handled the x-label with ax1
    ax2.plot(df_edge['time'], df_edge['num_cars'], color=color)
    ax2.tick_params(axis='y', labelcolor=color)

    fig.tight_layout()  # otherwise the right y-label is slightly clipped
    plt.show()

time_plot('9_30to8_30')



# another plot
# df = pd.read_csv('gps_table.csv')  # change the file path first
# sns.distplot(df.loc[df['type'] == 'Car']['speed'])
# sns.distplot(df.loc[df['type'] == 'Bus']['speed'])
# plt.show()