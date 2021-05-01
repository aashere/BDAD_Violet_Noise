# Scala Graph Code 

## Code to access Graph object and dataframes for node, edge, gps tables
### To run:

`spark-shell --deploy-mode=client`

### In spark shell:

`:load loadGraph.sc`

## Code to run shortest paths
### Assumption:

Input to this code is a csv file with timestamp, edge_id, and density columns.

### To run:

`spark-shell --deploy-mode=client`

### In spark shell:

`:load shortestPath.sc`
