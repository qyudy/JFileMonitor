package qyudy.filemonitor;

public class Constants {
    public static final String version;
    static {
        Package pack = Constants.class.getPackage();
        if (pack != null) {
            String ver = pack.getImplementationVersion();
            if (ver != null) {
                version = ver;
            } else {
                version = "";
            }
        } else {
            version = "";
        }
    }
}
