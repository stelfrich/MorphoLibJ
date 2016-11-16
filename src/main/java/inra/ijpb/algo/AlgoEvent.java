/**
 * 
 */
package inra.ijpb.algo;

/**
 * An event class for storing information about the status and progression of
 * an algorithm.
 * 
 * @author David Legland
 *
 */
public class AlgoEvent 
{
	/**
	 * The algorithm object that threw this event
	 */
	protected Object source;
	
	protected String status;
	
	protected double step;
	protected double total;
	
	public AlgoEvent(Object source, String status, double step, double total) 
	{
		this.source = source;
		this.status = status;
		this.step = step;
		this.total = total;
	}
	
	public AlgoEvent(Object source, String status) 
	{
		this.source = source;
		this.status = status;
		this.step = 0;
		this.total = 0;
	}
	
	public AlgoEvent(Object source, double step, double total) 
	{
		this.source = source;
		this.status = "";
		this.step = step;
		this.total = total;
	}
	
	/**
	 * @return the source object
	 */
	public Object getSource() 
	{
		return source;
	}

	/**
	 * @return the current status of the algorithm
	 */
	public String getStatus() 
	{
		return status;
	}
	
	/**
	 * @return the current progression of the algorithm
	 */
	public double getCurrentProgress() 
	{
		return step;
	}

	/**
	 * @return the total progression of the algorithm
	 */
	public double getTotalProgress() 
	{
		return total;
	}

	/**
	 * @return the progress ratio, as the ratio of current progression over total progression.
	 */
	public double getProgressRatio() 
	{
		return this.step / this.total;
	}
}
