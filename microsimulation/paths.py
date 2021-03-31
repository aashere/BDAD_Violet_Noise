import networkx as nx
import matplotlib.pyplot as plt
import pprint
from GraphTest import DG
import time

# Dict of all paths from every source to every sink.
# This gives all the routes. It is a dict that maps
# (source, sink): [list of routes in ascending order of size]
paths = dict()
#Min path size - we can adjust this
MIN_PATH_SIZE = 5

'''
# Test graph
DG = nx.DiGraph()
DG.add_node(1, cartesian=(0,0), nodetype='source', color='green')
DG.add_node(2, cartesian=(1,0), nodetype='source', color='green')
DG.add_node(3, cartesian=(2,0), nodetype='sink', color='red')
DG.add_node(4, cartesian=(0,1), nodetype='source_sink', color='yellow')
DG.add_node(5, cartesian=(1,1), nodetype='inner', color='gray')
DG.add_node(6, cartesian=(2,1), nodetype='source', color='green')
DG.add_node(7, cartesian=(0,2), nodetype='source', color='green')
DG.add_node(8, cartesian=(1,2), nodetype='sink', color='red')
DG.add_node(9, cartesian=(2,2), nodetype='source', color='green')

DG.add_edge(1, 2, direction='east')
DG.add_edge(2, 3, direction='east')
DG.add_edge(4, 1, direction='south')
DG.add_edge(2, 5, direction='north')
DG.add_edge(6, 3, direction='south')
DG.add_edge(6, 5, direction='west')
DG.add_edge(5, 4, direction='west')
DG.add_edge(7, 4, direction='south')
DG.add_edge(5, 8, direction='north')
DG.add_edge(9, 6, direction='south')
DG.add_edge(7, 8, direction='east')
DG.add_edge(8, 9, direction='east')

def show_graph():
    pos = nx.get_node_attributes(DG,'cartesian')
    nodecol = nx.get_node_attributes(DG, 'color').values()
    node_labels = dict()
    for node in DG.nodes:
        node_labels[node] = node

    plt.figure(figsize=(15,40)) 
    nx.draw(DG, 
            pos, 
            width=2,
            node_color=nodecol,
            font_color='blue',
            node_size=500,
            labels=node_labels)

    plt.show()
'''

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
                if ((DG.nodes[sink]['cartesian'][0]-DG.nodes[node]['cartesian'][0] < 0 and DG.edges[edge]['direction'] == 'west') or
                    (DG.nodes[sink]['cartesian'][0]-DG.nodes[node]['cartesian'][0] > 0 and DG.edges[edge]['direction'] == 'east') or
                    (DG.nodes[sink]['cartesian'][0]-DG.nodes[node]['cartesian'][0] == 0 and DG.edges[edge]['direction'] != 'east'
                                                                                        and DG.edges[edge]['direction'] != 'west') or
                    (DG.nodes[sink]['cartesian'][1]-DG.nodes[node]['cartesian'][1] < 0 and DG.edges[edge]['direction'] == 'south') or
                    (DG.nodes[sink]['cartesian'][1]-DG.nodes[node]['cartesian'][1] > 0 and DG.edges[edge]['direction'] == 'north') or
                    (DG.nodes[sink]['cartesian'][1]-DG.nodes[node]['cartesian'][1] == 0 and DG.edges[edge]['direction'] != 'south'
                                                                                        and DG.edges[edge]['direction'] != 'north')):
                    if visited[nbh] is False:
                        discover_paths(nbh, sink, pair, visited, cur_path, paths)

        # If we have finished exploring the node, set it to unvisited and
        # remove it from cur_path so that we can use it again for future paths
        visited[node] = False
        cur_path.pop()

if __name__ == "__main__":
    #show_graph()
    
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
                print(pair)
    print("Done!")
    #print(paths)