package qyudy.filemonitor.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import qyudy.filemonitor.IMonitor;
import qyudy.filemonitor.impl.FileMonitor;

public class FileMonitorPanel extends JPanel implements IMonitor {
    private static final long serialVersionUID = 5382474609608561053L;

    private static final String CONFIG_PREFIX = "file:";

    final JTextField fileToMonitorTextField = new JTextField();

    final JTextField fileCopyToTextField = new JTextField();

    @Override
    public void addToPane(JTabbedPane tabbedPane, String config, final PrintWriter pw) {
        this.setBackground(null);
        this.setOpaque(false);

        setConfig(config);

        tabbedPane.addTab(getName(), null, this, null);

        JLabel label_3 = new JLabel("监视文件");
        fileToMonitorTextField.setColumns(10);

        JLabel lblNewLabel = new JLabel("拷贝路径");
        fileCopyToTextField.setColumns(10);

        final JButton fileStartButton = new JButton("start");
        final FileMonitor[] fileMonitor = new FileMonitor[1];
        fileStartButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (fileStartButton.isEnabled()) {
                    fileMonitor[0] = new FileMonitor(fileToMonitorTextField.getText(), fileCopyToTextField.getText());
                    fileMonitor[0].setLogger(pw);
                    fileMonitor[0].startAsync();
                    fileStartButton.setEnabled(false);
                    fileStartButton.setText("stop");
                } else {
                    if (fileMonitor[0] != null) {
                        fileMonitor[0].stop();
                        fileMonitor[0] = null;
                    }
                    fileStartButton.setEnabled(true);
                    fileStartButton.setText("start");
                }
            }
        });
        GroupLayout filerGroupLayout = new GroupLayout(this);
        filerGroupLayout.setHorizontalGroup(
                filerGroupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(filerGroupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(filerGroupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(filerGroupLayout.createSequentialGroup()
                                                .addComponent(label_3)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(fileToMonitorTextField, GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE))
                                        .addGroup(filerGroupLayout.createSequentialGroup()
                                                .addComponent(lblNewLabel)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(filerGroupLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(fileStartButton, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(fileCopyToTextField, GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE))))
                                .addGap(4))
        );
        filerGroupLayout.setVerticalGroup(
                filerGroupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(filerGroupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(filerGroupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(label_3)
                                        .addComponent(fileToMonitorTextField, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(filerGroupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblNewLabel)
                                        .addComponent(fileCopyToTextField, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
                                .addGap(18)
                                .addComponent(fileStartButton)
                                .addContainerGap(114, Short.MAX_VALUE))
        );
        this.setLayout(filerGroupLayout);
    }

    public static boolean matchConfig(String config) {
        return config != null && config.startsWith(CONFIG_PREFIX);
    }

    @Override
    public String getConfig() {
        String monitor = fileToMonitorTextField.getText();
        String copy = fileCopyToTextField.getText();
        if (monitor.isEmpty() || copy.isEmpty()) {
            return null;
        }
        return CONFIG_PREFIX + getName() + "," + monitor + "," + copy;
    }

    private void setConfig(String config) {
        if (config != null && config.startsWith(CONFIG_PREFIX)) {
            String[] files = config.substring(CONFIG_PREFIX.length()).split(",");
            setName(files[0]);
            fileToMonitorTextField.setText(files[1]);
            fileCopyToTextField.setText(files[2]);
        } else {
            setName("文件监视器");
        }
    }

}
