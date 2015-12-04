/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness.results;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import spec.harness.Constants;

public abstract class TestResult {
    
    // type of run
    public final static int ITER = 1;
    public final static int TIMED = 2;
    
    protected List<String> errors;
    
    private final static DecimalFormat df = new DecimalFormat("#.##");
    public final static int TAB = 2;
    
    public final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n";
    
    static {
        df.setDecimalSeparatorAlwaysShown(true);
        df.setMinimumFractionDigits(2);
    }
    
    /**
     * Print result as xml on given string.
     */
    public abstract void toXml(PrintStream ps, int level);
    
    
    /**
     * Check if result is valid.
     */
    public abstract boolean isValid();
    
    public abstract List<String> getAllErrors(String tag);
    
    public List getErrors() {
        return errors;
    }
    
    public void addError(String error) {
        if (errors == null) {
            errors = new LinkedList<String>();
        }
        errors.add(error);
    }
    
    public boolean hasErrors() {
        return this.errors != null;
    }
        
    protected void printErrorsToXml(PrintStream ps, int indent) {
        if (hasErrors()) {
            ptxElementOpen(ps, indent, Constants.ERRORS_ENAME);
            for (Iterator iter = errors.iterator(); iter.hasNext();) {
                ptxElement(ps, indent + TAB, Constants.ERROR_ENAME, (String) iter.next());
            }
            ptxElementClose(ps, indent, Constants.ERRORS_ENAME);
        }
    }
    
    protected void ptxAttrib(PrintStream ps, int indent, String name, String value, boolean condition, String ending) {
        if (condition) {
            ps.print(getIndent(indent) + name + "=\"" + value + "\"" + ending);
        }
    }
    
    protected void ptxAttrib(PrintStream ps, int indent, String name, long value, boolean condition, String ending) {
        ptxAttrib(ps, indent, name, Long.toString(value), condition, ending);
    }
    
    protected void ptxAttrib(PrintStream ps, int indent, String name, double value, boolean condition, String ending) {
        ptxAttrib(ps, indent, name, Double.toString(value), condition, ending);
    }
    
    protected void ptxAttrib(PrintStream ps, int indent, String name, boolean value, boolean condition, String ending) {
        ptxAttrib(ps, indent, name, Boolean.toString(value), condition, ending);
    }
    
    protected void ptxAttrib(PrintStream ps, int indent, String name, String [] value, boolean condition, String ending) {
        ptxAttrib(ps, indent, name, ptxArrToValue(value), condition, ending);
    }
        
    protected void ptxElement(PrintStream ps, int indent, String name, String value) {
        ps.print(getIndent(indent) + "<" + name + ">" + correctContent(value, true)
        + "</" + name + ">\n");
    }
    
    protected void ptxElementOpen(PrintStream ps, int indent, String name) {
        ps.print(getIndent(indent) + "<" + name + ">\n");
    }
    
    protected void ptxElementStartOpen(PrintStream ps, int indent, String name, boolean linebreak) {
        ps.print(getIndent(indent) + "<" + name + (linebreak ? "\n" : " "));
    }
    
    protected void ptxElementClose(PrintStream ps, int indent, String name) {
        ps.print(getIndent(indent) + "</" + name + ">\n");
    }
        
    private String ptxArrToValue(String[] values) {
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            value.append(values[i]);
            if (i + 1 < values.length) {
                value.append(" ");
            }
        }
        return value.toString();
    }
    
    protected void ptxXmlHeader(PrintStream ps) {
        ps.print(XML_HEADER);
    }
    
    public static String getRunModeDescription(int mode) {
        return (mode == TestResult.ITER ? "static run" : (mode == TestResult.TIMED ? "timed run" : "unknown"));
    }
    
    public static String doubleAsRes(double d) {
        return df.format(d);
    }
    
    public static String millisAsSec(long time) {
        return (time / 1000) + "s";
    }
    
    protected static String getIndent(int indent) {
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < indent; i++) {
            ret.append(" ");
        }
        return ret.toString();
    }
    
    public static final String correctContent(String content, boolean doApos) {
        boolean needCorrection = content.matches(".*(<|>|'|\"|&|)+.*") || content.indexOf("\n") > 0;
        if (!needCorrection) {
            return content;
        }
        
        StringBuilder result = new StringBuilder();
        char current = 'a';
        char prev;
        for (int i = 0; i < content.length(); i ++) {
        	prev = current;
            current = content.charAt(i);
            if (prev == '\\') {
            	// Escape this character
            	result.append(current);
            	continue;
            }
            switch (current) {
            	case '\\':
                    // Eat escape character
            		// result.append("&amp;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '\'':
                    if (doApos) {
                        result.append("&apos;");
                    } else {
                        result.append(current);
                    }
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                default:
                    result.append(current);
                    break;
            }
        }
        return result.toString();
    }
}
