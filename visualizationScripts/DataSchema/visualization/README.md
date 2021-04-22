### Python

## To graph edge weights over time for all edges on the same graph:
--edge_weights
## To graph edge weights over time for all edges partitioned into separate graphs:
--edge_weights --partitions=partition_file_path
## To graph edge weights over time for certain edges on the same graph:
--edge_weights --edges=edges_file_path
## To graph edge weights over time for certain edges partitioned into separate graphs:
--edge_weights --edges=edges_file_path --partitions=partition_file_path

## To graph edge weights over time at the street and avenue level
--road_weights

## To graph max density histogram for all edges:
--max_density --edges