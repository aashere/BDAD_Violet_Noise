from xml.dom import minidom

xmldoc = minidom.parse('my_route.rou.xml')
traffic_plan_list = xmldoc.getElementsByTagName('vehicle')
print("{}\t{}\t{}\t{}".format("v_id", "depart_time", "route_id", "v_type"))
for s in traffic_plan_list:
    print("{}\t{}\t{}\t{}".format(s.attributes['id'].value, s.attributes['depart'].value, s.attributes['route'].value, 
    s.attributes['type'].value))