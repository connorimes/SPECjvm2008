package spec.harness;

public class VMVersionTest {
    static String[] properties = {"java.version",
    "java.vm.version",
    "java.home"};
    
    public static String getVersionInfo() {
        String result = "";
        for(int i = 0; i < properties.length; i ++) {
            result += properties[i] + ":" + System.getProperty(properties[i]) + "\n";
        }
        return result;
    }
    public static void main(String[] args) {
        System.out.print(getVersionInfo());
    }
}
