#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Sep  1 15:22:02 2018

@author: c00303945
"""
import numpy as np
import matplotlib.pyplot as plt
import math
from scipy import stats

# data to plot
n_groups = 6

def CI(mean,std):
    R = stats.norm.interval(0.95,loc=mean,scale=std/math.sqrt(30))
    diff = mean-R[0]
    return diff

baseline = (19.077422716770986,35.39310724915832,48.04668182106097,59.09113985472781,66.83732481548444,71.14027589396615)
baselineS = (3.3064072655523353,3.2436893353632814,3.294138143679737,2.4936702186846733,1.5482237055891162,1.1340232597954472)

lb = (7.831726333554623,22.76849334272134,43.81388026437516,56.676998251381384,63.55558100886687,69.11993987513561)
lbS = (1.0599180692938512,4.893936317507925,3.2058893898306176,2.925508083670556,2.194477252587292,1.8837952106154283)

cert = (7.897380952840213,19.59443324623504,45.764194726327325,57.55210165353585,63.93559948132978,68.97565847891205)
certS = (1.1028254845353718,2.892829225686091,4.274603397180923,3.009855174785951,2.38867211676106,2.4405172632590846)

mect = (8.090205550210452,22.491919549859702,41.977287630006444,56.64185288119116,63.99531362040909,69.39831404475343)
mectS = (0.9270344683525,4.1501340410265195,4.6270219100944825,2.3238792683732514,1.6917923983881724,1.6756579154066145)


ci_b = []
ci_l = []
ci_c = []
ci_m = []

for i in range(len(baseline)):
        ci_b.append(CI(baseline[i],baselineS[i]))
        ci_l.append(CI(lb[i], lbS[i]))
        ci_c.append(CI(cert[i],certS[i]))
        ci_m.append(CI(mect[i],mectS[i]))


fig, ax = plt.subplots()
index = np.arange(n_groups)
bar_width = 0.15
opacity = 1.0
                
font = {'family' : 'DejaVu Sans',
        'weight' : 'bold',
        'size'   : 10 }
plt.rc('font', **font)

       

axes = plt.gca()
axes.set_ylim([0,85])

axes.set_xlim([-0.5, 5.5])


rects1 = plt.bar(index - 2*bar_width, baseline, bar_width, yerr = ci_b,
                 alpha=opacity,
                 color='white',
                 edgecolor='cyan',
                 error_kw=dict(capsize = 2.0,ecolor ='black', lw=1.0),
                 label='Baseline',
                 hatch = "*")


rects3 = plt.bar(index - bar_width, lb, bar_width, yerr = ci_l,
                 alpha=opacity,
                 color='white',
                 error_kw=dict(capsize = 2.0,ecolor ='black', lw=1.0),
                 edgecolor='red',
                 label='Load Balancer',
                 hatch = "o")

rects2 = plt.bar(index, mect, bar_width, yerr = ci_m,
                 alpha=opacity,
                 color='white',
                 edgecolor='grey',
                 error_kw=dict(capsize = 2.0,ecolor ='black', lw=1.0),
                 label='MECT',
                 hatch = "/")


rects4 = plt.bar(index + bar_width, cert, bar_width, yerr = ci_c,
                 alpha=opacity,
                 color='white',
                 error_kw=dict(capsize = 2.0,ecolor ='black', lw=1.0),
                 edgecolor='blue',
                 label='Certainty',
                 hatch = ".")


plt.xlabel('Number of Vehicles (#)')
plt.ylabel('Average tasks deadline miss rate (%)')
plt.title('Increasing oversubscription')

ax.set_xticks(index)

ax.set_xticklabels(('1200', '2400','3600', '4800', '6000', '7200'))


ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.00), shadow=True, ncol=2)



plt.tight_layout()
#plt.savefig("overs.pdf")
plt.show()

