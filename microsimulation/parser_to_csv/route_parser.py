from xml.dom import minidom
import csv

xmldoc = minidom.parse('my_route.rou.xml')
route_list = xmldoc.getElementsByTagName('route')
print("{}\t{}".format("route_id", "edges"))
for s in route_list:
    print("{}\t{}".format(s.attributes['id'].value, s.attributes['edges'].value))