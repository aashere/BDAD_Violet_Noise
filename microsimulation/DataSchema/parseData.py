import xml.etree.ElementTree as ET
import pandas as pd
import json

tree = ET.parse('../sumoDataGeneration/data/my_nodes.nod.xml')
root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('node')])
df.to_csv('sampleSchema/node_table.csv', index=False)

tree = ET.parse('../sumoDataGeneration/data/my_edge.edg.xml')
root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('edge')])
df.to_csv('sampleSchema/edge_table.csv', index=False)

tree = ET.parse('../sumoDataGeneration/data/my_type.type.xml')
root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('type')])
df.to_csv('sampleSchema/road_type.csv', index=False)

tree = ET.parse('../sumoDataGeneration/data/my_route.rou.xml')
root = tree.getroot()
with open('sampleSchema/route.json', 'w') as fp:
    data = {row.attrib['id']:row.attrib['edges'] for row in root.findall('route')}
    json.dump(data, fp, sort_keys=True, indent=4)

root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('vType')])
df.to_csv('sampleSchema/vehicle_type.csv', index=False)

root = tree.getroot()
df = pd.DataFrame([row.attrib for row in root.findall('vehicle')])
df.to_csv('sampleSchema/traffic_plan.csv', index=False)

data = []
tree = ET.parse('../sumoDataGeneration/data/sumoTrace.xml')
root = tree.getroot()
for child in root:
    for v in child.findall('vehicle'):
        v.attrib.update(child.attrib)
        data.append(v.attrib)

df = pd.DataFrame(data)
df.to_csv('sampleSchema/gps_table.csv', index=False)
