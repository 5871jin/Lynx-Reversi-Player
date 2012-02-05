/**
 * This implements a specific artificial neural net.
 */
package theNet;

import java.util.Random;

/**
 * Implements a net of the structure described in [1]. Its purpose is to
 * be an Othello-player someday, maybe... ;-)
 * <br /><br />
 * [1] Manning, Edward P., (2007), "Temporal Difference Learning of an Othello
 * Evaluation Function for a Small Neural Network with Shared Weights," Proceedings
 * of the 2007 IEEE Symposium on Computational Intelligence and Games (CIG 2007).
 * 
 * @author Jonas Huber
 */
public class Net {
	
	/* The neurons of our net. */
	private Neuron hiddenUnitOne;
	private Neuron hiddenUnitTwo;
	private Neuron hiddenUnitThree;
	private Neuron hiddenUnitFour;
	private Neuron outputUnit;
	
	/* Constants. */
	private final static int INPUT_LENGTH = 64;
	
	/* Weights and other constants as calculated in [1]. */
	private final static double[][] hiddenUnitWeights = {
											{-0.412767, -0.086716, -0.137243, -0.116847, -0.108441, -0.132597, -0.082691, -0.447977},
											{ 0.095415,  0.066026,  0.081115,  0.039152,  0.037808,  0.075500,  0.060762,  0.098185},
											{-0.014942,  0.006509, -0.002831, -0.007430, -0.010424, -0.003539,  0.018977, -0.011253},
											{ 0.018324,  0.002287,  0.014223,  0.007309,  0.006887,  0.008938, -0.002179,  0.025855},
											{ 0.008673,  0.001184, -0.004982, -0.003349, -0.003205, -0.011319, -0.001403,  0.014132},
											{-0.030202, -0.019950, -0.000659, -0.000770, -0.002035, -0.002382, -0.024284, -0.018793},
											{ 0.107023,  0.068674, -0.015182,  0.002003,  0.002427, -0.016564,  0.080611,  0.101164},
											{-0.185074,  0.113824,  0.012961,  0.036921,  0.034189,  0.020531,  0.106863, -0.190973}
														};
	private final static double hiddenUnitBias = 0.046755;
	private final static double outputBias = 0.051627;
	private final static double outputWeight = -0.608910;
	
	
	/**
	 * Creates a new Net of the structure described in [1].
	 */
	public Net() {
		
		/* Creating the four hidden units. */
		hiddenUnitOne = new Neuron( getWeightVectorOne(), hiddenUnitBias );
		hiddenUnitTwo = new Neuron( getWeightVectorTwo(), hiddenUnitBias );
		hiddenUnitThree = new Neuron( getWeightVectorThree(), hiddenUnitBias );
		hiddenUnitFour = new Neuron( getWeightVectorFour(), hiddenUnitBias );
		
		/* Creating the output neuron. */
		double [] opw = {outputWeight, outputWeight, outputWeight, outputWeight};
		outputUnit = new Neuron( opw, outputBias );
		
	} /* End of constructor Net(). */
	
	
	/**
	 * Calculates the answer of the net to a given input vector. Make sure that the
	 * input vector has the same length as defined for the weights etc.
	 * 
	 * @param input A vector of input values.
	 * @return The answer of the net to that input.
	 * @throws Exception TODO what does it do?
	 */
	public double getOutput( double[] input ) throws Exception {

			return outputUnit.getOutput( new double [] {hiddenUnitOne.getOutput(input), hiddenUnitTwo.getOutput(input), hiddenUnitThree.getOutput(input), hiddenUnitFour.getOutput(input)});

	} /* End of getOutput(). */
	
	
	/**
	 * This generates the 64-elemet vector containing the weights
	 * used by hiddenUnitOne out of the hiddenUnitWeights-matrix.
	 * 
	 * @return 64-element-Vector for hiddenUnitOne.
	 */
	private double [] getWeightVectorOne( ) {
		
		double [] result = new double[ INPUT_LENGTH ];
		int index = 0;
		for ( int row = 0; row < 8; row++ ) {
			
			for ( int col = 0; col < 8; col++ ) {
				
				result[index] = hiddenUnitWeights[row][col];
				index++;
				
			}
			
		}
		return result;
	} /* End of getWeightVectorOne(). */
	
	/**
	 * This generates the 64-elemet vector containing the weights
	 * used by hiddenUnitTwo out of the hiddenUnitWeights-matrix.
	 * 
	 * @return 64-element-Vector for hiddenUnitTwo.
	 */
	private double [] getWeightVectorTwo( ) {
		
		double [] result = new double[ INPUT_LENGTH ];
		int index = 0;
		for ( int col = 0; col < 8; col++ ) {
			
			for ( int row = 0; row < 8; row++ ) {
				
				result[index] = hiddenUnitWeights[row][col];
				index++;
				
			}
			
		}
		return result;
	} /* End of getWeightVectorTwo(). */
	
	
	/**
	 * This generates the 64-elemet vector containing the weights
	 * used by hiddenUnitTwo out of the hiddenUnitWeights-matrix.
	 * 
	 * @return 64-element-Vector for hiddenUnitThree.
	 */
	private double [] getWeightVectorThree( ) {
		
		double [] result = new double[ INPUT_LENGTH ];
		int index = 0;
		for ( int col = 7; col >= 0; col-- ) {
			
			for ( int row = 7; row >= 0; row-- ) {
				
				result[index] = hiddenUnitWeights[row][col];
				index++;
				
			}
			
		}
		return result;
	} /* End of getWeightVectorThree(). */
	
	
	/**
	 * This generates the 64-elemet vector containing the weights
	 * used by hiddenUnitOne out of the hiddenUnitWeights-matrix.
	 * 
	 * @return 64-element-Vector for hiddenUnitFour.
	 */
	private double [] getWeightVectorFour( ) {
		
		double [] result = new double[ INPUT_LENGTH ];
		int index = 0;
		for ( int row = 7; row >= 0; row-- ) {
			
			for ( int col = 7; col >= 0; col-- ) {
				
				result[index] = hiddenUnitWeights[row][col];
				index++;
				
			}
			
		}
		return result;
	} /* End of getWeightVectorFour(). */
	
	
	/**
	 * For now just performing some tests...
	 * 
	 * @param args
	 */
	public static void main( String [] args ) {
		
		double output = 0;
		
		/* Creating a new Net. */
		Net theNet = new Net();
		
		try {
			
			/* Testing the output with 100 random input vectors. */
			for ( int i = 0; i <= 100; i++ ) {
				output = theNet.getOutput( Net.genRandomInput(64) );

				System.out.println("(II) Answer of outputUnit is: " + output);
			}
			
			/* FIXME: The range of outputs is between 0.5 and 1 and never smaller
			 * than 0.5. I expected values between 0 and 1.
			 */
			
		} catch ( Exception e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		}
		
	} /* End of main(). */
	
	
	/**
	 * Generates a vector of a specified length filled with random values
	 * -1, 0, 1. So you could say it generates random game board situations.
	 * 
	 * @param length The number of elements that the array should contain.
	 * @return The array filled with random numbers.
	 */
	private static double[] genRandomInput( int length ) {
		
		Random rnd = new Random();

		double [] input = new double [length];
		double temp;
		
		/* Generate a random input vector. */
		for ( int j = 0; j<length; j++ ) {

			/* Input vector should contain -1,0,1, like it would be in a game. */
			temp = (int)rnd.nextInt(3);
			if ( temp == 2 ) {
				input[j]= -1;
			} else {
				input[j] = temp;
			}
		}
		
		return input;
	} /* End of genRandomInput(). */
	
} /* End of class Net. */
