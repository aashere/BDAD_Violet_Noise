Macrosimulation code

Simulates traffic on a macro level, as flows passing through roads. Nodes represent intersections/destinations, and edges represent roads. The simulation randomly generates a new capacity for each edge every CAPACITY\_INTERVAL ticks, and then simulates flows entering and leaving the graph. The inflow > outflow, so that there is pressure built up in the graph.

To run: python3 simulation.py
