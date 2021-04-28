import numpy as np

# Range of X and Y coordinates cartesian
xC1 = (0,0)
xC2 = (0,28)
xC3 = (10,0)
xC4 = (10,28)

# Range of Longitude and Latitude 
xLL1 = (40.750885, -73.998169)
xLL2 = (40.768575, -73.985212)
xLL3 = (40.743195, -73.979931)
xLL4 = (40.760918, -73.966995)

# matrix calculations to calculate the transformation
# see https://math.stackexchange.com/questions/296794/finding-the-transform-matrix-from-4-projected-points-with-javascript/339033#339033

m_AC = np.mat([[xC1[0], xC2[0], xC3[0]], [xC1[1], xC2[1], xC3[1]], [1, 1, 1]])
m_BC = np.array(list(xC4) + [1]).reshape(-1,1)
lambda_C = np.matmul(np.linalg.inv(m_AC), m_BC)
lambda_C_ext = np.repeat(np.transpose(lambda_C), repeats=3,axis=0)
transform_C =  np.multiply(lambda_C_ext, m_AC)

m_ALL = np.mat([[xLL1[0], xLL2[0], xLL3[0]], [xLL1[1], xLL2[1], xLL3[1]], [1, 1, 1]])
m_BLL = np.array(list(xLL4) + [1]).reshape(-1,1)
lambda_LL = np.matmul(np.linalg.inv(m_ALL), m_BLL)
lambda_LL_ext = np.repeat(np.transpose(lambda_LL), repeats=3,axis=0)
transform_LL =  np.multiply(lambda_LL_ext, m_ALL)

# the final transformation matrix
fullmap = transform_LL * np.linalg.inv(transform_C)

def get_gps_coords(x, y):
    m_coords = np.transpose(np.mat([x, y, 1]))
    m_output = np.matmul(fullmap, m_coords)
    lat = np.around(m_output[0,0] / m_output[2,0], decimals=6)
    long = np.around(m_output[1,0] / m_output[2,0], decimals=6)
    return (lat, long)