import os
import sys
from typing import List
import GraphTest

if "SUMO_HOME" in os.environ:
    tools = os.path.join(os.environ["SUMO_HOME"], "tools")
    sys.path.append(tools)
else:
    sys.exit("please declare environment variable 'SUMO_HOME'")


class Node:
    def __init__(self, n_id, loc_x, loc_y, n_type=''):
        self.n_id = n_id
        self.loc_x = loc_x
        self.lox_y = loc_y
        self.n_type = n_type


class Nodes:

    def __init__(self):
        self.nodes: List[Node] = []

    def add_node(self, n_id, loc_x, loc_y, n_type=''):
        self.nodes.append(Node(n_id, loc_x, loc_y, n_type))

    def to_xml(self, file_name):
        with open(file_name, 'a') as f:
            f.write('<nodes>\n')
            for node in self.nodes:
                if node.n_type:
                    f.write('<node id="{}" x = "{}" y="{}" type="{}"/>\n'.format(node.n_id, node.loc_x, node.lox_y,
                                                                                 node.n_type))
                else:
                    f.write('<node id="{}" x = "{}" y="{}"/>\n'.format(node.n_id, node.loc_x, node.lox_y))
            f.write('</nodes>\n')


class Edge:
    pass


class Type:
    pass


class Net:
    pass


class Route:
    pass


def generate_graph():
    # generate node
    nodes = Nodes()
    nodes.add_node("n1", "0", "0", "priority")
    nodes.add_node("n2", "0", "300")
    nodes.add_node("n3", "250", "0", "traffic_light")
    nodes.add_node("n4", "250", "300", "traffic_light")
    nodes.add_node("n5", "500", "0")
    nodes.add_node("n6", "500", "300", "traffic_light")
    nodes.to_xml('my_nodes.nod.xml')
    # generate edge
    # generae type
    # netconvert --node-files my_nodes.nod.xml --edge-files my_edge.edg.xml -t my_type.type.xml -o my_net.net.xml
    pass


class Config:
    '''
    define start and end time and generate my_config_file.sumocfg
    for now i just directly write a file
    '''
    pass


def generate_trace():
    # sumo -c my_config_file.sumocfg --fcd-output sumoTrace.xml
    pass


import subprocess

if __name__ == "__main__":
    pass
    # 1. run graph test, or local generate_graph()
    # 2. run sumo commend in cmd
    #    or subprocess.Popen(['netconvert', '--node-files', 'my_nodes.nod.xml', '--edge-files', 'my_edge.edg.xml', '-t',
    #                   'my_type.type.xml', '-o', 'my_net.net.xml'])
    # 3. generate_trace()

'''
TODO:
1. separate class and put data in config file
'''
