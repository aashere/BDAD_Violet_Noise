from generateSumoPath import PathGenerator

generator = PathGenerator()
WEEKS = 1

if __name__ == "__main__":
    for i in range(WEEKS):
        filename = "data/routes/week_%s_route.rou.xml" % i
        generator.generate_traffic_route(filename)