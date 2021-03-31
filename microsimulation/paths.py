import networkx as nx
import matplotlib.pyplot as plt
from GraphTest import DG
import time

# Dict of all paths from every source to every sink.
# This gives all the routes. It is a dict that maps
# (source, sink): [list of routes]
paths = dict()
#Min path size - we can adjust this
MIN_PATH_SIZE = 5

def load_source_sink(sources, sinks):
    for node in DG.nodes:
        if DG.nodes[node]['nodetype'] == "source" or DG.nodes[node]['nodetype'] == "source_sink":
            sources.append(node)
        if DG.nodes[node]['nodetype'] == "sink" or DG.nodes[node]['nodetype'] == "source_sink":
            sinks.append(node)

def discover_paths(node, sink, pair, visited, cur_path, paths):        
        # Mark the current node as visited and push it to the path
        visited[node] = True
        cur_path.append(node)

        # If we have reached the sink, store the path in paths
        if node == sink:
            new_path = []
            for n in cur_path:
                new_path.append(n)
            if len(new_path) >= MIN_PATH_SIZE:
                paths[pair].append(new_path)
        # Otherwise, continue DFS for unvisited neighbors
        else:
            for edge in DG.out_edges(node):
                nbh = edge[1]
                if (DG.nodes[nbh]['cartesian'][0] <= max(DG.nodes[node]['cartesian'][0], DG.nodes[sink]['cartesian'][0]) and
                    DG.nodes[nbh]['cartesian'][0] >= min(DG.nodes[node]['cartesian'][0], DG.nodes[sink]['cartesian'][0]) and
                    DG.nodes[nbh]['cartesian'][1] <= max(DG.nodes[node]['cartesian'][1], DG.nodes[sink]['cartesian'][1]) and
                    DG.nodes[nbh]['cartesian'][1] >= min(DG.nodes[node]['cartesian'][1], DG.nodes[sink]['cartesian'][1])):
                    if visited[nbh] is False:
                        discover_paths(nbh, sink, pair, visited, cur_path, paths)

        # If we have finished exploring the node, set it to unvisited and
        # remove it from cur_path so that we can use it again for future paths
        visited[node] = False
        cur_path.pop()

if __name__ == "__main__":
    # List of all sources in graph
    sources = []
    # List of all sinks in graph
    sinks = []
    # Populate lists
    load_source_sink(sources, sinks)
    
    # For every 2 distinct source and sink, load all paths from source to sink
    for s in sources:
        for t in sinks:
            if s!=t:
                pair = (s,t)
                paths[pair] = []
                # List storing the current path from source to sink
                cur_path = []
                # Visited dict for nodes
                visited = dict()
                sub_nodes = []
                for node in DG.nodes:
                    if (DG.nodes[node]['cartesian'][0] <= max(DG.nodes[s]['cartesian'][0], DG.nodes[t]['cartesian'][0]) and
                        DG.nodes[node]['cartesian'][0] >= min(DG.nodes[s]['cartesian'][0], DG.nodes[t]['cartesian'][0]) and
                        DG.nodes[node]['cartesian'][1] <= max(DG.nodes[s]['cartesian'][1], DG.nodes[t]['cartesian'][1]) and
                        DG.nodes[node]['cartesian'][1] >= min(DG.nodes[s]['cartesian'][1], DG.nodes[t]['cartesian'][1])):
                        sub_nodes.append(node)
                for node in sub_nodes:
                    visited[node] = False
                # Generate all possible paths
                discover_paths(s, t, pair, visited, cur_path, paths)

