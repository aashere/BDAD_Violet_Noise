import xml.etree.ElementTree as ET
import pandas as pd
import json
import sys
import os
from helpers import get_gps_coords

inputdir = r"/scratch/hls327/sumoDataGeneration/data/net"
outputdir = r"/scratch/hls327/graphStructure"

tree = ET.parse(os.path.join(inputdir, "my_nodes.nod.xml"))
root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('node')])
gps = df[["x","y"]].astype(int).applymap(lambda s: max([0.0, s/100]))
gps = gps.apply(lambda s: pd.Series(get_gps_coords(s["x"],s["y"])),axis=1)
gps = gps.rename(columns={0:"latitude", 1:"longitude"})
df = df.join(gps)
df.to_csv(os.path.join(outputdir, "node_table.csv"), index=False)

tree = ET.parse(os.path.join(inputdir, "my_edge.edg.xml"))
root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('edge')])
df.to_csv(os.path.join(outputdir, "edge_table.csv"), index=False)

tree = ET.parse(os.path.join(inputdir, "my_type.type.xml"))
root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('type')])
df.to_csv(os.path.join(outputdir, "road_type.csv"), index=False)

vcols = ["accel", "decel", "id", "length", "maxSpeed", "sigma"]
vtypes = [[1.0, 3.5, "Bus", 15.0, 8.0, 0.5],
        [2.7, 4.6, "Car1", 4.0, 11.2, 0.5],
        [2.4, 4.5, "Car2", 5.0, 11.2, 0.5],
        [1.9, 4.3, "Car3", 7.0, 11.2, 0.5]]

df = pd.DataFrame(vtypes, columns=vcols)
df.to_csv(os.path.join(outputdir, "vehicle_type.csv"), index=False)