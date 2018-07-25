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

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
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
import edu.boun.edgecloudsim.utils.SimLogger;

public class BasicEdgeOrchestrator extends EdgeOrchestrator {
	private int numberOfHost; //used by load balancer
	private int lastSelectedHostIndex; //used by load balancer
	private int[] lastSelectedVmIndexes; //used by each host individually
	private static Datacenter receivingBS;
	private int bestDC;
	private int recBS = -1;
	
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
		if(simScenario.equals("SINGLE_TIER:")){
			result = SimSettings.GENERIC_EDGE_DEVICE_ID;
		}
		Location deviceLoc = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(), CloudSim.clock());
		int xdev = deviceLoc.getXPos();
		int ydev = deviceLoc.getYPos();
		//System.out.print("The device location is : " + xdev + " " + ydev + "\n");
		//int yyhost = -1;
		//int xxhost = -1;

		
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
					//yyhost = yhost;
					//xxhost = xhost;
					}
				}
			}
		//System.out.print("The host location is : " + xxhost + " " + yyhost + "\n");
		
		return result;
	}
	
	@Override
	public EdgeVM getVmToOffload(Task task) {
		if(simScenario.equals("TWO_TIER_WITH_EO"))
			return selectVmOnLoadBalancer(task);
		else
			return selectVmOnHost(task);
	}
	
	public EdgeVM selectVmOnHost(Task task){
		EdgeVM selectedVM = null;

		List<EdgeVM> vmArray = receivingBS.getVmList();
		
		if(policy.equalsIgnoreCase("SJF")){
			for(int i = 0; i < vmArray.size(); i++) {
				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(i).getVmType());
				double targetVmCapacity = (double)100 - vmArray.get(i).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
				if(requiredCapacity <= targetVmCapacity)
					selectedVM = vmArray.get(i);
			}
			
		}
		
//		else if(policy.equalsIgnoreCase("WORST_FIT")){
//			double selectedVmCapacity = 0; //start with min value
//			for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
//				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
//				double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
//				if(requiredCapacity <= targetVmCapacity && targetVmCapacity > selectedVmCapacity){
//					selectedVM = vmArray.get(vmIndex);
//					selectedVmCapacity = targetVmCapacity;
//				}
//			}
//		}
//		else if(policy.equalsIgnoreCase("BEST_FIT")){
//			double selectedVmCapacity = 101; //start with max value
//			for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
//				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
//				double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
//				if(requiredCapacity <= targetVmCapacity && targetVmCapacity < selectedVmCapacity){
//					selectedVM = vmArray.get(vmIndex);
//					selectedVmCapacity = targetVmCapacity;
//				}
//			}
//		}
//		else if(policy.equalsIgnoreCase("FIRST_FIT")){
//			for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
//				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
//				double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
//				if(requiredCapacity <= targetVmCapacity){
//					selectedVM = vmArray.get(vmIndex);
//					break;
//				}
//			}
//		}
	/*	else if(policy.equalsIgnoreCase("NEXT_FIT")){
			int tries = 0;
			while(tries < vmArray.size()){
				lastSelectedVmIndexes[relatedHostId] = (lastSelectedVmIndexes[relatedHostId]+1) % vmArray.size();
				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(lastSelectedVmIndexes[relatedHostId]).getVmType());
				double targetVmCapacity = (double)100 - vmArray.get(lastSelectedVmIndexes[relatedHostId]).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
				if(requiredCapacity <= targetVmCapacity){
					selectedVM = vmArray.get(lastSelectedVmIndexes[relatedHostId]);
					break;
				}
				tries++;
			}
		}*/
		
		return selectedVM;
	}

	public EdgeVM selectVmOnLoadBalancer(Task task){
		
		EdgeVM selectedVM = null;
		
		//System.out.print(task.getCloudletId()+"..."+bestProb+"..."+ bestDC +"\n");
		//List<EdgeVM> vmArray = dc.getVmList();
		
		if(policy.equalsIgnoreCase("SJF")){
			bestDC = getDC(task);
			Datacenter dc = SimManager.getInstance().edgeServerManager.getDatacenterList().get(bestDC);
			List<EdgeVM> vmArray = dc.getVmList();
			for(int i = 0; i < vmArray.size(); i++) {
				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(i).getVmType());
				double targetVmCapacity = (double)100 - vmArray.get(i).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
				if(requiredCapacity <= targetVmCapacity) {
					selectedVM = vmArray.get(i);
				}
				}
				
		}
		
		
//		else if(policy.equalsIgnoreCase("WORST_FIT")){
//			double selectedVmCapacity = 0; //start with min value
//			for(int hostIndex=0; hostIndex<numberOfHost; hostIndex++){
//				List<EdgeVM> vmArray = dc.getVmList();
//				for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
//					double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
//					double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
//					if(requiredCapacity <= targetVmCapacity && targetVmCapacity > selectedVmCapacity){
//						selectedVM = vmArray.get(vmIndex);
//						selectedVmCapacity = targetVmCapacity;
//					}
//				}
//			}
//		}
//		else if(policy.equalsIgnoreCase("BEST_FIT")){
//			double selectedVmCapacity = 101; //start with max value
//			for(int hostIndex=0; hostIndex<numberOfHost; hostIndex++){
//				List<EdgeVM> vmArray = dc.getVmList();
//				for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
//					double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
//					double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
//					if(requiredCapacity <= targetVmCapacity && targetVmCapacity < selectedVmCapacity){
//						selectedVM = vmArray.get(vmIndex);
//						selectedVmCapacity = targetVmCapacity;
//					}
//				}
//			}
//		}
//		else if(policy.equalsIgnoreCase("FIRST_FIT")){
//			for(int hostIndex=0; hostIndex<numberOfHost; hostIndex++){
//				List<EdgeVM> vmArray = dc.getVmList();
//				for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
//					double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
//					double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
//					if(requiredCapacity <= targetVmCapacity){
//						selectedVM = vmArray.get(vmIndex);
//						break;
//					}
//				}
//			}
//		}
//		else if(policy.equalsIgnoreCase("NEXT_FIT")){
//			int hostCheckCounter = 0;	
//			while(selectedVM == null && hostCheckCounter < numberOfHost){
//				int tries = 0;
//				lastSelectedHostIndex = (lastSelectedHostIndex+1) % numberOfHost;
//
//				List<EdgeVM> vmArray = dc.getVmList();
//				while(tries < vmArray.size()){
//					lastSelectedVmIndexes[lastSelectedHostIndex] = (lastSelectedVmIndexes[lastSelectedHostIndex]+1) % vmArray.size();
//					double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(lastSelectedVmIndexes[lastSelectedHostIndex]).getVmType());
//					double targetVmCapacity = (double)100 - vmArray.get(lastSelectedVmIndexes[lastSelectedHostIndex]).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
//					if(requiredCapacity <= targetVmCapacity){
//						selectedVM = vmArray.get(lastSelectedVmIndexes[lastSelectedHostIndex]);
//						break;
//					}
//					tries++;
//				}
//
//			hostCheckCounter++;
//			}
//		}
		
		return selectedVM;
	}
	
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
	
	public int getDC(Task task) {
		ETCMatrix matrix = SimLogger.getInstance().matrix;
		bestDC = -1;
		double bestProb = -1;
		double dl1 = SimManager.getInstance().getEdgeOrchestrator().deadline(task, matrix, 0.0001);
		if (matrix.getProbability(recBS, task.getTaskType().ordinal(), dl1) == 0) {
			bestDC = recBS;
			}
		else {
			for(int i = 0; i < matrix.getDataCnum(); i++) {
				double dl = SimManager.getInstance().getEdgeOrchestrator().deadline(task, matrix, 0.0001);
				double prob = matrix.getProbability(i, task.getTaskType().ordinal(), dl);
				if (prob > bestProb) {
					Datacenter dc = SimManager.getInstance().edgeServerManager.getDatacenterList().get(i);
					List<EdgeVM> vmArray = dc.getVmList();
					for(int j = 0; j < vmArray.size(); j++) {
						double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(j).getVmType());
						double targetVmCapacity = (double)100 - vmArray.get(j).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
						if(requiredCapacity <= targetVmCapacity) {
							bestProb = prob;
							bestDC = i;
						}
						}
					}
				}
		}
		return bestDC;
	}
	
	

	public Datacenter getReceivingBS() {
		return receivingBS;
	}

	public void setReceivingBS(Datacenter datacenter) {
		this.receivingBS = datacenter;
	}
}