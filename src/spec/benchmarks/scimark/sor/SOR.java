/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.scimark.sor;

import spec.benchmarks.scimark.utils.Constants;
import spec.benchmarks.scimark.utils.Random;
import spec.benchmarks.scimark.utils.Stopwatch;
import spec.benchmarks.scimark.utils.kernel;

public class SOR {
    int id;
    
    public SOR(int id) {
        this.id = id;
    }
    
    public static void main(int id) {
        SOR sor = new SOR(id);
        sor.run();
    }
    public final double num_flops(int M, int N, int num_iterations) {
        double Md = (double) M;
        double Nd = (double) N;
        double num_iterD = (double) num_iterations;
        
        return (Md-1)*(Nd-1)*num_iterD*6.0;
    }
    
    public final double execute(double omega, double G[][], int
            num_iterations) {
        int M = G.length;
        int N = G[0].length;
        
        double omega_over_four = omega * 0.25;
        double one_minus_omega = 1.0 - omega;
        double [] Gi = null;
        double Gi_Sum = 0.0;
        // update interior points
        //
        int Mm1 = M-1;
        int Nm1 = N-1;
        for (int p=0; p<num_iterations; p++) {
            for (int i=1; i<Mm1; i++) {
                Gi = G[i];
                double[] Gim1 = G[i-1];
                double[] Gip1 = G[i+1];
                for (int j=1; j<Nm1; j++)
                    Gi[j] = omega_over_four * (Gim1[j] + Gip1[j] + Gi[j-1]
                            + Gi[j+1]) + one_minus_omega * Gi[j];
            }
        }
        for(int k=0;k<Gi.length;k++)Gi_Sum += Gi[k];
        
        return Gi_Sum;
    }
    
    private static final ThreadLocal <double[][]> threadLocalMatrix =
            new ThreadLocal<double[][]> () {
        @Override protected double[][] initialValue() {
            return new double[kernel.CURRENT_SOR_SIZE][kernel.CURRENT_SOR_SIZE];
        }
    };
    public double measureSOR(int N, double min_time, Random R) {
        double G[][];
        G = threadLocalMatrix.get();
        if(G.length != N){
            System.out.println("G.length: " + G.length + " N: " + N);
            G = new double[N][N];
            threadLocalMatrix.set(G);
        }
        G = kernel.RandomizeMatrix(G, R);
        
        Stopwatch Q = new Stopwatch();
        int cycles=256;
        //while(true)
        //{
        Q.start();
        double x = execute(1.25, G, cycles);
        Q.stop();
        //	if (Q.read() >= min_time) break;
        
        //	cycles *= 2;
        //}
        //System.out.println("SOR cycles = " + cycles);
        // approx Mflops
        kernel.checkResults(kernel.CURRENT_SOR_RESULT, "" + x, id);
        return num_flops(N, N, cycles) / Q.read() * 1.0e-6;
    }
    
    public void run() {
        // default to the (small) cache-contained version
        
        double min_time = Constants.RESOLUTION_DEFAULT;
        int SOR_size =  kernel.CURRENT_SOR_SIZE;
        // run the benchmark
        
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        res = measureSOR( SOR_size, min_time, R);
        
        
        
        
        
    }
}

