import xml.etree.ElementTree as ET
import pandas as pd
import json
import sys
sys.path.insert(0, "../sumoDataGeneration")
from helpers import get_gps_coords

### THESE OUTPUTS SHOULD NOT CHANGE FROM EACH RUN
tree = ET.parse('../sumoDataGeneration/data/my_nodes.nod.xml')
root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('node')])
gps = df[["x","y"]].astype(int).applymap(lambda s: max([0.0, s/100]))
gps = gps.apply(lambda s: pd.Series(get_gps_coords(s["x"],s["y"])),axis=1)
gps = gps.rename(columns={0:"latitude", 1:"longitude"})
df = df.join(gps)
df.to_csv('sampleSchema/node_table.csv', index=False)

tree = ET.parse('../sumoDataGeneration/data/my_edge.edg.xml')
root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('edge')])
df.to_csv('sampleSchema/edge_table.csv', index=False)

tree = ET.parse('../sumoDataGeneration/data/my_type.type.xml')
root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('type')])
df.to_csv('sampleSchema/road_type.csv', index=False)


## The route and vType elements are the same in every route file
tree = ET.parse('../sumoDataGeneration/data/routes/week_0_route.rou.xml')
root = tree.getroot()
with open('sampleSchema/route.json', 'w') as fp:
    data = {row.attrib['id']:row.attrib['edges'] for row in root.findall('route')} 
    json.dump(data, fp, sort_keys=True, indent=4)

root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('vType')])
df.to_csv('sampleSchema/vehicle_type.csv', index=False)


### This part isn't scaling

# WEEKS=1  ## t
# for i in range(WEEKS):
#     fn = "week_%s_route.rou.xml" % i
#     pathstr = "../sumoDataGeneration/data/routes/" + fn
#     tree = ET.parse(pathstr)
#     root = tree.getroot()
#     df = pd.DataFrame([row.attrib for row in root.findall('vehicle')])
#     output = "sampleSchema/traffic_plan_week%s.csv" % i
#     df.to_csv(output, index=False)

### need to replace this with full list of trace files
# for tracefn in ["sumoTrace.xml"]:
#     pathstr = "../sumoDataGeneration/data/traces/" + tracefn
#     data = [] 
#     tree = ET.parse(pathstr)
#     root = tree.getroot()
#     for child in root:
#         for v in child.findall('vehicle'):
#             v.attrib.update(child.attrib)
#             data.append(v.attrib)

#     df = pd.DataFrame(data)
#     # gps = df[["x","y"]].astype(float).applymap(lambda s: max([0.0, s/100]))
#     # gps = gps.apply(lambda s: pd.Series(get_gps_coords(s["x"],s["y"])),axis=1)
#     # gps = gps.rename(columns={0:"latitude", 1:"longitude"})
#     # df = df.join(gps)

#     output = "sampleSchema/gps_table_%s.csv" % tracefn.split(".")[0]
#     df.to_csv(output, index=False)
