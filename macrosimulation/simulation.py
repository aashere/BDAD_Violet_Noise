from graph import Graph

# Number of iterations of simulation
NUM_TICKS = 50

# Instantiate new graph with desired capacity interval and density interval
simul_graph = Graph(2,1)

# Run simulation
for tick in range(0, NUM_TICKS):
    print("TICK: " + str(tick))
    # Simulate an inflow
    simul_graph.inflow(tick)
    # Simulate an outflow
    simul_graph.outflow(tick)
    print(simul_graph)
    # Calculate shortest path for this iteration
    # shortest_path = simul_graph.shortest_path()
    # print(shortest_path)
    # Print graph
    simul_graph.print_graph()