package qyudy.filemonitor.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import qyudy.filemonitor.IAddMonitor;
import qyudy.filemonitor.impl.DelMonitor;

public class DelMonitorPanel extends JPanel implements IAddMonitor
{
    private static final long serialVersionUID = 5382474609608561053L;

    @Override
    public void addToPane(JTabbedPane tabbedPane, final PrintWriter pw) {
        this.setBackground(null);
        this.setOpaque(false);
        tabbedPane.addTab("删除监视器", null, this, null);
        
        JLabel label_8 = new JLabel("文件/文件夹");
        
        JScrollPane delScrollPane = new JScrollPane();
        
        final JTextArea delMonitTextArea = new JTextArea();
        delScrollPane.setViewportView(delMonitTextArea);
        
        final JButton delStartButton = new JButton("start");
        final DelMonitor[] delMonitor = new DelMonitor[1];
        delStartButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (delStartButton.isEnabled())
                {
                    delMonitor[0] = new DelMonitor(delMonitTextArea.getText().split("\\n|\\r\\n"));
                    delMonitor[0].setLogger(pw);
                    delMonitor[0].startAsync();
                    delStartButton.setEnabled(false);
                    delStartButton.setText("stop");
                }
                else
                {
                    if (delMonitor[0] != null)
                    {
                        delMonitor[0].stop();
                        delMonitor[0] = null;
                    }
                    delStartButton.setEnabled(true);
                    delStartButton.setText("start");
                }
            }
        });
        GroupLayout delGroupLayout = new GroupLayout(this);
        delGroupLayout.setHorizontalGroup(
            delGroupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(delGroupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label_8)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(delGroupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(delStartButton, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
                        .addComponent(delScrollPane, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE))
                    .addContainerGap())
        );
        delGroupLayout.setVerticalGroup(
            delGroupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(delGroupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(delGroupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(delScrollPane, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_8))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(delStartButton, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(24, Short.MAX_VALUE))
        );
        this.setLayout(delGroupLayout);
    }
    
}
