import networkx as nx
import pickle
import json
import numpy as np
import pandas
import itertools
# Dict of all paths from every source to every sink.
# This gives all the routes. It is a dict that maps
# (source, sink): [list of routes in ascending order of size]

class PathGenerator:
    def __init__(self, basevol=15.0):
        self.lambdaavg = pandas.read_csv("data/inputs/weekdaymeanlambdas.csv").set_index('Seconds').applymap(lambda s: s*basevol)
        
        with open("data/inputs/busschedule.json",'r') as inf:
            busroute = json.loads(inf.read())
            for record in busroute:
                key = record['busroute']
                record['busroute'] = tuple([int(i) for i in key.split("|")])
        self.busroute = busroute

        with open("data/inputs/pathdict.json", 'r') as inf:
            paths = json.load(inf)
            self.paths = {tuple([int(i) for i in k.split("|")]): v for k, v in paths.items()}

        with open("data/inputs/graph.p", "rb") as f:
            self.DG = pickle.load(f)

        self.cardist = ["Car1"]*3 + ["Car2"]*6 + ["Car3"]        
        self.vehicle_id = 0


    def _generate_car_traffic(self, start_tm_seconds, weekday):
        vehicle_assigns = []
        sources = [i for i in self.DG.nodes if 'source' in self.DG.nodes[i]['nodetype']]
        daykey = str(weekday)
        meanlambda = self.lambdaavg[daykey].to_dict()
        
        for source in sources:
            vol_rates = self.DG.nodes[source]["volume_rate"]  # dict of minute start to rate diff from mean {0:3, 15:4, etc.}
            for minute in vol_rates:
                rate = vol_rates[minute] * meanlambda[minute]
                cars = int(np.random.poisson(rate))
                
                spread_cars = [min(i,2) for i in np.histogram(list(range(cars)),bins=900,density=False)[0]]
                timestamps = [i + minute*60 + start_tm_seconds for i in range(900)]
                cars_per_sec = [i for i in zip(timestamps, spread_cars) if i[1] > 0]
                for carstart in cars_per_sec:
                    timeseconds = carstart[0]
                    for car in range(carstart[1]):
                        self.vehicle_id +=1
                        options = [v for k, v in self.paths.items() if k[0] == source]
                        options = list(itertools.chain.from_iterable(options))
                        options = [i["path_id"] for i in options]
                        vehiclestr = '<vehicle depart="{}" id="veh{}" route="route{}" type="{}"/>\n'.format(timeseconds, 
                            self.vehicle_id, np.random.choice(options), np.random.choice(self.cardist))
                        assignment = {"time": timeseconds, "vxml":vehiclestr}
                        vehicle_assigns.append(assignment)
        return vehicle_assigns


    def _generate_bus_traffic(self, start_tm_seconds):
        vehicle_assigns = []

        for hour in range(24):
            for bus in self.busroute:
                if hour in bus['active_hrs']:
                    options = self.paths[bus['busroute']]
                    path_id = [i["path_id"] for i in options if i["turns"] <=1][0]
                    self.vehicle_id +=1
                    busstartsec = (bus["start_min"] + hour*60) * 60
                    totalseconds = busstartsec + start_tm_seconds
                    vehiclestr = '<vehicle depart="{}" id="veh{}" route="route{}" type="{}">\n'.format(totalseconds, self.vehicle_id, path_id, "Bus")
                    stopstr = "".join(["<stop busStop=\"%s\" duration=\"420\"/>\n" % i for i in bus['stops']])
                    vehiclestr = vehiclestr + stopstr + "</vehicle>\n"
                    assignment = {"time":totalseconds, "vxml":vehiclestr}
                    vehicle_assigns.append(assignment)
        return vehicle_assigns


    def generate_traffic_route(self, filepath, start_tm_seconds, weekday):
        cartraffic = self._generate_car_traffic(start_tm_seconds, weekday)
        bustraffic = self._generate_bus_traffic(start_tm_seconds)
        vehicle_assigns = cartraffic + bustraffic
        vehicle_assigns.sort(key = lambda x: x["time"])

        with open(filepath, 'w') as f:
            f.write('<routes>\n')
            f.write('<vType accel="1.0" decel="3.5" id="Bus" length="15.0" maxSpeed="8.0" sigma="0.5" />\n')
            f.write('<vType accel="2.7" decel="4.6" id="Car1" length="4.0" maxSpeed="11.2" sigma="0.5" />\n')
            f.write('<vType accel="2.4" decel="4.5" id="Car2" length="5.0" maxSpeed="11.2" sigma="0.5" />\n')
            f.write('<vType accel="1.9" decel="4.3" id="Car3" length="7.0" maxSpeed="11.2" sigma="0.5" />\n')
            
            for pair in self.paths:
                for record in self.paths[pair]:
                    path = record["path"]
                    if record["length"] == 0 or not path:
                        continue
                    tmp_route = [self.DG.nodes[path[i]]['name'] + 'to' + self.DG.nodes[path[i + 1]]['name'] for i in range(record["length"]-1)]
                    f.write('<route id="route{}" edges="{}"/>\n'.format(record["path_id"], ' '.join(tmp_route)))

            for v in vehicle_assigns:
                f.write(v["vxml"])

            f.write('</routes>\n')
