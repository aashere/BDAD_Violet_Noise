import sys, heapq
# For random number generation
import random
# Set seed = 0
random.seed(0)
from edge import Edge

# For graph visualization (copied from Helene's simulation)
import networkx as nx
import matplotlib.pyplot as plt
import time

# Graph class:   defines the graph for the simulation, with
#                nodes representing intersections and
#                edges representing roads. 
#                Uses adjacency list representation.
class Graph:
    def __init__(self, CAPACITY_INTERVAL, DENSITY_INTERVAL):
        # Source and sink for simulation
        self.source = 1
        self.sink = 6

        # Inflow and outflow, to be used in simulation later
        self.inflow_val = 0
        self.outflow_val = 0
        # Interval of ticks on which to change capacity
        self.CAPACITY_INTERVAL = CAPACITY_INTERVAL
        # Interval of ticks on which to change density
        # self.DENSITY_INTERVAL = DENSITY_INTERVAL
        # Ratio of output flow to input flow
        self.FLOW_RATIO = 0.9
        
        # Adjacency list representation
        self.adj_list = {
            1: [Edge(2,False),Edge(3,False)],
            2: [Edge(1,True),Edge(4,False)],
            3: [Edge(1,True),Edge(4,False),Edge(5,False)],
            4: [Edge(2,True),Edge(3,True),Edge(6,False)],
            5: [Edge(3,True),Edge(6,False)],
            6: [Edge(4,True),Edge(5,True)]
        }

        # For graph visualization (copied from Helene's simulation)
        self.DG = nx.DiGraph()
        self.DG.add_node(1, pos=(0,0), color='green', name='source')
        self.DG.add_node(2, pos=(0,1), color='grey', name='pass')
        self.DG.add_node(3, pos=(1,0), color='grey', name='pass')
        self.DG.add_node(4, pos=(1,1), color='grey', name='pass')
        self.DG.add_node(5, pos=(2,0), color='grey', name='pass')
        self.DG.add_node(6, pos=(2,1), color='red', name='sink')

        self.DG.add_weighted_edges_from([(1, 2, 0), 
                                        (1, 3, 0),
                                        (2, 4, 0),
                                        (3, 4, 0),
                                        (3, 5, 0),
                                        (4, 6, 0),
                                        (5, 6, 0)])
        for (u,v) in self.DG.edges:
            x1 = self.DG.nodes[u]['pos'][0]
            y1 = self.DG.nodes[u]['pos'][1]
            
            x2 = self.DG.nodes[v]['pos'][0]
            y2 = self.DG.nodes[v]['pos'][1]
            
            if (x1 == x2):
                self.DG.edges[u,v]["orient"] = "vertical"
            else:
                self.DG.edges[u,v]["orient"] = "horizontal"

    # Performs a DFS on the graph, starting from source, to 
    # determine the set of available paths from source to sink. 
    # 
    # For inflow: We want all paths with all edges having residual > 0
    # For outflow: We want all paths with all edges having residual < capacity
    # For maxflow: We want all paths with all edges having residual_temp > 0
    # 
    # Parameters:
    #               node - current node of DFS
    #               flow_type - specifies if we are looking for paths for inflow, outflow, or maxflow
    #               visited - visited array for nodes
    #               cur_path - stores the current path of the DFS
    #               paths - stores all available paths for caller
    def discover_paths(self, node, flow_type, visited, cur_path, paths):
        if cur_path:
            # Grab edge between end of cur_path and current node
            cur_edge = None
            for edge in self.adj_list[cur_path[-1]]:
                if edge.dest_node == node:
                    cur_edge = edge
            
            # If this edge violates path constraint, abandon rest of path
            if ((flow_type == "inflow" and not cur_edge.residual > 0)
                or (flow_type == "outflow" and not cur_edge.residual < cur_edge.capacity)
                or (flow_type == "maxflow" and not cur_edge.residual_temp > 0)):
                return
        
        # Otherwise, mark the current node as visited and push it to the path
        visited[node-1] = True
        cur_path.append(node)

        # If we have reached the sink, store the path in paths
        if node == self.sink:
            new_path = []
            for n in cur_path:
                new_path.append(n)
            paths.append(new_path)
        # Otherwise, continue DFS for unvisited neighbors
        else:
            for edge in self.adj_list[node]:
                if visited[edge.dest_node-1] is False:
                    self.discover_paths(edge.dest_node, flow_type, visited, cur_path, paths)

        # If we have finished exploring the node, set it to unvisited and
        # remove it from cur_path so that we can use it again for future paths
        visited[node-1] = False
        cur_path.pop()
    
    # Simulates inflow coming into the graph and percolating across
    # the edges (but not leaving the graph). Takes parameter tick, which
    # is the current simulation time.
    def inflow(self, tick):
        for edges in self.adj_list.values():
            for edge in edges:
                if not edge.rev_edge:
                    # Generate new capacity (every CAPACITY_INTERVAL ticks) for all forward edges
                    if tick % self.CAPACITY_INTERVAL == 0:
                        # Capacity has to be >= flow
                        edge.capacity = random.randint(edge.flow, Edge.MAX_CAPACITY)
                # Update residual capacity of every edge, since we changed the flows (from last iteration) and capacities
                edge.residual = edge.capacity - edge.flow

        # Generate random inflow at source <= max flow of the graph
        self.inflow_val = random.randint(0, self.max_flow())
        # Temp variable for decrementing below
        inflow = self.inflow_val

        # Randomly distribute inflows among paths
        while inflow > 0:
            # Recompute residual capacities since flows have now changed
            for edges in self.adj_list.values():
                for edge in edges:
                    edge.residual = edge.capacity - edge.flow
            
            # Array storing all appropriate paths from source to sink
            inflow_paths = []
            # List storing the current path from source to sink
            cur_path = []
            # Visited array for nodes
            visited = [False]*len(self.adj_list.keys())
            # Generate all possible paths for inflow
            self.discover_paths(self.source, "inflow", visited, cur_path, inflow_paths)

            # Randomly choose path and remove it from inflow_paths
            random_path = inflow_paths.pop(random.randint(0, len(inflow_paths)-1))
            # Increment flows of all edges in random_path
            for i in range(0, len(random_path)-1):
                a = random_path[i]
                b = random_path[i+1]
                # Increment the flow of the edge
                cur_edge = None
                for edge in self.adj_list[a]:
                    if edge.dest_node == b:
                        cur_edge = edge
                cur_edge.flow+=1
                # Decrement the flow of its reverse edge
                cur_rev_edge = None
                for edge in self.adj_list[b]:
                    if edge.dest_node == a:
                        cur_rev_edge = edge
                cur_rev_edge.flow-=1
            # Decrement inflow
            inflow-=1            

    # Simulates outflow leaving the edges. Takes parameter tick, which is
    # the current simulation time.
    def outflow(self, tick):        
        for edges in self.adj_list.values():
            for edge in edges:
                # Update residual capacity of every edge, since we changed the flows
                edge.residual = edge.capacity - edge.flow
        
        # Generate random outflow at sink <= 0.5*inflow. We want inflow >> outflow to build up congestion in the graph.
        self.outflow_val = random.randint(0, int(self.FLOW_RATIO*self.inflow_val))
        # Temp variable for decrementing below
        outflow = self.outflow_val

        # Randomly remove outflows from paths
        while outflow > 0:
            # Recompute residual capacities since flows have now changed
            for edges in self.adj_list.values():
                for edge in edges:
                    edge.residual = edge.capacity - edge.flow

            # Array storing all appropriate paths from source to sink
            outflow_paths = []
            # List storing the current path from source to sink
            cur_path = []
            # Visited array for nodes
            visited = [False]*len(self.adj_list.keys())
            # Generate all possible paths for outflow
            self.discover_paths(self.source, "outflow", visited, cur_path, outflow_paths)
            
            # Randomly choose path and remove it from outflow_paths
            random_path = outflow_paths.pop(random.randint(0, len(outflow_paths)-1))
            # Decrement flows of all edges in random_path
            for i in range(0, len(random_path)-1):
                a = random_path[i]
                b = random_path[i+1]
                # Decrement the flow of the edge
                cur_edge = None
                for edge in self.adj_list[a]:
                    if edge.dest_node == b:
                        cur_edge = edge
                cur_edge.flow-=1
                # Increment the flow of its reverse edge
                cur_rev_edge = None
                for edge in self.adj_list[b]:
                    if edge.dest_node == a:
                        cur_rev_edge = edge
                cur_rev_edge.flow+=1
            # Decrement outflow
            outflow-=1            

        # Update the density, speed, and times of every forward edge
        '''
        for edges in self.adj_list.values():
            for edge in edges:
                if not edge.rev_edge:
                    # Generate new density for all forward edges. If there's no flow then we have a density
                    # of 0. If there is flow, pick a random density (**REVIEW)
                    if tick % self.DENSITY_INTERVAL == 0:
                        edge.density = 0 if edge.flow == 0 else random.randint(Edge.MIN_DENSITY, Edge.MAX_DENSITY)
                    if edge.residual == 0:
                        # If we're at capacity, there is a jam
                        edge.speed = 0
                    elif edge.density != 0:
                        # If there is flow, then use formula q=kv
                        edge.speed = min(float(edge.flow) / float(edge.density),edge.SPEED_LIMIT)
                    else:
                        # If there is no flow, speed is infinite (no cars on the road)
                        edge.speed = edge.SPEED_LIMIT
                    if edge.speed > 0:
                        edge.time = float(edge.length) / edge.speed
                    else:
                        # For speed of 0, set time to infinity for use by Dijkstra
                        edge.time = sys.maxsize
        '''
    
    # Return the max flow through the graph
    def max_flow(self):
        # To store max flow
        max_flow = 0
        # Set the flow_temp of all edges
        for edges in self.adj_list.values():
            for edge in edges:
                edge.flow_temp = edge.flow
                edge.residual_temp = edge.capacity - edge.flow_temp

        while(True):
            # Calculate residual_temp of all edges
            for edges in self.adj_list.values():
                for edge in edges:
                    edge.residual_temp = edge.capacity - edge.flow_temp

            # Array to store all augmenting paths
            aug_paths = []
            # List storing the current path from source to sink
            cur_path = []
            # Visited array for nodes
            visited = [False]*len(self.adj_list.keys())
            # Generate all augmenting paths
            self.discover_paths(self.source, "maxflow", visited, cur_path, aug_paths)
            # Continue until there are no more augmenting paths
            if not aug_paths:
                break
            # Choose a path
            path = aug_paths[0]
            # Find bottleneck capacity
            bottleneck = sys.maxsize
            for i in range(0, len(path)-1):
                a = path[i]
                b = path[i+1]
                for edge in self.adj_list[a]:
                    if edge.dest_node == b:
                        if edge.residual_temp < bottleneck:
                            bottleneck = edge.residual_temp
            # Send bottleneck flow through this path
            for i in range(0, len(path)-1):
                a = path[i]
                b = path[i+1]
                # Increase the flow of the edge
                cur_edge = None
                for edge in self.adj_list[a]:
                    if edge.dest_node == b:
                        cur_edge = edge
                cur_edge.flow_temp+=bottleneck
                # Decrease the flow of its reverse edge
                cur_rev_edge = None
                for edge in self.adj_list[b]:
                    if edge.dest_node == a:
                        cur_rev_edge = edge
                cur_rev_edge.flow_temp-=bottleneck
            # Increase max flow
            max_flow+=bottleneck
        
        return max_flow  

    '''    
    # Return string representation of shortest path from source to sink using Dijkstra's algorithm
    def shortest_path(self):
        # Min Heap to store (distance, node)
        H = []
        # Distances
        dist = dict()
        # Parents of shortest path
        parent = dict()

        for node in self.adj_list.keys():
            # Initialize source's distance to 0
            if node == self.source:
                heapq.heappush(H, (0, node))
                dist[node] = 0
                parent[node] = 0
            else:
                # Initialize all other distances to infinity
                heapq.heappush(H, (sys.maxsize, node))
                dist[node] = sys.maxsize
                parent[node] = -1

        while(H):
            # Pop min
            v = heapq.heappop(H)[1]
            for edge in self.adj_list[v]:
                # Only consider forward edges for shortest path
                if not edge.rev_edge and dist[edge.dest_node] > dist[v] + edge.time:
                    # Update distance, parent, and heap if smaller distance found
                    dist[edge.dest_node] = dist[v] + edge.time
                    parent[edge.dest_node] = v
                    for r in H:
                        if r[1] == edge.dest_node:
                            H.remove(r)
                    heapq.heappush(H, (dist[edge.dest_node], edge.dest_node))
        
        print(parent)
        v = self.sink
        output = "->"+str(v)
        while parent[v] != self.source:
            output = "->"+str(parent[v])+output
            v = parent[v]
        output = str(dist[self.sink])+ ": " + str(self.source)+output
        return output
    '''

    # String representation of the adjacency list
    def __str__(self):
        output = ""
        for node in self.adj_list.keys():
            output+=str(node) + ": "
            for edge in self.adj_list[node]:
                output+=str(edge)+","
            output+="\n"
        return output
    
    #Graph printing with matplotlib
    def print_graph(self):
        # Populate weights (times) for DG
        # times = dict()
        # (Instead of times, populate residual capacities as the weights for DG)
        residuals = dict()
        for a in self.adj_list.keys():
            for b in self.adj_list[a]:
                if not b.rev_edge:
                    # times[(a,b.dest_node)] = int(b.time)
                    b.residual = b.capacity - b.flow
                    residuals[(a,b.dest_node)] = b.residual
        # nx.set_edge_attributes(self.DG, times, name='weight')
        nx.set_edge_attributes(self.DG, residuals, name='weight')

        # For graph visualization (copied from Helene's simulation)
        colors = nx.get_edge_attributes(self.DG,'weight').values()
        pos = nx.get_node_attributes(self.DG,'pos')
        nodecol = nx.get_node_attributes(self.DG, 'color').values()
        nx.draw(self.DG, pos, 
                edge_color=colors,
                width=5.0,
                with_labels=True,
                node_color=nodecol)
        nx.draw_networkx_edge_labels(self.DG,pos,edge_labels=nx.get_edge_attributes(self.DG,"weight"),font_color='red')
        
        plt.pause(0.05)

    # TODO: Extension: Create randomized graph and check that there's a path from source to every node
    #       and from every node to sink. Then add the reverse edges.