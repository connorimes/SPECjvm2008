/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.scimark.sparse;

import spec.benchmarks.scimark.utils.Constants;
import spec.benchmarks.scimark.utils.Random;
import spec.benchmarks.scimark.utils.Stopwatch;
import spec.benchmarks.scimark.utils.kernel;

public class SparseCompRow {
    int id;
    
    public SparseCompRow(int id) {
        this.id = id;
    }
    
    public static void main(int id) {
        SparseCompRow sparse= new SparseCompRow(id);
        sparse.run();
    }
        /* multiple iterations used to make kernel have roughly
                same granulairty as other Scimark kernels. */
    
    public double num_flops(int N, int nz, int num_iterations) {
                /* Note that if nz does not divide N evenly, then the
                   actual number of nonzeros used is adjusted slightly.
                 */
        int actual_nz = (nz/N) * N;
        return ((double)actual_nz) * 2.0 * ((double) num_iterations);
    }
    
    
        /* computes  a matrix-vector multiply with a sparse matrix
                held in compress-row format.  If the size of the matrix
                in MxN with nz nonzeros, then the val[] is the nz nonzeros,
                with its ith entry in column col[i].  The integer vector row[]
                is of size M+1 and row[i] points to the begining of the
                ith row in col[].
         */
    
    public void matmult( double y[], double val[], int row[],
            int col[], double x[], int NUM_ITERATIONS, int call_count) {
        double total = 0.0;
        int M = row.length - 1;
        for (int reps=0; reps<NUM_ITERATIONS; reps++) {
            
            for (int r=0; r<M; r++) {
                double sum = 0.0;
                int rowR = row[r];
                int rowRp1 = row[r+1];
                for (int i=rowR; i<rowRp1; i++)
                    sum += x[ col[i] ] * val[i];
                y[r] = sum;
            }
        }
        if(call_count==1){
            for(int i=0;i < y.length;i++)total += y[i];
            kernel.checkResults(kernel.CURRENT_SPARSE_RESULT, "" + total, id);
        }
        
        
    }
    public double measureSparseMatmult(int N, int nz,
            double min_time, Random R) {
        // initialize vector multipliers and storage for result
        // y = A*y;
        
        double x[] = kernel.RandomVector(N, R);
        double y[] = new double[N];
        
        // initialize square sparse matrix
        //
        // for this test, we create a sparse matrix wit M/nz nonzeros
        // per row, with spaced-out evenly between the begining of the
        // row to the main diagonal.  Thus, the resulting pattern looks
        // like
        //             +-----------------+
        //             +*                +
        //             +***              +
        //             +* * *            +
        //             +** *  *          +
        //             +**  *   *        +
        //             +* *   *   *      +
        //             +*  *   *    *    +
        //             +*   *    *    *  +
        //             +-----------------+
        //
        // (as best reproducible with integer artihmetic)
        // Note that the first nr rows will have elements past
        // the diagonal.
        
        int nr = nz/N; 		// average number of nonzeros per row
        int anz = nr *N;   // _actual_ number of nonzeros
        
        
        double val[] = kernel.RandomVector(anz, R);
        int col[] = new int[anz];
        int row[] = new int[N+1];
        
        row[0] = 0;
        for (int r=0; r<N; r++) {
            // initialize elements for row r
            
            int rowr = row[r];
            row[r+1] = rowr + nr;
            int step = r/ nr;
            if (step < 1) step = 1;   // take at least unit steps
            
            
            for (int i=0; i<nr; i++)
                col[rowr+i] = i*step;
            
        }
        
        Stopwatch Q = new Stopwatch();
        
        // Cycles set to integrate into SPECjvm2008 benchmark harness.  Testing done on
        // Apple Macbook Pro 2.0Ghz Intel Core Duo, 1GB 667mhz SODIMM
        
        int cycles=512;
        int count = 1;
        //while(true)
        //{
        Q.start();
        matmult(y, val, row, col, x, cycles, count++);
        Q.stop();
        
        return num_flops(N, nz, cycles) / Q.read() * 1.0e-6;
    }
    
    public void run() {
        // default to the (large) cache-contained version
        
        double min_time = Constants.RESOLUTION_DEFAULT;
        int Sparse_size_M = kernel.CURRENT_SPARSE_SIZE_M;
        int Sparse_size_nz = kernel.CURRENT_SPARSE_SIZE_nz;
        // run the benchmark
        
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        res = measureSparseMatmult( Sparse_size_M,
                Sparse_size_nz, min_time, R);
        
        
        
        //System.out.println("Sparse matmult (N="+ Sparse_size_M+
        //                   ", nz=" + Sparse_size_nz + "): " + res);
        
        
        
    }
    
}
