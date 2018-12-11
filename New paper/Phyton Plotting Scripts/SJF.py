import numpy as np
import matplotlib.pyplot as plt

# data to plot
n_groups = 10

lb_std = (15.942461195359, 27.269388336189, 40.418912723466, 50.296774421839, 60.512001279503, 64.678773010632, 63.262825240984, 63.359075228361, 63.594476735518, 	60.580913879181)

nl_std = (13.576052276059, 33.740882122979, 45.282256553982, 60.022256408501, 73.685133171996, 82.446255569742, 77.278240383575, 63.594476735518, 	75.056270078928, 67.865663452604)


with_lb = (24.9, 34.96666666666667, 59.03333333333333, 76.6, 96.23333333333333, 109.36666666666666, 120.23333333333333, 139.8, 168.13333333333333, 197.43333333333334)

no_lb = (38.36666666666667, 57.36666666666667,	92.0, 120.53333333333333, 149.53333333333333, 176.83333333333334, 194.36666666666667, 230.26666666666668, 272.93333333333334, 311.9)

# create plot
fig, ax = plt.subplots()
index = np.arange(n_groups)
bar_width = 0.3
opacity = 0.8


axes = plt.gca()
axes.set_ylim([0,500])

axes.set_xlim([-0.8, 10])



rects1 = plt.bar(index - bar_width, no_lb, bar_width, yerr = nl_std,
                 alpha=opacity,
                 color='gray',
                 error_kw=dict(ecolor ='black'),
                 label='Without LB')

rects2 = plt.bar(index, with_lb, bar_width, yerr = lb_std,
                 alpha=opacity,
                 color='cyan',
                 error_kw=dict(ecolor ='black'),
                 label='With LB')

plt.xlabel('Number of tasks submitted')
plt.ylabel('Number of tasks missed its deadline')
plt.title('Task dropping rates with 100 task oversubscription (SJF)')

ax.set_xticks(index)

ax.set_xticklabels(('100', '200', '300', '400', '500', '600', '700', '800', '900', '1000'))


ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.00), shadow= True, ncol=2)



plt.tight_layout()
plt.show()


