#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Sep  1 15:22:02 2018

@author: c00303945
"""
import numpy as np
import matplotlib.pyplot as plt
from scipy import stats

# data to plot
n_groups = 4

baseLine = [3.922513*3, 2.549842*3, 5.057443*3, 10.688259*3]

loadbalancer = [0.652458*3, 1.126205*3, 1.068658*3, 2.942468*3] 

# create plot
fig, ax = plt.subplots()
index = np.arange(n_groups)
bar_width = 0.25
opacity = 1.0


# ontime = 
#def average(ontimes,i):
#    n, min_max, mean, var, skew, kurt = stats.describe(ontimes)
#    std=math.sqrt(var)
#    R = stats.norm.interval(0.05,loc=mean,scale=std)
#    diff=mean-R[0]
#    meanpercent=mean/int (i) * 100
#    ontime_list.append(meanpercent)
#    ci_list.append(diff/int(i)*100)
#    return ontime_list,ci_list 

#for i in range(10):
#    with_lb [i] = with_lb [i] / int(i)*100
#    no_lb [i] = no_lb [i] / int(i)*100
#    MR_With_Task_Dropping [i] = MR_With_Task_Dropping [i] / int(i)*100


#####################
#def CI(mean,std):
#    R = stats.norm.interval(0.95,loc=mean,scale=std/math.sqrt(30))
#    diff=mean-R[0]
#    return diff 
######################

#ci_certainty_lb = []
#ci_cloud_lb = []
#ci_tt_lb = []
#ci_mr_task_dropping = []
    
#for i in range(n_groups):
#       ci_certainty_lb.append(CI(certainty_lb [i], certainty_lb_std [i])) 
#       ci_cloud_lb.append(CI(cloud_lb [i], cloud_std [i])) 
       #ci_mect.append(CI(with_mect [i], mect_std [i]))
#       ci_tt_lb.append(CI(tt_lb [i], tt_lb_std [i]))

font = {'family' : 'DejaVu Sans',
        'weight' : 'bold',
        'size'   : 10 }
plt.rc('font', **font)

       

axes = plt.gca()
axes.set_ylim([1,40])

axes.set_xlim([-0.8, 4])


rects1 = plt.bar(index - bar_width, loadbalancer, bar_width,
                 alpha=opacity,
                 color=(0.2588,0.4433,1.0),
                 error_kw=dict(capsize = 0.8,ecolor ='black', lw=1.0),
                 label='Load Balancer',
                 hatch = "//")
                 
#rects2 = plt.bar(index, with_mect, bar_width, yerr = ci_mect,
#                 alpha=opacity,
#                 color='red',
#                 error_kw=dict(ecolor ='black'),
#                 label='MECT LB',
#                 hatch = "\\\\\\")               

rects3 = plt.bar(index, baseLine, bar_width,
                 alpha=opacity,
                 color=(1.0,0.5,0.62),
                 error_kw=dict(capsize = 0.8,ecolor ='black', lw=0.5),
                 label='Baseline',
                 hatch = "\\\\\\")


plt.xlabel('Simulation Time in seconds')
plt.ylabel('Average deadline miss rate %')
plt.title('Increasing oversubscription level through decreasing simulation time.')

ax.set_xticks(index)

ax.set_xticklabels(('20', '15','10', '5'))


ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.00), shadow=True, ncol=2)



plt.tight_layout()
plt.savefig("DecreasingSimulationTime.pdf")
#plt.show()

