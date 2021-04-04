import pickle


with open("data/graph.p", "rb") as f:
    DG = pickle.load(f)

with open('data/my_nodes.nod.xml', 'w') as f:
    f.write('<nodes>\n')
    for i, n in enumerate(DG.nodes):
        f.write('<node id="{}" x = "{}" y="{}"/>\n'.format(DG.nodes[n]['name'], DG.nodes[n]['cartesian'][0] * 100,
                                                           DG.nodes[n]['cartesian'][1] * 100))
        # f.write('<node id="{}" x = "{}" y="{}"/>\n'.format(DG.nodes[n]['name'], DG.nodes[n]['longitude'],
        #                                                    DG.nodes[n]['latitude']))
    f.write('</nodes>\n')

with open('data/my_edge.edg.xml', 'w') as f:
    f.write('<edges>\n')
    for i, n in enumerate(DG.edges):
        f.write(
            '<edge from="{}" to="{}" id="{}" type="{}L25"/>\n'.format(DG.nodes[n[0]]['name'], DG.nodes[n[1]]['name'],
                                                                      DG.nodes[n[0]]['name'] + 'to' + DG.nodes[n[1]][
                                                                          'name'],
                                                                      DG.edges[n]['lanes']))
    f.write('</edges>\n')