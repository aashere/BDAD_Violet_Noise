## python node_parser.py > node_table.csv

from xml.dom import minidom
import csv

xmldoc = minidom.parse('my_nodes.nod.xml')
route_list = xmldoc.getElementsByTagName('node')
print("{},{},{}".format("id", "x", "y"))
for s in route_list:
    print("{},{},{}".format(s.attributes['id'].value, s.attributes['x'].value, s.attributes['y'].value))