from generateSumoPath import PathGenerator
import argparse

config_schema = '''<configuration> 

<input> 
<net-file value="%s/net/my_net.net.xml"/> 
<route-files value="%s"/> 
<additional-files value="%s/net/bus_stops.xml"/>
</input> 
<time> 
<begin value="%s"/> 
<end value="%s"/> 
</time> 

</configuration> 
'''
homedir = r"/scratch/hls327/sumoDataGeneration/data"

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    #parser.add_argument("weeks", type=int)
    parser.add_argument("basevol", type=float)
    args = parser.parse_args()

    generator = PathGenerator(basevol=args.basevol)
    #weeks = args.weeks
    start = 10
    weeks = 11
    days = 1

    for w in range(start,weeks):
        for d in range(0,days):
            
            startseconds = (w * 604800) + (d * 86400)
            endseconds = (w * 604800) + ((d+1) * 86400)
            
            routefile = "week_%s_day_%s_route.rou.xml" % (w, d)
            routefile = homedir + "/routes/" + routefile
            generator.generate_traffic_route(filepath=routefile, start_tm_seconds=startseconds, weekday=d)

            configname = homedir + "/configs/week_%s_day_%s_config.sumocfg" % (w,d)
            
            if startseconds > 0:
                startseconds = startseconds - 1

            print(endseconds-1)

            configdata = config_schema % (homedir, routefile, homedir, startseconds, endseconds)
            with open(configname, "w") as f:
                f.write(configdata)
            