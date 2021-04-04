import networkx as nx
import json
import pickle

with open("data/graph.p", "rb") as f:
    DG = pickle.load(f)


# Dict of all paths from every source to every sink.
# This gives all the routes. It is a dict that maps
# (source, sink): [list of routes in ascending order of size]
paths = dict()
#Min path size - we can adjust this
MIN_PATH_SIZE = 5

def filter_edge(edge, node, sink):
    if DG.nodes[sink]['cartesian'][0]-DG.nodes[node]['cartesian'][0] < 0 and DG.edges[edge]['direction'] == 'west':
        return True
    if DG.nodes[sink]['cartesian'][0]-DG.nodes[node]['cartesian'][0] > 0 and DG.edges[edge]['direction'] == 'east':
        return True
    if DG.nodes[sink]['cartesian'][1]-DG.nodes[node]['cartesian'][1] < 0 and DG.edges[edge]['direction'] == 'south':
        return True
    if DG.nodes[sink]['cartesian'][1]-DG.nodes[node]['cartesian'][1] > 0 and DG.edges[edge]['direction'] == 'north':
        return True
    if (DG.nodes[sink]['cartesian'][0]-DG.nodes[node]['cartesian'][0] == 0 and 
        DG.nodes[sink]['cartesian'][1]-DG.nodes[node]['cartesian'][1] < 0 and
        DG.edges[edge]['direction'] == 'south'):
        return True
    if (DG.nodes[sink]['cartesian'][0]-DG.nodes[node]['cartesian'][0] == 0 and 
        DG.nodes[sink]['cartesian'][1]-DG.nodes[node]['cartesian'][1] > 0 and
        DG.edges[edge]['direction'] == 'north'):
        return True
    if (DG.nodes[sink]['cartesian'][1]-DG.nodes[node]['cartesian'][1] == 0 and 
        DG.nodes[sink]['cartesian'][0]-DG.nodes[node]['cartesian'][0] < 0 and
        DG.edges[edge]['direction'] == 'west'):
        return True
    if (DG.nodes[sink]['cartesian'][1]-DG.nodes[node]['cartesian'][1] == 0 and 
        DG.nodes[sink]['cartesian'][0]-DG.nodes[node]['cartesian'][0] > 0 and
        DG.edges[edge]['direction'] == 'east'):
        return True
    return False                                           

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
                if filter_edge(edge, node, sink):
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
    for node in DG.nodes:
        if DG.nodes[node]['nodetype'] == "source" or DG.nodes[node]['nodetype'] == "source_sink":
            sources.append(node)
        if DG.nodes[node]['nodetype'] == "sink" or DG.nodes[node]['nodetype'] == "source_sink":
            sinks.append(node)
    
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
                for node in DG.nodes:
                    visited[node] = False
                # Generate all possible paths
                discover_paths(s, t, pair, visited, cur_path, paths)
                paths[pair].sort(key=lambda x : len(x))

                # add metadata to paths collected for each pair
                records = []
                for p in paths[pair]:
                    dirs = [DG.edges[tuple(p[i:i+2])]["direction"] for i in range(len(p)-1)]
                    turns = sum([int(dirs[i] != dirs[i+1]) for i in range(len(dirs)-1)])
                    length = len(p) - 1
                    records.append({"turns": turns, "length": length, "path": p})
                paths[pair] = records
                

    with open("data/pathdict.json", 'w') as outf:
        jsonformat = json.dumps({"%s|%s" % k:v for k, v in paths.items()})
        outf.write(jsonformat)
