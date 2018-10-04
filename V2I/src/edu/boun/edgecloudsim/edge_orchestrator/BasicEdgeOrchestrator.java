/*
 * Title:        EdgeCloudSim - Basic Edge Orchestrator implementation
 * 
 * Description: 
 * BasicEdgeOrchestrator implements basic algorithms which are
 * first/next/best/worst/random fit algorithms while assigning
 * requests to the edge devices.
 *               
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.edge_orchestrator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import java.lang.Math;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.core.CloudSim;


import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.boun.edgecloudsim.edge_client.CpuUtilizationModel_Custom;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.utils.ETCMatrix;
import edu.boun.edgecloudsim.utils.Location;
import edu.boun.edgecloudsim.utils.NormDistr;
import edu.boun.edgecloudsim.utils.SimLogger;

public class BasicEdgeOrchestrator extends EdgeOrchestrator {
	private int numberOfHost; //used by load balancer
	@SuppressWarnings("unused")
	private int lastSelectedHostIndex; //used by load balancer
	private int[] lastSelectedVmIndexes; //used by each host individually
	private static Datacenter receivingBS; // !!! IMPORTANT !!! DON'T USE THE METHOD Datacenter.getId(), it's messed up, use recBS instead if u need an index.
	private int recBS = -1; //Receiving DC ID
	private int bestBS = -1;
	private ArrayList<Integer> neighboringBS = new ArrayList<>(); //Neighboring BaseStations
	
	public BasicEdgeOrchestrator(String _policy, String _simScenario) {
		super(_policy, _simScenario);
	}

	@Override
	public void initialize() {
		numberOfHost=SimSettings.getInstance().getNumOfEdgeHosts();
		
		lastSelectedHostIndex = -1;
		lastSelectedVmIndexes = new int[numberOfHost];
		for(int i=0; i<numberOfHost; i++)
			lastSelectedVmIndexes[i] = -1;
	}
	
	

	@Override
	public int getDeviceToOffload(Task task) {
		
		int result = SimSettings.EDGE_ORCHESTRATOR_ID;
		if(simScenario.equals("SINGLE_TIER")){
			result = SimSettings.GENERIC_EDGE_DEVICE_ID;
		}
		/* Calculate the location of receiving Base Station
		 * the closest one to the device(vehicle)
		 */
		
		Location deviceLoc = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(), CloudSim.clock());
		int xdev = deviceLoc.getXPos();
		int ydev = deviceLoc.getYPos();
		
		List<Datacenter> datacenters = SimManager.getInstance().edgeServerManager.getDatacenterList();
		
		double best = 1000;

		for(int i = 0; i < datacenters.size(); i++) {
			List<EdgeHost> hostlist = datacenters.get(i).getHostList();
			
			for(EdgeHost host : hostlist) {
				Location hostLocation = host.getLocation();
				int xhost = hostLocation.getXPos();
				int yhost = hostLocation.getYPos();
				double dist = Math.sqrt((Math.pow((double)xdev-xhost, 2))+ (Math.pow((double)ydev-yhost, 2)));
				if (dist <= best) {
					best = dist;
					setReceivingBS(datacenters.get(i));
					recBS = i;
					task.setDc(i);
					}
				}
			}
		
		return result;
	}
	
	@Override
	public EdgeVM getVmToOffload(Task task) {
		if(simScenario.equals("TWO_TIER_WITH_EO")) {
			SimLogger.getInstance().setInitialDC(task.getCloudletId(), recBS);
			return selectVmOnLoadBalancer(task);
		}
		else
			return selectVmOnHost(task);
	}
	
	/*
	 * The base case policy;
	 */
	
	public EdgeVM selectVmOnHost(Task task){
		
		EdgeVM selectedVM = null;
		List<EdgeVM> vmArray = receivingBS.getVmList();
		
		for(int i = 0; i < vmArray.size(); i++) {
			double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(i).getVmType());
			double targetVmCapacity = (double)100 - vmArray.get(i).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
			if(requiredCapacity <= targetVmCapacity)
				selectedVM = vmArray.get(i);
			}
		
		return selectedVM;
	}
	
	/*
	 * Load Balancer Policy;
	 */

	public EdgeVM selectVmOnLoadBalancer(Task task){
		
		getNeighbors();
		//System.out.print("!!!!!");
		
		EdgeVM selectedVM  = getDC(task);
		
		if(selectedVM == null) {
		
		List<EdgeVM> vmArray = SimManager.getInstance().edgeServerManager.getDatacenterList().get(bestBS).getVmList();
		
		for(int j = 0; j < vmArray.size(); j++) {
			double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(j).getVmType());
			double targetVmCapacity = (double)100 - vmArray.get(j).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
			if(requiredCapacity <= targetVmCapacity) {
				selectedVM = vmArray.get(j);
				
				}
			}
		}
		return selectedVM;
		
	}
	
	/*
	 * Calculate the deadline for the task
	 * 
	 */
	
	public double deadline(Task task, ETCMatrix b, double slack) {
		
		double comDelay = SimManager.getInstance().getNetworkModel().getUploadDelay(task.getMobileDeviceId(), recBS ,task.getCloudletFileSize()) + 
							SimManager.getInstance().getNetworkModel().getDownloadDelay(recBS, task.getMobileDeviceId(), task.getCloudletFileSize());
		double submissionTime = task.getSubmissionTime();
		double avgMu = 0;
		for(int i = 0; i < b.getDataCnum(); i++) {
			avgMu += b.getMu(i, task.getTaskType().ordinal());
		}
		double avgMuAll = avgMu/b.getDataCnum();
		double deadline =  avgMuAll+ slack + submissionTime + comDelay;
		task.setDeadLine(deadline);
		return deadline;
	}
	
	
	
	public EdgeVM getDC(Task task) {
		
		EdgeVM selectedVM = null;

		double dl = SimManager.getInstance().getEdgeOrchestrator().deadline(task, SimLogger.getInstance().matrix, 0.0001);
		double bestProb = SimLogger.getInstance().matrix.getProbability(recBS, task.getTaskType().ordinal(), dl);
		
		List<EdgeVM> recvmArray = receivingBS.getVmList();
		
		for(int j = 0; j < recvmArray.size(); j++) {
			double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(recvmArray.get(j).getVmType());
			double targetVmCapacity = (double)100 - recvmArray.get(j).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
			if(requiredCapacity <= targetVmCapacity) {
				selectedVM = recvmArray.get(j);
				}
			}
		
		for(int i = 0; i < neighboringBS.size(); i++) {
			
			double exMu = SimLogger.getInstance().matrix.getMu(neighboringBS.get(i), task.getTaskType().ordinal());
			double exSigma = SimLogger.getInstance().matrix.getSigma(neighboringBS.get(i), task.getTaskType().ordinal());
			double trMu = SimLogger.getInstance().ETTmatrix.getMu(recBS, neighboringBS.get(i));
			double trSigma = SimLogger.getInstance().ETTmatrix.getSigma(recBS, neighboringBS.get(i));
			
			double finalMu = exMu + trMu;
			double finalSigma = Math.sqrt((exSigma*exSigma) + (trSigma*trSigma));
			
			NormDistr resultDistr = new NormDistr(finalMu, finalSigma);
			
			double prob = SimLogger.getInstance().matrix.getProbConvolved(dl, resultDistr);
			
			if (prob > bestProb || selectedVM == null) {
				bestProb = prob;
				bestBS = neighboringBS.get(i);
				}
			}
		
		return selectedVM;
		
	}
	
	/*
	 * Fill the ArrayList for neighboring Base Stations;
	 */
	
	public void getNeighbors() {
		
		List<EdgeHost> recLoc = receivingBS.getHostList();
		Location recLocation = recLoc.get(0).getLocation();
		int xRec = recLocation.getXPos();
		int yRec = recLocation.getYPos();
		
		List<Datacenter> datacenters = SimManager.getInstance().edgeServerManager.getDatacenterList();

		for(int i = 0; i < datacenters.size(); i++) {
			List<EdgeHost> neighbourlist = datacenters.get(i).getHostList();
			Location neighLocation = neighbourlist.get(0).getLocation();
			int xNeigh = neighLocation.getXPos();
			int yNeigh = neighLocation.getYPos();
			int xdiff = xRec-xNeigh;
			int ydiff = yRec-yNeigh;
			if(Math.abs(xdiff) == 1 && Math.abs(ydiff)== 1) {
				neighboringBS.add(i);		
				}
			}

	}

	
	

	public Datacenter getReceivingBS() {
		return receivingBS;
	}

	public static void setReceivingBS(Datacenter datacenter) {
		receivingBS = datacenter;
	}
}