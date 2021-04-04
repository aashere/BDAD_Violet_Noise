import networkx as nx
import matplotlib.pyplot as plt
import pprint
from GraphTest import DG
import time
import json
# Dict of all paths from every source to every sink.
# This gives all the routes. It is a dict that maps
# (source, sink): [list of routes in ascending order of size]
with open("data/pathdict.json", 'r') as inf:
    paths = json.load(inf)
    paths = {tuple([int(i) for i in k.split("|")]): v for k, v in paths.items()}


with open('data/my_route.rou.xml', 'a') as f:
    f.write('<routes>\n')
    f.write('<vType accel="1.0" decel="3.0" id="Bus" length="12.0" maxSpeed="10" sigma="0.0" />\n')
    f.write('<vType accel="5.0" decel="5.0" id="Car" length="3.0" maxSpeed="25" sigma="0.0" />\n')
    counter = 0
    for k, v in paths.items():
        #print(k, v)
        if not v or not v[0]:
            continue
        counter += 1
        tmp_route = [DG.nodes[v[0][i]]['name'] + 'to' + DG.nodes[v[0][i + 1]]['name'] for i in range(len(v[0]) - 1)]
        f.write('<route id="route{}" edges="{}"/>\n'.format(counter, ' '.join(tmp_route)))
        if counter % 2 == 0:
            f.write(
                    '<vehicle depart="{}" id="veh{}" route="route{}" type="Bus"/>\n'.format(counter, counter, counter))
        else:
            f.write(
                    '<vehicle depart="{}" id="veh{}" route="route{}" type="Car"/>\n'.format(counter, counter, counter))
    f.write('</routes>\n')
