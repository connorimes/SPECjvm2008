/*
 * 
 * Copyright (c) 2000-2008 Standard Performance Evaluation Corporation (SPEC) All
 * rights reserved. Copyright (c) 2000-2008 Hewlett-Packard All rights reserved.
 * Copyright (c) 1997-2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * This source code is provided as is, without any express or implied warranty.
 *  
 */

package spec.validity;

import java.io.*;
import java.lang.reflect.Constructor;
import java.security.*;
import java.util.*;

import spec.harness.Context;
import spec.harness.StopBenchmarkException;

public class Digests {

    boolean debug;
    PrintStream out;

    public Digests(PrintStream out) {
        this.debug = false;
        this.out = out;
    }

    public String crunch_jars() {
        String prefix = Context.getSpecBasePath() + "/";
        String ret = crunch("Jar", prefix, 1);
        return ret;
    }

    public String crunch_resources() {
        String prefix = Context.getSpecBasePath() + "/resources/";
        String ret = crunch("Resource", prefix, 5);
        return ret;
    }

    private String crunch(String target, String prefix, int step) {

        DigestDefinition digests = createDigester(target);

        // Get iterator of resource filenames to check
        Iterator<String> iter = digests.iterator();

        String ret = null;

        for (int s = 0; iter.hasNext(); s++) {
            String res = null;
            String current = iter.next();
            String current_name = new String(prefix + current);

            try {
                FileInputStream the_one = new FileInputStream(current_name);

                byte[] expected = digests.getArray(current);
                MessageDigest md = MessageDigest.getInstance("SHA");
                DigestInputStream dis = new DigestInputStream(the_one, md);
                int count = (int) (new File(current_name).length());
                for (int i = 0; i < count; i++) {
                    dis.read();
                }
                byte a[] = md.digest();
                for (int i = 0; i < 10; i++) {
                    if (debug) {
                        System.out.println(", " + a[i]);
                    }
                    if (a[i] != expected[i]) {
                        res = "Checksum test failed on " + current;
                    }
                }
                if (debug) {
                    System.out.println(current + " validity is " + (res == null));
                }
            } catch (Exception e) {
                res = "Digests: caught exception " + e.getMessage();
            }

            if (res != null) {
                if (ret == null) {
                    ret = res;
                } else {
                    ret += ", " + res;
                }
                out.print('!');
            } else {
            	if (s % step == 0) {
            		out.print('.');
            	}
            }
        }
        return ret;
    }

    /**
     * This helper method creates the digester for either jars or resources. It
     * exists, since the digesters are generated after the build is done and
     * cannot be invoked by the code directly in the source depot.
     */
    @SuppressWarnings(value = { "unchecked" })
    private DigestDefinition createDigester(String type) {
        try {
            Class cls = Class.forName("spec.validity.Expected" + type + "Digests");
            Constructor constr = cls.getConstructor((Class[]) null);
            DigestDefinition jarDigests = (DigestDefinition) constr.newInstance((Object[]) null);
            return jarDigests;
        } catch (Exception e) {
            throw new StopBenchmarkException("Failed to create checksum digester.", e);
        }
    }
}
