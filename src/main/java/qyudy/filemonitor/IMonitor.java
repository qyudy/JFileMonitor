package qyudy.filemonitor;

import java.io.PrintWriter;

import javax.swing.JTabbedPane;

public interface IMonitor {
    void addToPane(JTabbedPane tabbedPane, String title, PrintWriter pw);

    String getConfig();
}
