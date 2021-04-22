Macrosimulation code

Simulates traffic on a macro level, as flows passing through roads. Nodes represent intersections/destinations, and edges represent roads. The simulation randomly generates a new capacity for each edge every CAPACITY\_INTERVAL ticks, and then simulates flows entering and leaving the graph. The inflow > outflow, so that there is pressure built up in the graph.

The visualization code is the same as for Helene's simulation, for consistency. For the edge weights, I have used the residual capacities for now, so each edge displays how much more flow is available on that edge at the end of that iteration (after the iteration's inflow and outflow have both occurred).

To run: python3 simulation.py
