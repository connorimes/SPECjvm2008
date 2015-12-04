/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 *
 * Note:
 * This program can be used outside the SPEC benchmark harness to test
 * your own floating point accuracy. SPEC does not consider 80-bit
 * intermediate values to be invalid. However there are other floating
 * point bugs that may occur in JVM's. By running this program and
 * comparing with the expected results below you can increase your
 * confidence that your JVM does not suffer from such bugs:
 
$ java spec.benchmarks.check.FloatingPointCheck
FloatingPointCheck
        tiny:             1.0E-323
        tiny/small:       0.0
        (tiny/small)*big: 0.0
        MIN_VALUE*big:    1.265E-321
        (tiny*big)/small: 3.16E-322
        huge:             8.988465674311579E307
        huge*small:       Infinity
        (huge*small)/big: Infinity
        eps:    1.1102230246251565E-16
        eps:    0x3ca0000000000000
        1+eps:  1.0
        (1+eps)-1:      0.0
        tiny:             2.8E-45
        tiny/small:       0.0
        (tiny/small)*big: 0.0
        MIN_VALUE*big:    3.59E-43
        (tiny*big)/small: 9.0E-44
        huge:             1.7014117E38
        huge*small:       Infinity
        (huge*small)/big: Infinity
        eps:    5.9604645E-8
        eps:    0x33800000
        1+eps:  1.0
        (1+eps)-1:      0.0
OK
 
 * End of sample execution
 */

package spec.benchmarks.check;
import spec.harness.Context;

public class FloatingPointCheck {
    
    //  Level of print output produced
    public static boolean verbose = true;
    
    // global error flag
    private static boolean error = false;
    
    //	Are these susceptible to constant folding and propagation?
    //	If so, maybe get them from a hash table, instead of variables.
    
    public static final double doubleTiny = Double.MIN_VALUE + Double.MIN_VALUE;
    public static final double doubleSmall = 8.0;
    public static final double doubleBig = 256.0;
    public static final double doubleHuge = Double.MAX_VALUE / 2.0;
    
    public static final float floatTiny = Float.MIN_VALUE + Float.MIN_VALUE;
    public static final float floatSmall = 8.0f;
    public static final float floatBig = 256.0f;
    public static final float floatHuge = Float.MAX_VALUE / 2.0f;
    
    public static final double doubleOne = 1.0;
    public static final double doubleEpsilon = Math.pow(2.0, -53.0);
    
    public static final float floatOne = 1.0f;
    public static final float floatEpsilon = (float) Math.pow(2.0, -24.0);
    
    public static void doubleRange(boolean print){
        // First test:
        //	The value tiny is near the minimum representable double.
        //	Dividing tiny by small should get us a value
        //	that is not representable.
        //	Page 35 says the result should be "the representable value
        //	nearest to the the infinitely precise result".
        //	The nearest representable value is 0.0, being 4 times
        //	nearer than Double.MIN_VALUE.
        //	Multiply that by big and you should still get 0.0.
        //	What you should not get is the extended-precision
        //	value of (tiny/small) multiplied by big.
        //	Note that the computation of tiny/small should
        //	yield 0.0, whether it is an intermediate result or a
        //	stored value.
        double check = (doubleTiny / doubleSmall) * doubleBig;
        // Validate the result
        if (check != 0.0) {
            error = true;
            if (print) {
                Context.getOut().println("Ooops: double range too big near 0");
            }
        }
        
        if (print && verbose) {
            // Some diagnostics
            Context.getOut().println("	tiny:             " +
                    doubleTiny);
            Context.getOut().println("	tiny/small:       " +
                    (doubleTiny/doubleSmall));
            Context.getOut().println("	(tiny/small)*big: " +
                    ((doubleTiny/doubleSmall)*doubleBig));
            Context.getOut().println("	MIN_VALUE*big:    " +
                    (Double.MIN_VALUE*doubleBig));
            Context.getOut().println("	(tiny*big)/small: " +
                    ((doubleTiny*doubleBig)/doubleSmall));
        }
        
        // Second test:
        //	Huge is near the maximum representable double.
        //	Multiplying huge by small should get us a value that
        //	is not representable, so should be Double.POSITIVE_INFINITY.
        //	Divide that by big and you should still get Infinity.
        //	What you should not get is the extended-precision
        //	value of (huge*small) divided by big.
        //
        check = ( doubleHuge * doubleSmall ) / doubleBig;
        if ( check != Double.POSITIVE_INFINITY ) {
            error = true;
            if (print) {
                Context.getOut().println("Ooops: double range too big near infinity");
            }
        }
        if (print && verbose) {
            // Some diagnostics
            Context.getOut().println("	huge:             " +
                    doubleHuge);
            Context.getOut().println("	huge*small:       " +
                    (doubleHuge*doubleSmall));
            Context.getOut().println("	(huge*small)/big: " +
                    ((doubleHuge*doubleSmall)/doubleBig));
        }
    }
    
    public static void floatRange(boolean print){
        /* This is analogous to doubleRange in every way. */
        float check = (floatTiny / floatSmall) * floatBig;
        // Validate the result
        if (check != 0.0) {
            error = true;
            if (print) {
                Context.getOut().println("Ooops: float range too big near 0");
            }
        }
        
        if (print && verbose) {
            // Some diagnostics
            Context.getOut().println("	tiny:             " +
                    floatTiny);
            Context.getOut().println("	tiny/small:       " +
                    (floatTiny/floatSmall));
            Context.getOut().println("	(tiny/small)*big: " +
                    ((floatTiny/floatSmall)*floatBig));
            Context.getOut().println("	MIN_VALUE*big:    " +
                    (Float.MIN_VALUE*floatBig));
            Context.getOut().println("	(tiny*big)/small: " +
                    ((floatTiny*floatBig)/floatSmall));
        }
        
        check = ( floatHuge * floatSmall ) / floatBig;
        if ( check != Float.POSITIVE_INFINITY ) {
            error = true;
            if (print) {
                Context.getOut().println("Ooops: float range too big near infinity");
            }
        }
        if (print && verbose) {
            // Some diagnostics
            Context.getOut().println("	huge:             " +
                    floatHuge);
            Context.getOut().println("	huge*small:       " +
                    (floatHuge*floatSmall));
            Context.getOut().println("	(huge*small)/big: " +
                    ((floatHuge*floatSmall)/floatBig));
        }
    }
    
    public static void doublePrecision(boolean print){
        // 1.0 + (2^-53) is a number which requires
        // 54 bits of precision, including a hidden high-order
        // bit. This is more than a double result is allowed to
        // have. Rounding this sum to the nearest double gives 1.0.
        // Subtracting 1.0 from the sum should result in 0.0.
        //
        double check = ( doubleOne + doubleEpsilon ) - doubleOne;
        if (check != 0.0) {
            error = true;
            if (print) {
                Context.getOut().println("Ooops: double precision too big near 1");
            }
        }
        if (print && verbose) {
            //  Some diagnostics
            Context.getOut().println("	eps:	" +
                    doubleEpsilon );
            Context.getOut().println("	eps:	0x" +
                    Long.toString(Double.doubleToLongBits(doubleEpsilon), 16));
            Context.getOut().println("	1+eps:	" +
                    (doubleOne+doubleEpsilon) );
            Context.getOut().println("	(1+eps)-1:	" +
                    ((doubleOne+doubleEpsilon)-doubleOne) );
        }
    }
    
    public static void floatPrecision(boolean print){
        // 1.0 + (2^-24) is a number which requires
        // 25 bits of precision, including a hidden high-order
        // bit. This is more than a float result is allowed to
        // have. Rounding this sum to the nearest float gives 1.0.
        // Subtracting 1.0 from the sum should result in 0.0.
        //
        //
        float check = ( floatOne + floatEpsilon ) - floatOne;
        if (check != 0.0) {
            error = true;
            if (print) {
                Context.getOut().println("Ooops: float precision too big near 1");
            }
        }
        if (print && verbose) {
            //  Some diagnostics
            Context.getOut().println("	eps:	" +
                    floatEpsilon );
            Context.getOut().println("	eps:	0x" +
                    Integer.toString(Float.floatToIntBits(floatEpsilon), 16));
            Context.getOut().println("	1+eps:	" +
                    (floatOne+floatEpsilon) );
            Context.getOut().println("	(1+eps)-1:	" +
                    ((floatOne+floatEpsilon)-floatOne) );
        }
    }
    
    public static void test(boolean print) {
        doubleRange(print);
        doublePrecision(print);
        floatRange(print);
        floatPrecision(print);
    }
    
    public static void run(int length) {
        // Run it for a while to make sure things are compiled by adaptive compilers
        // It's most important for a compliant 100% run, so run with the
        // full "warm-up" count for that and correspondingly less for
        // smaller sizes. This means it's possible that an adaptive
        // compiler with a floating point bug might pass at say 1% where
        // it hadn't fully compiled everything, but then fail at 100%
        // after it compiled erroneously
        Context.getOut().println("FloatingPointCheck");
        int count;
        for (count = 0; count < 1*10000*length; count += 1) {
            test(false);
        }
        // Run it once to get the answers
        test(true);
        if (error)
            Context.getOut().println("Error");
        else
            Context.getOut().println("OK");
    }
    
    public static void main(String[] args) {
        run(100);
    }
    
}
