/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.scimark.utils;

import spec.harness.Context;
import spec.harness.Util;

public class kernel {
    // each measurement returns approx Mflops
    
    public static double[] NewVectorCopy(double x[]) {
        int N = x.length;
        
        double y[] = new double[N];
        for (int i=0; i<N; i++)
            y[i] = x[i];
        
        return y;
    }
    
    public static void CopyVector(double B[], double A[]) {
        int N = A.length;
        
        for (int i=0; i<N; i++)
            B[i] = A[i];
    }
    
    
    public static double normabs(double x[], double y[]) {
        int N = x.length;
        double sum = 0.0;
        
        for (int i=0; i<N; i++)
            sum += Math.abs(x[i]-y[i]);
        
        return sum;
    }
    
    public static void CopyMatrix(double B[][], double A[][]) {
        int M = A.length;
        int N = A[0].length;
        
        int remainder = N & 3;		 // N mod 4;
        
        for (int i=0; i<M; i++) {
            double Bi[] = B[i];
            double Ai[] = A[i];
            for (int j=0; j<remainder; j++)
                Bi[j] = Ai[j];
            for (int j=remainder; j<N; j+=4) {
                Bi[j] = Ai[j];
                Bi[j+1] = Ai[j+1];
                Bi[j+2] = Ai[j+2];
                Bi[j+3] = Ai[j+3];
            }
        }
    }
    
    public static double[][] RandomizeMatrix(double[][] A, Random R) {
        //double A[][] = new double[M][N];
        for (int i=0; i<A.length; i++)
            for (int j=0; j<A[i].length; j++)
                A[i][j] = R.nextDouble();
        return A;
    }
    
    public static double[][] RandomMatrix(int M, int N, Random R) {
        double A[][] = new double[M][N];
        
        for (int i=0; i<N; i++)
            for (int j=0; j<N; j++)
                A[i][j] = R.nextDouble();
        return A;
    }
    
    public static double[] RandomVector(int N, Random R) {
        double A[] = new double[N];
        
        for (int i=0; i<N; i++)
            A[i] = R.nextDouble();
        return A;
    }
    
    public static double[] RandomizeVector(double[] A, Random R) {
        //double A[] = new double[N];
        
        for (int i=0; i<A.length; i++)
            A[i] = R.nextDouble();
        return A;
    }
    
    public static double[] matvec(double A[][], double x[]) {
        int N = x.length;
        double y[] = new double[N];
        
        matvec(A, x, y);
        
        return y;
    }
    
    public static void matvec(double A[][], double x[], double y[]) {
        int M = A.length;
        int N = A[0].length;
        
        for (int i=0; i<M; i++) {
            double sum = 0.0;
            double Ai[] = A[i];
            for (int j=0; j<N; j++)
                sum += Ai[j] * x[j];
            
            y[i] = sum;
        }
    }
    
    public static int CURRENT_FFT_SIZE = Constants.DEFAULT_FFT_SIZE;
    public static int CURRENT_SOR_SIZE = Constants.DEFAULT_SOR_SIZE;
    public static int CURRENT_SPARSE_SIZE_M = Constants.DEFAULT_SPARSE_SIZE_M;
    public static int CURRENT_SPARSE_SIZE_nz = Constants.DEFAULT_SPARSE_SIZE_nz;
    public static int CURRENT_LU_SIZE = Constants.DEFAULT_LU_SIZE;
    
    public static String CURRENT_FFT_RESULT = Constants.DEFAULT_FFT_RESULT;
    public static String CURRENT_LU_RESULT = Constants.DEFAULT_LU_RESULT;
    public static String CURRENT_SOR_RESULT = Constants.DEFAULT_SOR_RESULT;
    public static String CURRENT_SPARSE_RESULT = Constants.DEFAULT_SPARSE_RESULT;
    
    public static int LU_LOOPS = 1;
    public static int FFT_LOOPS = 1;
    public static int SOR_LOOPS = 1;
    public static int SPARSE_LOOPS = 1;
    public static int MC_LOOPS = 10;  
    
    public static void init() {
        String currentDataset = Util.getProperty(spec.harness.Constants.SCIMARK_SIZE_PROP,
                Constants.DEFAULT_DATASET_NAME);
        if (Constants.SMALL_DATASET_NAME.equals(currentDataset)) {
            CURRENT_FFT_SIZE = Constants.SMALL_FFT_SIZE;
            CURRENT_SOR_SIZE = Constants.SMALL_SOR_SIZE;
            CURRENT_SPARSE_SIZE_M = Constants.SMALL_SPARSE_SIZE_M;
            CURRENT_SPARSE_SIZE_nz = Constants.SMALL_SPARSE_SIZE_nz;
            CURRENT_LU_SIZE = Constants.SMALL_LU_SIZE;
            
            CURRENT_FFT_RESULT = Constants.SMALL_FFT_RESULT;
            CURRENT_LU_RESULT = Constants.SMALL_LU_RESULT;
            CURRENT_SOR_RESULT = Constants.SMALL_SOR_RESULT;
            CURRENT_SPARSE_RESULT = Constants.SMALL_SPARSE_RESULT;
            
            LU_LOOPS = 64;
            FFT_LOOPS = 64;
            SOR_LOOPS = 16;
            SPARSE_LOOPS = 16;
            // Size does not apply to Monte carlo 
            
        } else if (Constants.LARGE_DATASET_NAME.equals(currentDataset)) {
            CURRENT_FFT_SIZE = Constants.LARGE_FFT_SIZE;
            CURRENT_SOR_SIZE = Constants.LARGE_SOR_SIZE;
            CURRENT_SPARSE_SIZE_M = Constants.LARGE_SPARSE_SIZE_M;
            CURRENT_SPARSE_SIZE_nz = Constants.LARGE_SPARSE_SIZE_nz;
            CURRENT_LU_SIZE = Constants.LARGE_LU_SIZE;
            
            CURRENT_FFT_RESULT = Constants.LARGE_FFT_RESULT;
            CURRENT_LU_RESULT = Constants.LARGE_LU_RESULT;
            CURRENT_SOR_RESULT = Constants.LARGE_SOR_RESULT;
            CURRENT_SPARSE_RESULT = Constants.LARGE_SPARSE_RESULT;
            
            LU_LOOPS = 1;
            FFT_LOOPS = 1;
            SOR_LOOPS = 1;
            SPARSE_LOOPS = 1;
            // Size does not apply to Monte carlo 
            
        } else {
            CURRENT_FFT_SIZE = Constants.DEFAULT_FFT_SIZE;
            CURRENT_SOR_SIZE = Constants.DEFAULT_SOR_SIZE;
            CURRENT_SPARSE_SIZE_M = Constants.DEFAULT_SPARSE_SIZE_M;
            CURRENT_SPARSE_SIZE_nz = Constants.DEFAULT_SPARSE_SIZE_nz;
            CURRENT_LU_SIZE = Constants.DEFAULT_LU_SIZE;
            
            CURRENT_FFT_RESULT = Constants.DEFAULT_FFT_RESULT;
            CURRENT_LU_RESULT = Constants.DEFAULT_LU_RESULT;
            CURRENT_SOR_RESULT = Constants.DEFAULT_SOR_RESULT;
            CURRENT_SPARSE_RESULT = Constants.DEFAULT_SPARSE_RESULT;
            
            LU_LOOPS = 1;
            FFT_LOOPS = 1;
            SOR_LOOPS = 1;
            SPARSE_LOOPS = 1;
            // Size does not apply to Monte carlo 
        }
    }
    
    
    public static final void checkResults(String expectedValue, String gottenValue, int loop) {
        if (expectedValue.equals(gottenValue)) {
            if (loop == 1) {
                Context.getOut().println(Constants.VERIFICATION_PASSED_MESSAGE);
            }
        } else {
            Context.getOut().println(Constants.VERIFICATION_FAILED_MESSAGE
                    + "The result is "  + gottenValue + "  instead of " + expectedValue);
        }
    }
}
