# visualize feature importance, features: maxSpeed, avgSpeed, startNode, stopNode, hour, minute, turnsCount

from matplotlib import pyplot as plt
import matplotlib.ticker as plticker

plt.bar(x=['maxSpeed', 'avgSpeed','startNode', 'stopNode', 'hour', 'minute', 'turnsCount'],
        # the values of height come from the training result
        height=[0.542049560202803,0.3171423293537713,8.019712746244066E-4,0.0018272824001011809,5.87631494154728E-5,0.0014839349625163666,0.1366361586567682], 
        color=['limegreen', 'dodgerblue', 'crimson', 'purple', 'pink', 'cyan', 'orange'])
ax = plt.gca()
ax.set(title='Feature Importances', xlabel='Feature', ylabel='Importance Percentage')
plt.ylim(0, 1)
ax.yaxis.set_major_locator(plticker.MultipleLocator(base=0.1)) # set ticks at the y-axis
ax.yaxis.set_minor_locator(plticker.MultipleLocator(base=0.02)) # set minor ticks at the y-axis

# annotate the bars displaying the value as percentage
for p in ax.patches:
    ax.annotate(f'\n{p.get_height()*100:.1f}%',
      (p.get_x()+p.get_width()/2, p.get_height()), ha='center', va='bottom', color='black', size=14)
plt.tight_layout()
plt.savefig("feature_importance_histogram.png")