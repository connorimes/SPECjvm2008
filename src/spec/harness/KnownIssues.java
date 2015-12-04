package spec.harness;

public class KnownIssues {
    
    /** Known issues flag, to report better error messages. */
    public static boolean isKnownIssueJavacVersion = false;
    public static boolean isKnownIssueXmlTransformRace = false;
    public static boolean isKnownIssueOutOfMemoryError = false;

    private final static String fs = System.getProperty("file.separator");

	protected static void printKnownIssuesInfo() {
		
		if (isKnownIssueJavacVersion || isKnownIssueXmlTransformRace || isKnownIssueOutOfMemoryError) {
			System.out.println();
			System.out.println("--- --- --- --- --- --- --- --- ---");
			System.out.println();
			System.out.println("WARNING! Known issue detected:");
			System.out.println();
		}
		
		if (isKnownIssueJavacVersion) {
			printKnownIssueJavacVersion();
		}
		
		if (isKnownIssueXmlTransformRace) {
			printKnownIssueXmlTransformRace();
		}
		
		if (isKnownIssueOutOfMemoryError) {
			printKnownIssueutOfMemoryError();
		}
	}

    
	protected static void printKnownIssueXmlTransformRace() {
		System.out.println();
		Context.getOut().println("The XML transform benchmark failed in validation of the result.");
        Context.getOut().println("This may be due to a known race in the XML library included in the 5.0 JRE.");
        Context.getOut().println("For more info, see " + Launch.specjvmHomeDir + fs + "docs" + fs + "KnownIssues.html");
	}


	protected static void printKnownIssueJavacVersion() {
		Context.getOut().println();
        Context.getOut().println("The Javac version test in check failed.");
        Context.getOut().println("The Javac version must be the one included in SPECjvm2008.");
        Context.getOut().println("There is a known issue with this for Java on Mac OS X, including a workaround.");
        Context.getOut().println("For more info, see " + Launch.specjvmHomeDir + fs + "docs" + fs + "KnownIssues.html");
	}
	
	protected static void printKnownIssueutOfMemoryError() {
		Context.getOut().println();
        Context.getOut().println("An Out of Memory Error has been thrown.");
        Context.getOut().println("This is an unfortunate but expected error for some configurations.");
        Context.getOut().println("The heap size needs to be increased or the amount of live data decreased.");
		Context.getOut().println();
        Context.getOut().println("In a peak run where tuning is allowed, use JVM argument -Xmx or equivalent.");
        Context.getOut().println("In a base run, it is possible to configure the harness.");
        Context.getOut().println("For more info, see " + Launch.specjvmHomeDir + fs + "docs" + fs + "KnownIssues.html");
	}

}
