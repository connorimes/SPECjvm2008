package spec.reporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import spec.harness.Constants;


public class BenchmarkGroupRecords {	
	static class BenchmarkGroupRecord {	
		String groupName;
		boolean isSingleBenchmark;	
		ArrayList<BenchmarkRecord> bmRecords = new ArrayList<BenchmarkRecord>();
		double score;
		boolean isValid = true;
		
		public BenchmarkGroupRecord(BenchmarkRecord record) {
			isSingleBenchmark = true;
			bmRecords.add(record);
			groupName = record.name;
		}
		
        public BenchmarkGroupRecord(String name) {
			groupName = name;			
		}
        
        void addBenchmarkRecord(BenchmarkRecord bmRecord) {
        	bmRecords.add(bmRecord);        	
        }
        
        double computeScore() {
        	if (isSingleBenchmark) {
        		BenchmarkRecord record = bmRecords.get(0); 
        		score = record.maxScore;
        		isValid = record.isValidRun();        		
        	} else {
        		double product = 1;
        		for (int i = 0; i < bmRecords.size(); i ++) {
        			BenchmarkRecord record = bmRecords.get(i);        			
        			isValid = isValid && record.isValidRun();
        			Utils.dbgPrint("\t" + record.name + " " + isValid + " " + record.maxScore);
        			product *= record.maxScore;
        		}
        		score = Math.pow(product, (double) 1/bmRecords.size());
        	}
        	score = isValid ? score : Utils.INVALID_SCORE;
        	return score;
        }
	}
	
	class BenchmarkResultsIterator {
		Iterator iter;
		BenchmarkGroupRecord currentGroupRecord;
		BenchmarkRecord currentRecord;
		int currentIndex;
		boolean wasMonteCarlo;
		
		public BenchmarkResultsIterator() {
			iter = groupRecords.keySet().iterator();
		}
		
		BenchmarkRecord next() {
			currentRecord = null;
			if (currentGroupRecord != null && currentGroupRecord.bmRecords.size() > currentIndex) {				
				currentRecord = currentGroupRecord.bmRecords.get(currentIndex++);				 
			} else {				
				do {
				   if (!iter.hasNext()) {	
					   break;
				   }				   
				   String key = (String)iter.next();				   
				   currentGroupRecord = groupRecords.get(key);
				   if (currentGroupRecord.bmRecords.size() > 0) {
					   currentIndex = 0;
					   currentRecord = currentGroupRecord.bmRecords.get(currentIndex++);
					   break;
				   }
				} while (true);
			} 
			if (Utils.isScimarkMonteCarlo(currentRecord)) {
				if (!wasMonteCarlo) {
				    wasMonteCarlo = true;
				} else {
					return next();
				}
			}
			return currentRecord;
		}	
		
		BenchmarkRecord getCurrentRecord() {
			return currentRecord;
		}
	}
	
    TreeMap<String, BenchmarkGroupRecord> groupRecords = new TreeMap<String, BenchmarkGroupRecord>();    
    int  validBenchmarksNumber;
    boolean allBenchmarksValid = true;
    TreeMap<String, Double> scores = new TreeMap<String, Double>();    
    
        
    void addNewBenchmarkRecord(BenchmarkRecord record) {
    	if (record.isValidRun() && !Constants.CHECK_BNAME.equals(record.name)) {
    		if (!Constants.CHECK_BNAME.equals(record.name)) {
    		    validBenchmarksNumber ++;
    		}    		
    	}
    	allBenchmarksValid = allBenchmarksValid && record.isValidRun();    	
    	int index = record.name.indexOf(".");
    	if (index >= 0) {
    		String subgroup = record.name.substring(0, index);
    		if (Utils.isScimarkLarge(record)) {
    			subgroup += "." + Constants.SCIMARK_BNAME_LARGE_POSTFIX;
    		} else if (Utils.isScimarkSmall(record)) {
    			subgroup += "." + Constants.SCIMARK_BNAME_SMALL_POSTFIX;    			 
    		}    
    		if (Utils.isScimarkMonteCarlo(record)) {
    			updateGroupRecord(Constants.SCIMARK_SMALL_GNAME, record);
    			updateGroupRecord(Constants.SCIMARK_LARGE_GNAME, record);
    		}  else {
    			updateGroupRecord(subgroup, record);
    		}
    		
    	} else {
    		groupRecords.put(record.name, new BenchmarkGroupRecord(record));
    	}
    }   
    
    double computeCompositeScore() {    	
    	Iterator iter = groupRecords.keySet().iterator();
    	double product = 1;
    	int counter = 0;
    	while (iter.hasNext()) {
    		String key = (String) iter.next();
    		BenchmarkGroupRecord r = (BenchmarkGroupRecord)groupRecords.get(key);
    		if (r.bmRecords.size() > 0 && !Utils.isCheck(r.bmRecords.get(0))) {
    			double groupScore = r.computeScore();
    			Utils.dbgPrint("geo_mean: " + r.groupName + " " + groupScore);
    		    product *= groupScore;
    		    counter ++;
    		    scores.put(r.groupName, groupScore);
    		}     
    	}
    	if (counter == 0) {
    		return 1;
    	} 
    	double compositeScore = allBenchmarksValid ? Math.pow(product, (double) 1/counter) : Utils.INVALID_SCORE;
    	Utils.dbgPrint("composite score: " + compositeScore);
    	return compositeScore;
    	
    }
    
    private void updateGroupRecord(String name, BenchmarkRecord record) {
    	if (!groupRecords.containsKey(name)) {
    		groupRecords.put(name, new BenchmarkGroupRecord(name));
    	}
    	groupRecords.get(name).addBenchmarkRecord(record);
    }   
}
