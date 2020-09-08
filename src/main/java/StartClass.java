import java.io.IOException;

public class StartClass {
    public static void main(String[] args) throws IOException {
        MonitoredData m1=new MonitoredData();
        m1.readDataFromFile();
    }
}
