# Visualization Generation
The code to generate visualizations is contained in ```visualize.py```. 
## Types of Visualizations
The following visualizations are supported:
1. 
## visualize.py Options
The following parameters can be passed to the ```visualize.py``` script:
1. 
## How to Run
WARNING: If you have a folder called "visualizations" in the current working directory, make sure to move it somewhere else before running ```visualize.sh```; otherwise it will be overwritten. In order to generate all the visualizations, follow the steps below.
1. ```scp``` this directory into your Peel home or scratch directory, and ```cd``` into the directory.
2. Run ```chmod +x visualize.sh```.
3. Comment out lines in ```visualize.sh``` for the visualizations you do not want and/or edit/add more lines with calls to ```visualize.py``` that include the parameters you do want.
4. Run ```visualize.sh```.