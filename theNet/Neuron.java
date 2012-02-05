/**
 * This implements a simple artificial neuron.
 */
package theNet;

import java.util.Random;

/**
 * This class represents a *basic* artificial neuron, and is used by
 * the class Net.
 * 
 * It can reacts on an input vector and a weight vector using a 
 * sigmoid function. There is also a bias input.
 * 
 * @author Jonas Huber
 */
public class Neuron {
	
	
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
	 * the sigmoid function
	 * 							phi(t) := tanh(x) * bias
	 * 
	 * where t is the linear combination of the input vector's elements with
	 * the corresponding elements of the weight's vector.
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
		
		/* TODO: Is the bias correctly used? Or has it to be *added* instead
		 * of being multiplied?
		 */
		
		/* It has to be a hyperbolic tangent function. */
		result = Math.tanh(linearComb + bias );
		
		return result;
	} /* End of getOutput(). */
	
	
	/**
	 * This is just used for some basic testing in an early developement stage.
	 * 
	 * @param args
	 */
	public static void main( String[] args ) {
		System.out.println("(II) Performing some test on a neuron...");
		
		Random rnd = new Random();
		
		double [] w = new double [64];
		double [] i = new double [64];
		double temp;
		
		for ( int j = 0; j<64; j++ ) {
			
			/* Use random weights. */
			w[j] = rnd.nextDouble();
			
			/* Input vector should contain -1,0,1, like it would in a game. */
			temp = (int)rnd.nextInt(3);
			if ( temp == 2 ) {
				i[j]= -1;
			} else {
				i[j] = temp;
			}
		}
		
		Neuron n = new Neuron(w, 1);
		
		double output = 0;
		
		try {
			output = n.getOutput( i );
		} catch ( Exception e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		}
		
		System.out.println("(II) Answer of neuron: " + output );
		
	} /* End of main(). */
}
