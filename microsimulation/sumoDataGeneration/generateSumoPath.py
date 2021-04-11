import networkx as nx
import pickle
import json
import numpy as np
import itertools
# Dict of all paths from every source to every sink.
# This gives all the routes. It is a dict that maps
# (source, sink): [list of routes in ascending order of size]

with open("data/busschedule.json",'r') as inf:
    busroute = json.loads(inf.read())
    for record in busroute:
        key = record['busroute']
        record['busroute'] = tuple([int(i) for i in key.split("|")])

with open("data/pathdict.json", 'r') as inf:
    paths = json.load(inf)
    paths = {tuple([int(i) for i in k.split("|")]): v for k, v in paths.items()}

with open("data/graph.p", "rb") as f:
    DG = pickle.load(f)

sources = [i for i in DG.nodes if 'source' in DG.nodes[i]['nodetype']]
vehicle_assigns = []
# do we need to edit the type here below? or is that in my types?

with open('data/my_route.rou.xml', 'w') as f:
    f.write('<routes>\n')
    f.write('<vType accel="1.0" decel="3.9" id="Bus" length="15.0" maxSpeed="25" sigma="0.5" />\n')
    f.write('<vType accel="2.7" decel="4.6" id="Car1" length="4.0" maxSpeed="25" sigma="0.5" />\n')
    f.write('<vType accel="2.4" decel="4.5" id="Car2" length="5.0" maxSpeed="25" sigma="0.5" />\n')
    f.write('<vType accel="1.9" decel="4.3" id="Car3" length="7.0" maxSpeed="25" sigma="0.5" />\n')
    
    for pair in paths:
        for record in paths[pair]:
            path = record["path"]
            if record["length"] == 0 or not path:
                continue
            tmp_route = [DG.nodes[path[i]]['name'] + 'to' + DG.nodes[path[i + 1]]['name'] for i in range(record["length"]-1)]
            f.write('<route id="route{}" edges="{}"/>\n'.format(record["path_id"], ' '.join(tmp_route)))

    cardist = ["Car1"]*3 + ["Car2"]*6 + ["Car3"]
    vehicle_id = 0
    for source in sources:
        vol_rates = DG.nodes[source]["volume_rate"]  # dict of minute start to rate {0:3, 15:4, etc.}
        for minute in vol_rates: 
            rate = vol_rates[minute]
            cars = int(np.random.poisson(rate))
            spread_cars = [min(i,2) for i in np.histogram(list(range(cars)),bins=15,density=False)[0]]
            timestamps = [i + minute for i in range(15)]
            cars_per_min = [i for i in zip(timestamps, spread_cars) if i[1] > 0]
            for carstart in cars_per_min:
                time = carstart[0]
                for car in range(carstart[1]):
                    vehicle_id +=1
                    options = [v for k, v in paths.items() if k[0] == source]
                    options = list(itertools.chain.from_iterable(options))
                    options = [i["path_id"] for i in options]
                    assignment = {"time": time, "vehicle_id": vehicle_id, "route_id": np.random.choice(options), "type":np.random.choice(cardist)}
                    vehicle_assigns.append(assignment)

    for hour in range(24):
        for bus in busroute:
            if hour in bus['active_hrs']:
                options = paths[bus['busroute']]
                path_id = [i["path_id"] for i in options if i["turns"] <=1][0]
                vehicle_id +=1
                minute = busroute[pair] + hour*60
                vehiclestr = '<vehicle depart="{}" id="veh{}" route="route{}" type="{}">\n'.format(minute, vehicle_id, path_id, "Bus")
                stopstr = "".join(["<stop busStop=\"%s\" duration=\"20\"/>\n" % i for i in bus['stops']])
                vehiclestr = vehiclestr + stopstr + "</vehicle>\n"
                assignment = {"time":minute, "vxml":vehiclestr}
                vehicle_assigns.append(assignment)
                
    vehicle_assigns.sort(key = lambda x: x["time"])

    for v in vehicle_assigns:
        f.write('<vehicle depart="{}" id="veh{}" route="route{}" type="{}"/>\n'.format(v["time"], v["vehicle_id"], v["route_id"], v["type"]))

    f.write('</routes>\n')
