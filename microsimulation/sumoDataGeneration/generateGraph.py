import networkx as nx
import math
import itertools
import numpy as np
from helpers import get_gps_coords
import pickle

cart_Xs = [0, 2, 4, 6, 7, 8, 9, 10]
name_Xs = dict(zip(cart_Xs, ["9", "8", "7", "6", "5", "Madison", "Park", "Lexington", "3"]))
cart_Ys = list(range(0, 29))
name_Ys = dict(zip(cart_Ys, [str(i + 30) for i in cart_Ys]))

DG = nx.DiGraph()
coords = list(itertools.product(cart_Xs, cart_Ys))
for nodeid, pair in enumerate(coords):
    intersection = name_Xs[pair[0]] + "_" + name_Ys[pair[1]]
    gps = get_gps_coords(*pair)
    DG.add_node(nodeid, cartesian=pair, name=intersection, latitude=gps[0], longitude=gps[1], nodetype="inner",
                color="#e1e3f0", volume_rate=dict())

nodes = {v: k for k, v in nx.get_node_attributes(DG, "cartesian").items()}

for pair in nodes:

    nextave = cart_Xs.index(pair[0]) + 1
    if nextave >= len(cart_Xs):
        right = (-1, -1)
    else:
        right = (cart_Xs[nextave], pair[1])
    above = (pair[0], pair[1] + 1)

    eastboundst = (pair[1] % 2) == 0

    if above in nodes:  # and pair[0] not in {1,6}:

        if pair[0] in [2, 6, 8, 10]:
            DG.add_edge(nodes[pair], nodes[above], direction='north', lanes=4, name=name_Xs[pair[0]] + "_ave")
        elif pair[0] == 9:
            DG.add_edge(nodes[pair], nodes[above], direction='north', lanes=2, name=name_Xs[pair[0]] + "_ave")
            DG.add_edge(nodes[above], nodes[pair], direction='south', lanes=2, name=name_Xs[pair[0]] + "_ave")
        else:
            DG.add_edge(nodes[above], nodes[pair], direction='south', lanes=4, name=name_Xs[pair[0]] + "_ave")

    if right in nodes:  # and pair[1] not in {1,6}:
        if pair[1] in [4, 12, 27]:
            DG.add_edge(nodes[pair], nodes[right], direction='east', lanes=2, name=name_Ys[pair[1]] + "_st")
            DG.add_edge(nodes[right], nodes[pair], direction='west', lanes=2, name=name_Ys[pair[1]] + "_st")
        if eastboundst:
            DG.add_edge(nodes[pair], nodes[right], direction='east', lanes=2, name=name_Ys[pair[1]] + "_st")
        else:
            DG.add_edge(nodes[right], nodes[pair], direction='west', lanes=2, name=name_Ys[pair[1]] + "_st")

for node in DG.nodes:
    border_west = DG.nodes[node]["cartesian"][0] == 0
    border_east = DG.nodes[node]["cartesian"][0] == 10

    border_north = DG.nodes[node]["cartesian"][1] == 0
    border_south = DG.nodes[node]["cartesian"][1] == 28

    border = (border_west or border_east or border_north or border_south)

    if DG.nodes[node]["cartesian"][0] == 0:  # west border
        if DG.nodes[node]["cartesian"][1] in [4, 12, 27]:
            DG.nodes[node]["nodetype"] = "source_sink"
            DG.nodes[node]["color"] = "yellow"
        elif len([i for i in DG.out_edges(node) if DG.edges[i]['direction'] == 'east']) > 0:
            DG.nodes[node]["nodetype"] = "source"
            DG.nodes[node]["color"] = "green"
        else:
            DG.nodes[node]["nodetype"] = "sink"
            DG.nodes[node]["color"] = "red"
    elif DG.nodes[node]["cartesian"][0] == 10:  # east border
        if DG.nodes[node]["cartesian"][1] in [4, 12, 27]:
            DG.nodes[node]["nodetype"] = "source_sink"
            DG.nodes[node]["color"] = "yellow"
        elif len([i for i in DG.out_edges(node) if DG.edges[i]['direction'] == 'west']) > 0:
            DG.nodes[node]["nodetype"] = "source"
            DG.nodes[node]["color"] = "green"
        else:
            DG.nodes[node]["nodetype"] = "sink"
            DG.nodes[node]["color"] = "red"
    elif DG.nodes[node]["cartesian"][1] == 28:  # north border
        if DG.nodes[node]["cartesian"][0] == 9:
            DG.nodes[node]["nodetype"] = "source_sink"
            DG.nodes[node]["color"] = "yellow"
        elif len([i for i in DG.out_edges(node) if DG.edges[i]['direction'] == 'south']) > 0:
            DG.nodes[node]["nodetype"] = "source"
            DG.nodes[node]["color"] = "green"
        else:
            DG.nodes[node]["nodetype"] = "sink"
            DG.nodes[node]["color"] = "red"
    elif DG.nodes[node]["cartesian"][1] == 0:  # south border
        if DG.nodes[node]["cartesian"][0] == 9:
            DG.nodes[node]["nodetype"] = "source_sink"
            DG.nodes[node]["color"] = "yellow"
        elif len([i for i in DG.out_edges(node) if DG.edges[i]['direction'] == 'north']) > 0:
            DG.nodes[node]["nodetype"] = "source"
            DG.nodes[node]["color"] = "green"
        else:
            DG.nodes[node]["nodetype"] = "sink"
            DG.nodes[node]["color"] = "red"


mins = [i*15 for i in list(range(96))]
rate = [int(i) for i in list(np.ones(96)*2)]

defaultrate = dict(zip(mins,rate))

for node in DG.nodes:
    if "source" in DG.nodes[node]["nodetype"]:
        DG.nodes[node]["volume_rate"] = defaultrate


with open("data/graph.p", 'wb') as f:
    pickle.dump(DG, f)