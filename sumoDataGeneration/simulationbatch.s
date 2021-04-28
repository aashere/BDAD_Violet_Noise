#!/bin/bash
#
#SBATCH --nodes=1
#SBATCH --cpus-per-task=4
#SBATCH --time=12:00:00
#SBATCH --mem=32GB
#SBATCH --job-name=bdadsumosim
#SBATCH --mail-type=END
#SBATCH --mail-user=hls327@nyu.edu

cd /scratch/hls327/
srun runscripts/simulate.sh