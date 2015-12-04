package spec.benchmarks.xml.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import spec.harness.Context;

class BaseOutputStream extends OutputStream {
    private long crc = 1;
    Properties validiationProperties;
    String propertyName;
    
    public void setCurrentProp(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public String getCurrentProp() {
        return propertyName;
    }
    
    public void write(int b) throws IOException {
        crc = crc * 33 + b;
    }
    
    
    public void write(byte[] b) throws IOException {
        for (int i = 0; i < b.length; i ++) {
            crc = crc * 33 + b[i];
        }
    }
    
    
    public void write(byte[] b, int offset, int len) throws IOException {
        for (int i = offset; i < offset + len; i ++) {
            crc = crc * 33 + b[i];
        }
    }
    
    public void reset() {
        crc = 1;
    }
    
    public long getCRC() {
        return crc;
    }
    
    
    public String getResult() {
        return "" + getCRC();
    }
    
    public void setValidationProperties(Properties props) {
        validiationProperties = props;
    }
    
    public void checkResult(int loopNumber) {
        if (getResult().equals(validiationProperties.getProperty(propertyName))) {
            if (loopNumber == 0) {
                handleSuccessfullValidation();
            }
        } else {
            handleErrorValidation(loopNumber);
        }
        reset();
    }
    
    public void handleSuccessfullValidation() {
        Context.getOut().println(propertyName + ":PASSED");
    }
    
    public void handleErrorValidation(int loopNumber) {
        Context.getOut().println(propertyName + ":FAILED");
    }
}