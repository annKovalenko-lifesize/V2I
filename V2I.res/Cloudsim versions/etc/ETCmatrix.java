package org.cloudbus.cloudsim.examples;

import java.util.Arrays;
import java.util.HashMap;
import org.cloudbus.cloudsim.distributions.LognormalDistr;


/**
 * This class represents an Estimated Completion Time matrix 
 * 
 * @author Anna Kovalenko
 */

public class ETCmatrix {
	
	/**
	 *  The matrix is holding a mean and a standard deviation 
	 *  for each type of task on every base station
	 */
	protected LognormalDistr[][] etcMatrix = null;
	
	/**
	 * The number of Virtual Machines in the simulation and
	 * the number of Task Types
	 */
	protected int totalDataC = 0;
	protected int totalCloudlet = 0;
	
	protected HashMap<String, LognormalDistr> distributions;

	private String[] arrays;
	
	/**
	 * A private constructor to ensure that only 
	 * an correct initialized matrix could be created
	 */
	
	@SuppressWarnings("unused")
	private ETCmatrix() {
		
	};
	
	/**
	 * A parameterized constructor
	 * @param vmTotalNum takes the total number of VMs in the simulation 
	 * @param cloudletTotalnum takes the total number of Cloudlet Types in the simulation
	 */
	
	public ETCmatrix(int dataCnum, int taskTypeTotal, HashMap<String, LognormalDistr> _distributions) {
		
		this.totalDataC = dataCnum;
		this.totalCloudlet = taskTypeTotal;
		this.etcMatrix = new LognormalDistr[dataCnum][taskTypeTotal+1];
		this.distributions = _distributions;
		createETCmatrix();
		
	
		}
	
	/**
	 * Returns the LognormalDistr object
	 * @param dataCenterID
	 * @param cloudletType
	 * @return
	 */
	
	public LognormalDistr getDistribution(int dataCenterID, int cloudletType) {
			
			if (cloudletType > totalCloudlet || dataCenterID > totalDataC) {
				throw new ArrayIndexOutOfBoundsException("The Virtual Machine or the Task Type does not exist in this ETC");
			}

			return etcMatrix[dataCenterID][cloudletType];
		}

		/**
		 * Inputs the LognormalDistributions from the HashMap into
		 * the matrix (2d-array)
		 */
	
	private void createETCmatrix() {
		
		for(String key: distributions.keySet()) {
			
			arrays = key.split("\\.");
			
			int row = Integer.parseInt(arrays[0]);
			int column = Integer.parseInt(arrays[1]);
			LognormalDistr newDistribution = distributions.get(key);
			this.etcMatrix[row][column] = newDistribution;
			}
		}
	
	public double getMu(int taskType, int DataCenter) {
		
		LognormalDistr distr = etcMatrix[taskType][DataCenter];
		
		return distr.getMu();
	}
	
	public double getSigma(int taskType, int DataCenter) {
		
		LognormalDistr distr = etcMatrix[taskType][DataCenter];
		
		return distr.getSigma();
	}
	
	public double getWorseCaseTime(int taskType, int DataCenter) {
		
		LognormalDistr distr = etcMatrix[taskType][DataCenter];
		double time = distr.getMu() + distr.getSigma();
		
		return time;
		
	}
	
	public void printMatrix() {
		
		System.out.println(Arrays.deepToString(etcMatrix));
		
	}


}