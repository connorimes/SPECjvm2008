
package spec.reporter;

import java.io.File;

public class Reporter {

    public static void usage() {
        System.out.println("java spec.reporter.Reporter <raw file>");
        System.out.println("java spec.reporter.Reporter <raw file> [-html <bool>] [-txt <bool>] [-summary <bool>] [-sub <bool>]");
        System.out.println("java spec.reporter.Reporter --prepare <base raw file> <optional peak raw file>");
        System.out.println("java spec.reporter.Reporter --specprocess <merged raw file>");
        return;
    }

    public static String main2(String[] args) throws Exception {

        if (args.length < 1 || args[0].equalsIgnoreCase("--help")) {
            usage();
            return "";
        }

        if (args.length > 1 && args[0].equalsIgnoreCase("--prepare")) {
            String [] args2 = new String[args.length - 1];
            System.arraycopy(args, 1, args2, 0, args2.length);
            SPECSubmissionPreparer.main(args2);
            return "";
        }

        if (args.length > 1 && args[0].equalsIgnoreCase("--specprocess")) {
            String [] args2 = new String[args.length - 1];
            System.arraycopy(args, 1, args2, 0, args2.length);
            SPECSubmissionProcessor.main(args2);
            return "";
        }

        // Handle (consume) reporter invocation args -r and --reporter
        if (args.length >= 1 && (args[0].equalsIgnoreCase("--reporter") || args[0].equalsIgnoreCase("-r"))) {
            String [] args2 = new String[args.length - 1];
            System.arraycopy(args, 1, args2, 0, args2.length);
            args = args2;
        }

        if (args.length >= 1) {
            
            if (!(new File(args[0]).exists())) {
                System.out.println("Cannot find file '" + args[0] + "'");
                usage();
                return "";
            }
            return ReportGenerator.main2(args);
        }
        
        usage();
        return "";
    }

    public static void main(String[] args) throws Exception {
        main2(args);
    }
}
