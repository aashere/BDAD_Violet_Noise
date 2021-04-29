# visualize feature importances, features: maxSpeed, averageSpeed, hour, minute, startNodeId, stopNodeId, turnsCount

from matplotlib import pyplot as plt
import matplotlib.ticker as plticker

plt.bar(x=['maxSpeed', 'averageSpeed', 'hour', 'minute', 'startNode', 'stopNode', 'turnsCount'],
        # the values of height come from the training result
        height=[], 
        color=['limegreen', 'dodgerblue', 'crimson', 'purple', 'pink', 'cyan', 'orange'])
ax = plt.gca()
ax.set(title='Feature Importance', xlabel='Feature', ylabel='Importance Percentage')
plt.ylim(0, 1)
ax.yaxis.set_major_locator(plticker.MultipleLocator(base=0.1)) # set ticks at the y-axis
ax.yaxis.set_minor_locator(plticker.MultipleLocator(base=0.02)) # set minor ticks at the y-axis

# annotate the bars displaying the value as percentage
for p in ax.patches:
    ax.annotate(f'\n{p.get_height()*100:.1f}%',
      (p.get_x()+p.get_width()/2, p.get_height()), ha='center', va='top', color='white', size=18)
plt.tight_layout()
plt.savefig("feature_importance_histogram.png")