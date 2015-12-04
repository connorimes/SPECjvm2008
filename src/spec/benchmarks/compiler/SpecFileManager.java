package spec.benchmarks.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JavacFileManager;
import com.sun.tools.javac.util.ListBuffer;


public class SpecFileManager extends JavacFileManager {		
	public class BaseFileObject implements JavaFileObject {
		public boolean delete() {
			throw new UnsupportedOperationException("delete()");
		}
		
		public Modifier getAccessLevel() {
			throw new UnsupportedOperationException("getAccessLevel()");
		}
		
		public CharSequence getCharContent(boolean arg0) throws IOException {			
			throw new UnsupportedOperationException("getCharContent(boolean)");
		}
		
		public Kind getKind() {			
			throw new UnsupportedOperationException("getKind()");
		}
		
		public long getLastModified() {
			throw new UnsupportedOperationException("getLastModified()");
		}
		
		public String getName() {
			throw new UnsupportedOperationException("getName()");
		}
		
		public NestingKind getNestingKind() {
			throw new UnsupportedOperationException("getNestingKind()");
		}
		
		public boolean isNameCompatible(String arg0, Kind arg1) {			
			throw new UnsupportedOperationException("isNameCompatible(String, Kind)");
		}
		
		public InputStream openInputStream() throws IOException {
			throw new UnsupportedOperationException("openInputStream");
		}
		
		public OutputStream openOutputStream() throws IOException {
			throw new UnsupportedOperationException("openOutputStream()");
		}
		
		public Reader openReader(boolean arg0) throws IOException {
			throw new UnsupportedOperationException("openReader(boolean)");
		}
		
		public Writer openWriter() throws IOException {			
			throw new UnsupportedOperationException("openWriter()");
		}
		
		public URI toUri() {
			throw new UnsupportedOperationException("toURI");			
		}	
	}
	
    public class InputFileObject extends RegularFileObject {       
        CharBuffer buffer;

        public InputFileObject(String name, File f) {
        	super(name, f);                         
            try {                                            
                byte[] b = Util.getBytes(f);                                
                ByteBuffer buf = ByteBuffer.wrap(b);
                CharsetDecoder decoder = Util.CHARSET.newDecoder()
                    .onMalformedInput(Util.ACTION)
                    .onUnmappableCharacter(Util.ACTION);
                buffer = decoder.decode(buf).asReadOnlyBuffer();                
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }

        public InputStream openInputStream() throws IOException {
            throw new UnsupportedOperationException();
        } 

        public CharBuffer getCharContent(boolean ignoreEncodingErrors) throws IOException {        	
        	return buffer;
        }       
    }
    
    public class OutputFileObject extends BaseFileObject {
    	String name;
    	public OutputFileObject(String name) {
    		this.name = name;
    	}
        public OutputStream openOutputStream() throws IOException {        	
        	return new OutputStream() {        		
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
        	};
        }                
    }
    
	public class CachedFileObject extends BaseFileObject {		
		Kind kind;		
		String name;
		NestingKind nestingKind;			
		String history;
		String path;
		byte[] b;

		CachedFileObject(JavaFileObject jfo) {
			this.kind = jfo.getKind();			
			this.name = jfo.getName();
			this.nestingKind = jfo.getNestingKind();			
			if (jfo instanceof RegularFileObject) {
				history = Util.REGULAR_FILE_OBJECT_NAME;
				path = ((RegularFileObject) jfo).getPath();
			} else if (jfo instanceof ZipFileObject) {
				history = Util.ZIP_FILE_OBJECT_NAME;
				path = ((ZipFileObject) jfo).getZipEntryName();
			} else if (jfo instanceof ZipFileIndexFileObject) {
				history = Util.ZIP_FILE_INDEX_FILE_OBJECT_NAME;
				path = ((ZipFileIndexFileObject) jfo).getZipEntryName();
			}

			try {
				InputStream is = jfo.openInputStream();				
				b = Util.getBytes(is);
                is.close();				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public Kind getKind() {
			return kind;
		}

		public String getName() {
			return name;
		}

		public NestingKind getNestingKind() {
			return nestingKind;
		} 

		public InputStream openInputStream() throws IOException {		
			return new ByteArrayInputStream(b);
		}	
	}

	static HashMap<String, Iterable<JavaFileObject>> map = new HashMap<String, Iterable<JavaFileObject>>();
	static HashMap<Location, Iterable<? extends File>> locationMap =
		                                  new HashMap<Location, Iterable<? extends File>>();
	
	static HashMap<File, InputFileObject> inputFiles = new HashMap<File, InputFileObject>();
	
	public static void preRegister(final Context context, final Compiler compiler) {
		context.put(JavaFileManager.class,
				new Context.Factory<JavaFileManager>() {
					public JavaFileManager make() {
						SpecFileManager fileManager = 
						    new SpecFileManager(context, true, null);
						compiler.fileManager = fileManager;
						return fileManager;
					}
				});
	}
	
	public static void reset() {
		map = new HashMap<String, Iterable<JavaFileObject>>();
		locationMap = new HashMap<Location, Iterable<? extends File>>();
		inputFiles = new HashMap<File, InputFileObject>();
	}
	
	private long crc = 1;

	public SpecFileManager(Context context, boolean register, Charset charset) {
		super(context, register, charset);
	}	

	public Iterable<JavaFileObject> list(Location location, String packageName,
			Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
		String key = generateKey(location, packageName, kinds, recurse);
		if (map.containsKey(key)) {
			return map.get(key);
		}

		Iterable<JavaFileObject> superResults = super.list(location,
				packageName, kinds, recurse);
		ListBuffer<JavaFileObject> results = new ListBuffer<JavaFileObject>();
		for (JavaFileObject obj : superResults) {			
			results.append(new CachedFileObject(obj));
		}
		map.put(key, results.toList());
		return results;
	}

	public static String generateKey(Location location, String packageName,
			Set<JavaFileObject.Kind> kinds, boolean recurse) {
		String result = /*packageName;//*/location.getName() + "," + location.isOutputLocation()
				+ ";" + packageName + ";";
		for (JavaFileObject.Kind kind : kinds) {
			result += kind.extension;
		}
		result += ";" + recurse;
		return result;
	}

	public String inferBinaryName(Location location, JavaFileObject file) {		
		if (file instanceof CachedFileObject) {
			return inferBinaryName2(location, (CachedFileObject) file);
		} else		
	    return super.inferBinaryName(location, file);	
	}

	public String inferBinaryName2(Location location, CachedFileObject file) {		
		file.getClass(); // null check
		location.getClass(); // null check
		// Need to match the path semantics of list(location, ...)
		Iterable<? extends File> path = getLocation(location);
		if (path == null) {
			// System.err.println("Path for " + location + " is null");
			return null;
		}
		// System.err.println("Path for " + location + " is " + path);
		if (Util.REGULAR_FILE_OBJECT_NAME.equals(file.history)) {
			String rPath = file.path;			
			for (File dir : path) {				
				String dPath = dir.getPath();
				if (!dPath.endsWith(File.separator))
					dPath += File.separator;
				if (rPath.regionMatches(true, 0, dPath, 0, dPath.length())
						&& new File(rPath.substring(0, dPath.length()))
								.equals(new File(dPath))) {
					String relativeName = rPath.substring(dPath.length());
					return removeExtension(relativeName).replace(
							File.separatorChar, '.');
				}
			}
		} else if (Util.ZIP_FILE_OBJECT_NAME.equals(file.history)) {
			String entryName = file.path;			
			return removeExtension(entryName).replace('/', '.');
		} else if (Util.ZIP_FILE_INDEX_FILE_OBJECT_NAME.equals(file.history)) {
			String entryName = file.path;			
			return removeExtension(entryName).replace(File.separatorChar, '.');
		} else {			
			throw new IllegalArgumentException(file.getClass().getName());
		}	
		// System.err.println("inferBinaryName failed for " + file);
		return null;
	}

	private static String removeExtension(String fileName) {
		int lastDot = fileName.lastIndexOf(".");
		return (lastDot == -1 ? fileName : fileName.substring(0, lastDot));
	}
		
	public Iterable<? extends File> getLocation(Location location) {
		if (locationMap.containsKey(location)) {			
			return locationMap.get(location);
		}
		Iterable<? extends File> result = super.getLocation(location);
		locationMap.put(location, result);
		return result;
	}
		
	public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(
		Iterable<? extends File> files) {		
	    ArrayList<InputFileObject> result;
	    if (files instanceof Collection) {
	        result = new ArrayList<InputFileObject>(((Collection)files).size());
	    } else {
	        result = new ArrayList<InputFileObject>();
	    }        
	    
	    for (File f: files) {
	    	if (inputFiles.containsKey(f)) {
	    		result.add(inputFiles.get(f));
	    	} else {
	    		InputFileObject obj = new InputFileObject(f.getName(), f);
	    		inputFiles.put(f, obj);
	    		result.add(obj);
	    	}	        
	    }        
	    return result;
	}
	
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
		int index = className.lastIndexOf(".");
		String name = className.substring(index + 1, className.length()) + ".class";		
		return new OutputFileObject(name);
	}
	
	
	public long getChecksum() {
		return crc;		
	}
}
