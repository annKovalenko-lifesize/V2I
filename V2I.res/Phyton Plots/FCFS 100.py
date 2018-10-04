import numpy as np
import matplotlib.pyplot as plt

# data to plot
n_groups = 10

lb_std = (13.572114752336, 33.222966713426, 46.642206233184, 57.79746643614, 67.772743987434, 71.858385635964, 64.413927243947, 64.331219070731, 65.281433173923, 63.64849593260)

nl_std = (13.466390687965, 22.921053916513, 37.283952358197, 50.090768186077, 65.848354171736, 76.586882523789, 75.772109932865, 75.439402491509, 77.285119235027, 74.330390127805)


with_lb = (74.733333333333, 31.6, 54.433333333333, 71.933333333333, 89.6, 102.6, 111.53333333333, 130.33333333333, 157.7, 184.2)

no_lb = (78.966666666667, 	93.266666666667, 123.9, 149.46666666667, 177.83333333333, 205.63333333333, 223.96666666667, 256, 298.1, 337.6)

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
plt.title('Task dropping rates with 100 task oversubscription (FCFS)')

ax.set_xticks(index)

ax.set_xticklabels(('100', '200', '300', '400', '500', '600', '700', '800', '900', '1000'))


ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.00), shadow= True, ncol=2)



plt.tight_layout()
plt.show()


