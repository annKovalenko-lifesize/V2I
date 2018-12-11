import numpy as np
import matplotlib.pyplot as plt

# data to plot
n_groups = 6

#lb_std = (18.263839029403, 25.473989438858, 38.856056200395, 49.185842743993, 60.410368676893, 64.153013922982, 60.432397872695, 60.493221810856, 	62.266021803744, 62.834173292352)
#nl_std = (12.40948839795, 25.919880977448, 41.795671557226, 57.098513377189, 73.319494514502, 81.929209701418, 78.372643596907, 	79.247147234633, 80.906537536172, 76.333968275229)


no_lb = (0.104420, 0.146220, 0.279018, 0.544415, 2.353228, 2.749791)
with_lb = (0.133200, 0.095675, 0.051584, 0.143906, 0.064208, 0.159845)


# create plot
fig, ax = plt.subplots()
index = np.arange(n_groups)
bar_width = 0.3
opacity = 0.8


axes = plt.gca()
axes.set_ylim([0,5])

axes.set_xlim([-0.8, 6])



rects1 = plt.bar(index - bar_width, no_lb, bar_width,
                 alpha=opacity,
                 color='gray',
                 error_kw=dict(ecolor ='black'),
                 label='Without LB')

rects2 = plt.bar(index, with_lb, bar_width,
                 alpha=opacity,
                 color='cyan',
                 error_kw=dict(ecolor ='black'),
                 label='With LB')

plt.xlabel('Number of tasks submitted')
plt.ylabel('% of tasks missed its deadline')
plt.title('LB performance compared to No LB')

ax.set_xticks(index)

ax.set_xticklabels(('2900', '6900', '9000', '11000', '16500', '18000'))


ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.00), shadow=True, ncol=2)



plt.tight_layout()
plt.show()

