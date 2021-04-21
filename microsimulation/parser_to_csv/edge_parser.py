## python edge_parser.py > edge_table.csv

from xml.dom import minidom
import csv

xmldoc = minidom.parse('my_edge.edg.xml')
route_list = xmldoc.getElementsByTagName('edge')
print("{},{},{},{}".format("from", "to", "id", "type"))
for s in route_list:
    print("{},{},{},{}".format(s.attributes['from'].value, s.attributes['to'].value, s.attributes['id'].value, s.attributes['type'].value))