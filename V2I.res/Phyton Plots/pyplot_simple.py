"""
=============
Pyplot Simple
=============

"""
import matplotlib.pyplot as plt
axes = plt.gca()
axes.set_xlim([1,100])
axes.set_ylim([0,100])
plt.plot([60, 59, 58, 53, 57, 55, 54, 52, 49, 57, 56, 55, 61, 57, 49, 54, 52, 54, 58, 54, 51, 54, 61, 59, 46, 57, 53, 51, 54, 54, 62, 56, 54, 52, 55, 51, 54, 64, 55, 54, 53, 55, 56, 51, 54, 52, 52, 56, 56, 59, 56, 57, 54, 58, 53, 55, 55, 61, 49, 55, 58, 62, 48, 55, 59, 52, 51, 52, 60, 59, 59, 56, 51, 56, 55, 57, 54, 52, 55, 56, 52, 54, 60, 55, 46, 51, 56, 54, 53, 51, 48, 54, 58, 53, 55, 51, 56, 58, 54, 50])
plt.plot([27, 29, 23, 30, 22, 29, 23, 27, 25, 25, 27, 29, 19, 27, 21, 25, 35, 29, 26, 30, 26, 23, 22, 19, 28, 30, 26, 26, 20, 27, 26, 23, 24, 27, 27, 32, 30, 23, 34, 25, 22, 19, 27, 26, 32, 26, 23, 23, 29, 25, 22, 31, 22, 27, 28, 28, 25, 33, 25, 18, 23, 27, 29, 29, 22, 31, 24, 24, 35, 26, 23, 22, 22, 28, 30, 32, 29, 31, 28, 28, 20, 36, 27, 25, 26, 24, 22, 23, 25, 29, 35, 24, 34, 25, 31, 35, 26, 19, 23, 24])
plt.ylabel('The Number Tasks Dropped')
plt.xlabel('The Trial #')
plt.show()