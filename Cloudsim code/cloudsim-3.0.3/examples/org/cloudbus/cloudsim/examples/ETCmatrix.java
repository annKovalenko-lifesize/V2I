package org.cloudbus.cloudsim.examples;

import java.util.Iterator;

import org.cloudbus.cloudsim.network.FloydWarshall_Float;

import org.cloudbus.cloudsim.network.TopologicalLink;

/**
 * This class represents an Estimated Completion Time matrix 
 * 
 * @author Anna Kovalenko
 */

public class ETCmatrix {
	
	/**
	 *  The matrix is holding a mean and a standart deviation 
	 *  for each type of task on every base station
	 */
	protected double[][] etcMatrix = null;
	
	/**
	 * The number of Data Centers in the simulation and
	 * the number of Task Types
	 */
	protected int totalDataCenter = 0;
	protected int totalCloudlet = 0;
	
	/**
	 * Mu and Sigma values
	 */
	
	protected double mu = 0;
	protected double sigma = 0;
	
	/**
	 * A private constructor to ensure that only 
	 * an correct initialized matrix could be created
	 */
	
	@SuppressWarnings("unused")
	private ETCmatrix() {
		
	};
	
	/**
	 * A prameterised constructor
	 * @param dcTotalNum takes the total number of VMs in the simulation 
	 * @param cloudletTotalnum takes the total number of Cloudlet Types in the simulation
	 */
	
	public ETCmatrix(int dcTotalNum, int cloudletTotalnum) {
		
		this.totalDataCenter = dcTotalNum;
		this.totalCloudlet = cloudletTotalnum;
		etcMatrix = new double[dcTotalNum][cloudletTotalnum];
		calculateShortestPath();
		}
	
	public double getTime(int dataCenterID, int cloudletType) {
			
			if (cloudletType > totalCloudlet || dataCenterID > totalDataCenter) {
				throw new ArrayIndexOutOfBoundsException("The Virtual Machine or the Task Type does not exist in this ETC");
			}

			return etcMatrix[dataCenterID][cloudletType];
		}

		/**
		 * creates all internal necessary network-distance structures from the given graph for
		 * similarity we assume all kommunikation-distances are symmetrical thus leads to an undirected
		 * network
		 * 
		 * @param graph this graph contains all node and link information
		 * @param directed defines to preinitialize an directed or undirected Delay-Matrix!
		 */
		private void createDelayMatrix(TopologicalGraph graph, boolean directed) {

			// number of nodes inside the network
			mTotalNodeNum = graph.getNumberOfNodes();

			mDelayMatrix = new float[mTotalNodeNum][mTotalNodeNum];

			// cleanup the complete distance-matrix with "0"s
			for (int row = 0; row < mTotalNodeNum; ++row) {
				for (int col = 0; col < mTotalNodeNum; ++col) {
					mDelayMatrix[row][col] = Float.MAX_VALUE;
				}
			}

			Iterator<TopologicalLink> itr = graph.getLinkIterator();

			TopologicalLink edge;
			while (itr.hasNext()) {
				edge = itr.next();

				mDelayMatrix[edge.getSrcNodeID()][edge.getDestNodeID()] = edge.getLinkDelay();

				if (!directed) {
					// according to aproximity of symmetry to all kommunication-paths
					mDelayMatrix[edge.getDestNodeID()][edge.getSrcNodeID()] = edge.getLinkDelay();
				}

			}
		}

		/**
		 * just calculates all pairs shortest paths
		 */
		private void calculateShortestPath() {
			FloydWarshall_Float floyd = new FloydWarshall_Float();

			floyd.initialize(mTotalNodeNum);
			mDelayMatrix = floyd.allPairsShortestPaths(mDelayMatrix);
		}

		/**
		 * this method just creates an string-output from the internal structures... eg. printsout the
		 * delay-matrix...
		 */
		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();

			buffer.append("just a simple printout of the distance-aware-topology-class\n");
			buffer.append("delay-matrix is:\n");

			for (int column = 0; column < mTotalNodeNum; ++column) {
				buffer.append("\t" + column);
			}

			for (int row = 0; row < mTotalNodeNum; ++row) {
				buffer.append("\n" + row);

				for (int col = 0; col < mTotalNodeNum; ++col) {
					if (mDelayMatrix[row][col] == Float.MAX_VALUE) {
						buffer.append("\t" + "-");
					} else {
						buffer.append("\t" + mDelayMatrix[row][col]);
					}
				}
			}

			return buffer.toString();
		}
	
	

}