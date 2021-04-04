from xml.dom import minidom

xmldoc = minidom.parse('my_route.rou.xml')
vehicle_type_list = xmldoc.getElementsByTagName('vType')
print("{}\t{}\t{}\t{}\t{}".format("v_type", "length", "max_speed", "accel", "decel"))
for s in vehicle_type_list:
    print("{}\t{}\t{}\t{}\t{}".format(s.attributes['id'].value, s.attributes['length'].value, s.attributes['maxSpeed'].value, 
    s.attributes['accel'].value, s.attributes['decel'].value))