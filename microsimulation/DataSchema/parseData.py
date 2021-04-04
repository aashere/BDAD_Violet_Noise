import xml.etree.ElementTree as ET
import pandas as pd

tree = ET.parse('../sumoDataGeneration/data/my_nodes.nod.xml')
root = tree.getroot()

df = pd.DataFrame([row.attrib for row in root.findall('node')])
df.to_csv('node.csv', index=False)