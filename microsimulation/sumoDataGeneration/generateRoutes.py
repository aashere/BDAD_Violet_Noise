from generateSumoPath import PathGenerator

generator = PathGenerator()
WEEKS = 1

config_schema = '''<configuration> 

<input> 
<net-file value="data/net/my_net.net.xml"/> 
<route-files value="%s"/> 
<additional-files value="data/net/bus_stops.xml"/>
</input> 
<time> 
<begin value="%s"/> 
<end value="%s"/> 
</time> 

</configuration> 
'''


if __name__ == "__main__":
    for w in range(0,WEEKS):
        for d in range(0,7):
            startseconds = (w * 604800) + (d * 86400)
            routefile = "week_%s_day_%s_route.rou.xml" % (w, d)
            filename = "data/routes/" + routefile
            generator.generate_traffic_route(filepath=filename, start_tm_seconds=startseconds, weekday=d)

            configname = "data/configs/week_%s_day_%s_config.sumocfg" % (w,d)
            configdata = config_schema % (filename, startseconds, startseconds + 86400)
            with open(configname, "w") as f:
                f.write(configdata)
            break