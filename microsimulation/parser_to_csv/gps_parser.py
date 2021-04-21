# python gps_parser.py > gps_table.csv

import sys
from xml.dom import minidom
import csv

xmldoc = minidom.parse('sumoTrace.xml')
gps_list = xmldoc.getElementsByTagName('timestep')
print("{},{},{},{},{},{},{},{},{},{}".format("id", "x", "y", "angle", "type", "speed", "pos", "lane", "slope", "time"))
for s in gps_list:
    vehicles = s.getElementsByTagName('vehicle')
    for v in vehicles:
        print("{},{},{},{},{},{},{},{},{},{}".format(v.attributes['id'].value, v.attributes['x'].value, v.attributes['y'].value, \
            v.attributes['angle'].value, v.attributes['type'].value, v.attributes['speed'].value, v.attributes['pos'].value, \
                v.attributes['lane'].value, v.attributes['slope'].value, s.attributes['time'].value))
