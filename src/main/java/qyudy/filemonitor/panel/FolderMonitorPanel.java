package qyudy.filemonitor.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import qyudy.filemonitor.IMonitor;
import qyudy.filemonitor.impl.FolderMonitor;

public class FolderMonitorPanel extends JPanel implements IMonitor {
    private static final long serialVersionUID = 5382474609608561053L;

    private static final String CONFIG_PREFIX = "folder:";

    final JTextArea folderMonitTextArea = new JTextArea();

    final JTextField folderCopyToTextField = new JTextField(50);

    final JCheckBox folderDelIfNotSyncCheckBox = new JCheckBox("同步删除 *慎用");

    @Override
    public void addToPane(JTabbedPane tabbedPane, String config, final PrintWriter pw) {
        this.setBackground(null);
        this.setOpaque(false);

        setConfig(config);

        tabbedPane.addTab(getName(), null, this, null);

        JLabel label = new JLabel("监视目录");

        JScrollPane folderMonitScrollPane = new JScrollPane();

        folderMonitScrollPane.setViewportView(folderMonitTextArea);

        JLabel label_1 = new JLabel("拷贝目录");

        folderDelIfNotSyncCheckBox.setBackground(null);
        folderDelIfNotSyncCheckBox.setOpaque(false);

        final JButton folderStartButton = new JButton("start");
        final FolderMonitor[] folderMonitor = new FolderMonitor[1];
        folderStartButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (folderStartButton.isEnabled()) {
                    folderMonitor[0] = new FolderMonitor(folderMonitTextArea.getText().split("\n|\r\n"), folderCopyToTextField.getText(), folderDelIfNotSyncCheckBox.isSelected());
                    folderMonitor[0].setLogger(pw);
                    folderMonitor[0].startAsync();
                    folderStartButton.setEnabled(false);
                    folderStartButton.setText("stop");
                } else {
                    if (folderMonitor[0] != null) {
                        folderMonitor[0].stop();
                        folderMonitor[0] = null;
                    }
                    folderStartButton.setEnabled(true);
                    folderStartButton.setText("start");
                }
            }
        });

        GroupLayout folderGroupLayout = new GroupLayout(this);
        folderGroupLayout.setHorizontalGroup(
                folderGroupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(folderGroupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(folderGroupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(folderGroupLayout.createSequentialGroup()
                                                .addComponent(label)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(folderGroupLayout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(folderMonitScrollPane, GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                                                        .addGroup(Alignment.LEADING, folderGroupLayout.createSequentialGroup()
                                                                .addComponent(folderDelIfNotSyncCheckBox)
                                                                .addGap(36)
                                                                .addComponent(folderStartButton, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))))
                                        .addGroup(Alignment.TRAILING, folderGroupLayout.createSequentialGroup()
                                                .addComponent(label_1)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(folderCopyToTextField, GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        folderGroupLayout.setVerticalGroup(
                folderGroupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(folderGroupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(folderGroupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(folderGroupLayout.createSequentialGroup()
                                                .addComponent(folderMonitScrollPane, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(folderGroupLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(folderCopyToTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label_1))
                                                .addGap(10)
                                                .addGroup(folderGroupLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(folderStartButton, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(folderDelIfNotSyncCheckBox))
                                                .addContainerGap(21, Short.MAX_VALUE))
                                        .addComponent(label)))
        );
        this.setLayout(folderGroupLayout);
    }

    public static boolean matchConfig(String config) {
        return config.startsWith(CONFIG_PREFIX);
    }

    @Override
    public String getConfig() {
        String monitor = folderMonitTextArea.getText();
        String copyTo = folderCopyToTextField.getText();
        boolean del = folderDelIfNotSyncCheckBox.isSelected();
        if (monitor.isEmpty() || copyTo.isEmpty()) {
            return null;
        }
        return CONFIG_PREFIX + getName() + "," + String.join(",", monitor.split("\n|\r\n")) + "," + copyTo + "," + del;
    }

    private void setConfig(String config) {
        if (config != null && config.startsWith(CONFIG_PREFIX)) {
            String[] conf = config.substring(CONFIG_PREFIX.length()).split(",");
            setName(conf[0]);
            folderMonitTextArea.setText(String.join("\n", Arrays.copyOfRange(conf, 1, conf.length - 2)));
            folderCopyToTextField.setText(conf[conf.length - 2]);
            folderDelIfNotSyncCheckBox.setSelected(Boolean.parseBoolean(conf[conf.length - 1]));
        } else {
            setName("文件夹监视器");
        }
    }

}
