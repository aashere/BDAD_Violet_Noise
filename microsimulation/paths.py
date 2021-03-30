import networkx as nx
import matplotlib.pyplot as plt
from GraphTest import DG
import time

# Dict of all paths from every source to every sink
paths = dict()
MIN_PATH_SIZE = 5
MAX_PATH_SIZE = 15

def load_source_sink(sources, sinks):
    for node in DG.nodes:
        if DG.nodes[node]['nodetype'] == "source" or DG.nodes[node]['nodetype'] == "source_sink":
            sources.append(node)
        if DG.nodes[node]['nodetype'] == "sink" or DG.nodes[node]['nodetype'] == "source_sink":
            sinks.append(node)

def discover_paths(node, sink, pair, sub_edges, visited, cur_path, paths):        
        if cur_path:
            if len(cur_path) > MAX_PATH_SIZE:
                return
        
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
                #print(time.time())
        # Otherwise, continue DFS for unvisited neighbors
        else:
            for edge in DG.out_edges(node):
                if edge in sub_edges and visited[edge[1]] is False:
                    discover_paths(edge[1], sink, pair, sub_edges, visited, cur_path, paths)

        # If we have finished exploring the node, set it to unvisited and
        # remove it from cur_path so that we can use it again for future paths
        visited[node] = False
        cur_path.pop()


if __name__ == "__main__":
    start_time = time.time()
    count = 0
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
                start_iter_time = time.time()
                sub_nodes = []
                for node in DG.nodes:
                    if (DG.nodes[node]['cartesian'][0] <= max(DG.nodes[s]['cartesian'][0], DG.nodes[t]['cartesian'][0]) and
                        DG.nodes[node]['cartesian'][0] >= min(DG.nodes[s]['cartesian'][0], DG.nodes[t]['cartesian'][0]) and
                        DG.nodes[node]['cartesian'][1] <= max(DG.nodes[s]['cartesian'][1], DG.nodes[t]['cartesian'][1]) and
                        DG.nodes[node]['cartesian'][1] >= min(DG.nodes[s]['cartesian'][1], DG.nodes[t]['cartesian'][1])):
                        sub_nodes.append(node)
                
                sub_edges = []
                for edge in DG.edges:
                    if edge[0] in sub_nodes and edge[1] in sub_nodes:
                        sub_edges.append(edge)

                pair = (s,t)
                paths[pair] = []
                # List storing the current path from source to sink
                cur_path = []
                # Visited dict for nodes
                visited = dict()
                for node in sub_nodes:
                    visited[node] = False
                # Generate all possible paths
                discover_paths(s, t, pair, sub_edges, visited, cur_path, paths)
                
                end_iter_time = time.time()
                print(str(pair) + " ITERATION " + str(count) + " TOOK " + str(end_iter_time-start_iter_time) + " SECONDS")
                count+=1
    print(paths)
    end_time = time.time()
    print("TOTAL TIME: " + str(end_time-start_time))

