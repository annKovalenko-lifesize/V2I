import numpy as np
import matplotlib.pyplot as plt
from scipy import stats
import math
def getMeanAndCI(ontimes,i):
    n, min_max, mean, var, skew, kurt = stats.describe(ontimes)
    std=math.sqrt(var)
    R = stats.norm.interval(0.95,loc=mean,scale=std/math.sqrt(i)) #definition's way
    #R = stats.norm.interval(0.05,loc=mean,scale=std) #dr.Amini's dropbox file way
    diff=mean-R[0]
    return mean,diff
    #ci=diff/int(i)*100 #dr.Amini's dropbox file way
    #return ci #dr.Amini's dropbox file way

def insMeanAndCI(mean,CI,raw,count):
    cin=getMeanAndCI(raw[l],count)
    mean.append(cin[0])
    CI.append(cin[1])
    

##########start data section
#dump raw data here, so we can calculate both average and confidence interval
raw_centralcloud=[[44.82,50.22,55.89,64.35,61.26],[52.74,60.85,58.24,65.1,66.93],[64.09,64.17,67.31,71.1,75.48],[71.19,69.26,74.78,80.52,77.65]]

raw_cdn=[[18.37,15.37,21.37],[22.19,14.19,30.19],[37.24,30.24,44.24],[45.97,55.97,35.97]]

raw_fdnnonfdn=[[21.77,8.45,8.88,27.53,8.41],[10.09,23.26,18.85,24.31,8.54],[27.62,25.66,31.13,45.31,28.13],[24.43,32.12,43.69,63.42,25.69]]

raw_fdnnaive=[[3.12,3.53,3.01,9.58,4.3],[5.95,25.32,9.22,6.83,13.32],[31.92,14.13,6.72,37.81,27.15],[21.39,30.19,51,21.15,34.83]]

raw_fullfdn=[[3.36,3.09,3.51,2.49,3.36],[4.21,6.04,6.59,11.48,6.48],[26.08,9.27,7.34,22.91,15.02],[44.19,20.91,18.29,19.14,43.03]]

#create array for mean, and confidence interval
centralcloud = []
cdn = []
fdnnonfdn = []
fdnnaive = []
fullfdn = []
#
centralcloud_ci=[]
fdnnonfdn_ci=[]
cdn_ci =[]
fdnnaive_ci=[]
fullfdn_ci=[]


#calculate CI and mean
for l in range(len(raw_fullfdn)):
    #format: mean array, CI array, raw data, how many trials in the raw data
    #don't forget to change number 30 to number of trials you actually test
    insMeanAndCI(centralcloud,centralcloud_ci,raw_centralcloud,30)
    insMeanAndCI(cdn,cdn_ci,raw_cdn,30)
    insMeanAndCI(fdnnonfdn,fdnnonfdn_ci,raw_fdnnonfdn,30)
    insMeanAndCI(fdnnaive,fdnnaive_ci,raw_fdnnaive,30)
    insMeanAndCI(fullfdn,fullfdn_ci,raw_fullfdn,30)

###########################################
# initiation
fig, ax = plt.subplots()
axes = plt.gca()
############
#your main input parameter section
n_groups = 4 # number of differentdata to plot-1
xlabel='Number of Video Segments'
ylabel='Segment deadline miss rate (%)'
xtick=('3000','3500', '4000', '4500')
labels=['Central Cloud','CDN','Isolated FDN','Deterministic FDN','Robust FDN']
legendcolumn= 2 #number of column in the legend
data=[centralcloud,cdn,fdnnonfdn,fdnnaive,fullfdn]
yerrdata=[centralcloud_ci,cdn_ci,fdnnonfdn_ci,fdnnaive_ci,fullfdn_ci]
axes.set_ylim([0,100]) #y axis scale

############
#auto calculated values and some rarely change config, can also overwrite
axes.set_xlim([-0.5, len(xtick)]) #y axis
font = {'family' : 'DejaVu Sans',
        'weight' : 'bold',
        'size'   : 26 }
bar_width =1.0/(n_groups+2) 
edgecols=['magenta','orange','royalblue','forestgreen','red','mediumblue','limegreen','lightblue','darkgreen'] #prepared 9 colors
hatch_arr=["//",".","\\\\","////","---","x","+","*","o"] #prepared 9 hatch style
opacity = 1 #chart opacity
offsetindex=(n_groups-1)/2.0


############
#plot section
plt.rc('font', **font)
index = np.arange(n_groups)


for i in range(0,n_groups):
    #draw internal hatch, and labels
    plt.bar(index - (offsetindex-i)*bar_width, data[i], bar_width,
                     alpha=opacity,                 
                     hatch=hatch_arr[i],
                	 color='white',
		     edgecolor=edgecols[i],
             label=labels[i],
		     lw=1.0,
		     zorder = 0)
    #draw black liner and error bar
    plt.bar(index - (offsetindex-i)*bar_width, data[i], bar_width, yerr =
		    yerrdata[i],                              
                    color='none',
		    error_kw=dict(ecolor='black',capsize=3),
                    edgecolor='k',
            
		    zorder = 1,

		    lw=1.0)



plt.xlabel(xlabel,fontsize=26)
plt.ylabel(ylabel,fontsize=26)
#plt.title('Execution time (deadline sorted batch queue)')

ax.set_xticks(index)

ax.set_xticklabels(xtick)

ax.legend(loc='upper center', prop={'size': 18},bbox_to_anchor=(0.5, 1.00), shadow= True, ncol=legendcolumn)

plt.tight_layout()
plt.show()


