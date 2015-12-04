package spec.validity;

import java.util.Iterator;

public interface DigestDefinition extends Iterable<String>{

    /**
     *  Get iterator of jar filenames to check
     */
    Iterator<String> iterator();
    
    /**
     * Gets the byte definition for current item.
     * @param currItem What to digest and 
     * @return a byte array with the data.
     */
    byte [] getArray(String currItem);
    
}
