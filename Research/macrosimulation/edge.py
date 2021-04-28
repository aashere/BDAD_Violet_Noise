# For random number generation
import sys, random
# Set seed = 0
random.seed(0)

# Edge class:   defines an edge, with certain attributes.
#               Note that a forward edge is an edge that was
#               present in our original graph, whereas a 
#               reverse edge is an edge that was added to our
#               original graph so that the algorithm can work
#               properly. The graph that we will ultimately
#               print as part of the simulation will NOT contain
#               these reverse edges - they are only for the purposes
#               of the algorithm.
class Edge:
    # Maximum capacity a forward edge can attain
    MAX_CAPACITY = 30
    # Density bounds
    # MIN_DENSITY = 10
    # MAX_DENSITY = 200
    # Max speed on an edge
    # SPEED_LIMIT = 60

    def __init__(self, dest_node, rev_edge):
        # Destination node of this edge
        self.dest_node = dest_node
        # Flow through this edge
        self.flow = 0
        # Is this is a reverse edge?
        self.rev_edge = rev_edge
        # Capacity of this edge; reverse edges always have capacity 0
        self.capacity = 0 if rev_edge else random.randint(0, self.MAX_CAPACITY)
        # Residual capacity of this edge
        self.residual = self.capacity - self.flow

        # Only forward edges actually have these properties
        # Length of this edge (in miles)
        # self.length = random.randint(1, 10)
        # Average density on this edge (in vehicles per mile)
        # self.density = 0
        # Average speed on this edge (in miles per tick)
        # self.speed = self.SPEED_LIMIT
        # Average time a car spends on this edge (in ticks)
        # self.time = 0

        # For calculating max flow
        self.flow_temp = 0
        self.residual_temp = self.capacity
    
    # String representation of edge
    def __str__(self):
        # return "(" + str(self.dest_node) + "," + str(self.flow) + "/" + str(self.capacity) + "," + str(self.residual) + "," + \
        #         str(self.length) + "," + str(self.density) + "," + str(self.speed) + "," + str(self.time) + ")"
        return "(" + str(self.dest_node) + "," + str(self.flow) + "/" + str(self.capacity) + "," + str(self.residual) + ")"