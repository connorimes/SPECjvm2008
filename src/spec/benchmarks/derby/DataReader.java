package spec.benchmarks.derby;

import java.io.IOException;

public class DataReader {
	public class Data {		
		byte[] inbytes = new byte[Utils.INFO_LENGTH * DerbyHarness.scale];
	    String[] durations = new String[DerbyHarness.scale];
	    byte[] info = new byte[Utils.INFO_LENGTH * DerbyHarness.scale];    
	}
		
	private int limit;
	private int start;
	private int end;	  
    private CyclicReader specsReader = new CyclicReader(Utils.specsFileName, true);
    private CyclicReader callsReader = new CyclicReader(Utils.callsFileName, false);    
    private boolean[] endOfTablesReached; 
    private int currentTable;
    
    public DataReader(int limit, int tableNumber) {			
		this.limit = limit;		
		endOfTablesReached = new boolean[tableNumber];
	}
    
    synchronized int[] getData(Data[] data, int index) throws IOException {
    	if (isEndOfTableReached(index)) {    		
			return new int[] {0, start, end, -1};
		}
    	for (int i = 0; i < data.length; i ++) {    		
    		if (end != limit) {    			
    	        if (specsReader.read(data[i].inbytes, Utils.INFO_LENGTH * DerbyHarness.scale) > 0
                       && callsReader.read(data[i].durations) > 0) {
         	        data[i].info = DerbyHarness.getSpec(data[i].inbytes, data[i].info);    
         	        if (i == 0) {
         	        	start = end;
         	        }
         	        end = end + Main.THREADSPERDB;         	        
    	        } else {   	        	
    	            return new int[] {i, start, end, 0};
    	        }    
    		}  else {
    			int[] result = new int[] {i, start, end, i == 0 ? -1 : 0};
    			if (currentTable < endOfTablesReached.length) {
	    		    endOfTablesReached[currentTable] = true;
	    		}    
	    		currentTable++;
	    		end = 0;
	    		start = 0;	    		
    			return result;
    		}    		
    	}    	
    	return new int[] {data.length, start, end, 0};
    }
    
    void close() {
    	specsReader.close();
    	callsReader.close();
    }   
    
    private synchronized boolean isEndOfTableReached(int index) {
    	return (index < currentTable || (index == currentTable && endOfTablesReached[index]));    	
    }    
}
