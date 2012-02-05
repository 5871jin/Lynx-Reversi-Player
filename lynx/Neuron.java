package lynx;


/**
* This class implements an artificial neuron as used by the class Net.
* 
* It reacts on an input vector and a weight vector using a 
* hyperbolic tangent activation function. There is also a bias input.
* 
* @author Jonas Huber
* @author Benedikt Koeppel
*/
public class Neuron {

	/* Local variables. */
	private double[] weights;
	private double bias;
	
	
	/**
	 * Constructs a new neuron using a specified vector with the weights.
	 * 
	 * @param weights
	 * @param bias 
	 */
	public Neuron( double[] weights, double bias ) {
		this.weights = weights;
		this.bias = bias;
	} /* End of constructor Neuron(). */
	
	
	/**
	 * Calculates the answer of the neuron to a given input vector using
	 * the hyperbolic tangent function
	 * 							phi(t) := tanh(x + bias * 1)
	 * 
	 * where t is the linear combination of the input vector's elements with
	 * the corresponding elements of the weight-vector.
	 * 
	 * Note that the input vector must have the same length as the weights
	 * vector used to initialize this neuron.
	 * 
	 * @param input A vector of inputs.
	 * @return The answer of the neuron to the given input.
	 * @throws Exception If the length of the input-vector doesn't match the
	 * one of the weights-vector.
	 */
	public double getOutput( double[] input ) throws Exception {
		
		/* If the length does not match, it does not make sense to go further. */
		if ( input.length != weights.length ) {
			throw new Exception("(EE) Neuron.getOutput(): Length of input and weights don't match!");
		}
		
		double result = 0;
		double linearComb = 0;
		
		/* Build the linear combination of weights and inputs. */
		for ( int i = 0; i<input.length; i++ ) {
			linearComb = linearComb + ( weights[i] * input[i] );
		}
		
		/* It has to be a hyperbolic tangent function. */
		result = Math.tanh(linearComb + bias );
		
		return result;
	} /* End of getOutput(). */
	
	/*
	 * NOTE: Removed method main() because it was only used for testing purposes.
	 */

} /* End of class Tuft. */
