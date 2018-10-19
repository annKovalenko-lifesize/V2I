package edu.boun.edgecloudsim.utils;

import edu.boun.edgecloudsim.core.SimSettings;

public class NormDistr {
	
	protected double mean;
	protected double stdev;
	protected int hostID;
	protected SimSettings.APP_TYPES taskType;
	
	NormDistr() {	
	};
	
	public NormDistr(double _mean, double _stdev) {
		
		this.mean = _mean;
		this.stdev = _stdev;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getStdev() {
		return stdev;
	}
	
	public NormDistr convolveDistr(NormDistr b) {
 
		double oldMu = this.mean;
		double oldSigma = this.stdev;
		double newMu = b.getMean();
		double newSigma = b.getStdev();
		double finalMu = oldMu + newMu;
		double finalSigma = Math.sqrt((oldSigma*oldSigma) + (newSigma*newSigma));
		NormDistr newDist = new NormDistr(finalMu, finalSigma);
		return newDist;
	}

	public void setStdev(double stdev) {
		this.stdev = stdev;
	}

	public int getHostID() {
		return hostID;
	}

	public void setHostID(int hostID) {
		this.hostID = hostID;
	}

	public SimSettings.APP_TYPES getTaskType() {
		return taskType;
	}

	public void setTaskType(SimSettings.APP_TYPES taskType) {
		this.taskType = taskType;
	}
	
	
	

}
