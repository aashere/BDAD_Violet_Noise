import pandas as pd
import numpy as np
import csv

# Note: CSV files imported are files which contain the most recent data collection for our segments of interest
# Data from: https://www.dot.ny.gov/divisions/engineering/technical-services/highway-data-services/hdsb/
# Key for column names: https://www.dot.ny.gov/divisions/engineering/technical-services/highway-data-services/hdsb/repository/Field_Definitions_SC%20Formats.pdf

# Lookup table correlating node_id and RC_STATION identifier
# Table populated using: https://www.dot.ny.gov/divisions/engineering/technical-services/hds-respository/NYSDOT_2019TrafficVolumeReport-LocalRoads.pdf
lookup_table = pd.read_csv('nys_data.csv', dtype=object)

# Get rows in data for which data.RC_STATION is in lookup_table.rc_station
def lookup(data_join_key, lookup_join_key, data, lookup):
    merged = data.merge(lookup, left_on=data_join_key, right_on=lookup_join_key)
    # Note that this only works since we know there are no common column header names between
    # data and lookup in this case
    for col in [x for x in merged.columns.tolist() if x!=data_join_key and x not in data.columns.tolist()]:
        merged.drop(columns=[col], inplace=True)
        merged.drop_duplicates(inplace=True)
    return merged

raw_cols = ['RC_STATION', 'YEAR', 'MONTH', 'DAY', 'DAY_OF_WEEK', 'COLLECTION_INTERVAL', 'DATA_INTERVAL', 'CLASS_F2', 'CLASS_F4']

# Bring 2019 data into memory, get rid of leading/trailing whitespace
data_2019 = pd.read_csv('SC_CLASS_DATA_R11_2019.csv', dtype=object).applymap(lambda x: x.strip() if type(x) == str else x)
data_2019.rename(str.strip, axis='columns', inplace=True)
# Get only the columns we're interested in
data_2019 = data_2019[raw_cols]
# Select only the rows which have RC_STATION in lookup_table.rc_station
cur_data = lookup('RC_STATION', 'rc_station', data_2019, lookup_table)
del data_2019

# Get rows with RC_STATION that are:
# 1) In left but not in right
# 2) In right but not in left
# 3) If in both left and right, take the one in left
def coalesce(join_key, left, right):
    merged = left.merge(right, on=join_key, how='outer')
    for col in [x for x in left.columns.tolist() if x!=join_key]:
        merged[col] = merged[col+'_x'].combine_first(merged[col+'_y']).tolist()
        merged.drop(columns=[col+'_x', col+'_y'], inplace=True)
    return merged

data_files = ['SC_CLASS_DATA_R11_2018.csv', 'SC_Class_Data_R11_2017.csv', 'SC_Class_Data_R11_2016.csv', 'SC_Class_Data_R11_2011.csv']

for i in range(0, len(data_files)):
    new_data_name = data_files[i]

    # Bring new data into memory, get rid of leading/trailing whitespace
    new_data = pd.read_csv(new_data_name, dtype=object).applymap(lambda x: x.strip() if type(x) == str else x)
    new_data.rename(str.strip, axis='columns', inplace=True)
    # Get only the columns we're interested in
    new_data = new_data[raw_cols]
    # Select only the rows which have RC_STATION in lookup_table.rc_station
    new_data = lookup('RC_STATION', 'rc_station', new_data, lookup_table)
    # Coalesce new_data into cur_data
    cur_data = coalesce('RC_STATION', cur_data, new_data)

# cur_data now contains the most recent data entries for each RC_STATION in lookup_table.rc_station

# Rename columns we're not going to change
name_changes = {'RC_STATION': 'rc_station', 'YEAR': 'year', 
                'MONTH': 'month', 'DAY': 'day', 
                'DAY_OF_WEEK': 'day_of_week',
                'COLLECTION_INTERVAL': 'interval',
                }
cur_data.rename(name_changes, axis='columns', inplace=True)

# Join cur_data and lookup_table on rc_station to add 
# node_id, road_name, segment_start, segment_end, segment_length
cur_data = cur_data.merge(lookup_table, on='rc_station')
# We don't need rc_station column anymore
cur_data.drop(columns=['rc_station'], inplace=True)
# Get rid of duplicate year column and rename to year
cur_data.drop(columns=['year_y'], inplace=True)
cur_data.rename({'year_x': 'year'}, axis='columns', inplace=True)

# Transform DATA_INTERVAL -> (output.hour, output.minute)
# Note that output.hour and output.minute give the time at which the
# traffic count STARTED, and output.interval gives the duration of the
# traffic count
cur_data['hour'] = cur_data['DATA_INTERVAL'].apply(lambda x: str(x).split(".")[0])
def to_minute(x):
    if x == '1':
        return '0'
    elif x == '2':
        return '15'
    elif x == '3':
        return '30'
    else:
        return '45'
cur_data['minute'] = cur_data['DATA_INTERVAL'].apply(lambda x: to_minute(str(x).split(".")[1]))
cur_data.drop(columns=['DATA_INTERVAL'], inplace=True)

# Get rid of records with blank traffic counts
cur_data = cur_data[cur_data['CLASS_F2'].notna()]
cur_data = cur_data[cur_data['CLASS_F4'].notna()]
# Transform (CLASS_F2, segment_length) -> lambda_car
cur_data['lambda_car'] = np.ceil(cur_data['CLASS_F2'].astype('float') / cur_data['segment_length'].astype('float'))

# Transform (CLASS_F4, segment_length) -> lambda_bus
cur_data['lambda_bus'] = np.ceil(cur_data['CLASS_F4'].astype('float') / cur_data['segment_length'].astype('float'))
cur_data.drop(columns=['CLASS_F2', 'CLASS_F4'], inplace=True)

# Rearrange columns
cur_data = cur_data[['node_id', 'road_name', 
                    'segment_start', 'segment_end', 
                    'segment_length', 'direction',
                    'interval', 'year',
                    'month', 'day',
                    'day_of_week', 'hour', 'minute',
                    'lambda_car', 'lambda_bus']]
# Sort dataframe
cur_data['node_id'] = cur_data['node_id'].astype('int')
cur_data['year'] = cur_data['year'].astype('int')
cur_data['month'] = cur_data['month'].astype('int')
cur_data['day'] = cur_data['day'].astype('int')
cur_data['hour'] = cur_data['hour'].astype('int')
cur_data['minute'] = cur_data['minute'].astype('int')
cur_data.sort_values(by=['node_id', 'direction', 'year', 'month', 'day', 'hour', 'minute'], inplace=True)

# Output to CSV
cur_data.to_csv('lambdas.csv', index=False, encoding='utf-8')