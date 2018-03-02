/**
 * 
 */
package qyudy.filemonitor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import qyudy.filemonitor.impl.DelMonitor;
import qyudy.filemonitor.impl.FileMonitor;
import qyudy.filemonitor.impl.FolderMonitor;

/**
 * @author qyudy
 *
 */
public class FileMonitorWindow
{

    private JFrame frame;
    private JTextField fileToMonitorTextField;
    private JTextField fileCopyToTextField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable()
        {
            public void run() {
                try
                {
                    FileMonitorWindow window = new FileMonitorWindow();
                    window.frame.setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public FileMonitorWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (e.getMessage() != null && e.getMessage().length() > 0) {
                    JOptionPane.showMessageDialog(frame, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                } else {
                    e.printStackTrace();
                }
            }
        });
        
        frame = new JFrame();
        frame.setTitle("JFileMonitor " + Constants.version);
        frame.setMinimumSize(new Dimension(600, 450));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu addMenu = new JMenu("新增");
        menuBar.add(addMenu);

        JMenu propMenu = new JMenu("配置");
        menuBar.add(propMenu);

        JMenuItem loadPropMenuItem = new JMenuItem("读取配置");
        loadPropMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "暂未实现", "暂未实现", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        propMenu.add(loadPropMenuItem);

        JMenuItem savePropMenuItem = new JMenuItem("保存配置");
        savePropMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "暂未实现", "暂未实现", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        propMenu.add(savePropMenuItem);

        JMenu helpMenu = new JMenu("帮助");
        menuBar.add(helpMenu);

        JMenuItem readmeMenuItem = new JMenuItem("readme");
        readmeMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("qyudy/filemonitor/readme.txt");
                        FileOutputStream out = new FileOutputStream("readme.txt"))
                {
                    byte[] bs = new byte[1024 * 100];
                    int count = 0;
                    while ((count = in.read(bs)) > 0)
                    {
                        out.write(bs, 0, count);
                    }
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                try
                {
                    java.awt.Desktop.getDesktop().open(new File("readme.txt"));
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        helpMenu.add(readmeMenuItem);

        JMenuItem aboutMenuItem = new JMenuItem("about");
        aboutMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "JFileMonitor " + Constants.version + "\nedit by qyudy\nstart from 2018/3", "about", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutMenuItem);
        
        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(null);
        tabbedPane.setOpaque(false);
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem moveLeftMenuItem = new JMenuItem("左移");
        moveLeftMenuItem.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = tabbedPane.getSelectedIndex();
                if (index <= 0) return;
                tabbedPane.insertTab(tabbedPane.getTitleAt(index), null, tabbedPane.getSelectedComponent(), null, index - 1);
                tabbedPane.setSelectedIndex(index - 1);
            }
        });
        popupMenu.add(moveLeftMenuItem);
        JMenuItem moveRightMenuItem = new JMenuItem("右移");
        moveRightMenuItem.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = tabbedPane.getSelectedIndex();
                if (index >= tabbedPane.getTabCount() - 1) return;
                tabbedPane.insertTab(tabbedPane.getTitleAt(index), null, tabbedPane.getSelectedComponent(), null, index + 2);
                tabbedPane.setSelectedIndex(index + 1);
            }
        });
        popupMenu.add(moveRightMenuItem);
        JMenuItem renameMenuItem = new JMenuItem("改名");
        renameMenuItem.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e) {
                String title = JOptionPane.showInputDialog("修改名称", tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), title);
            }
        });
        popupMenu.add(renameMenuItem);
        JMenuItem closeMenuItem = new JMenuItem("关闭");
        closeMenuItem.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e) {
                Container selected = (Container) tabbedPane.getSelectedComponent();
                for (Component c : selected.getComponents())
                {
                    if (c instanceof JButton)
                    {
                        if (((JButton) c).isEnabled())
                        {
                            tabbedPane.remove(tabbedPane.getSelectedComponent());
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(selected, "删除监视器前需要先停止它");
                        }
                        break;
                    }
                }
            }
        });
        popupMenu.add(closeMenuItem);
        tabbedPane.setComponentPopupMenu(popupMenu);
        
        JScrollPane resultScrollPane = new JScrollPane();
        
        final JTextArea resultTextArea = new JTextArea();
        resultScrollPane.setViewportView(resultTextArea);
        
        final PrintWriter pw = new PrintWriter(new Writer()
        {
            private char[] cs = new char[1024 * 100];
            private int length = 0;
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                while (len > 0)
                {
                    int readLen = Math.min(cs.length - length, len);
                    System.arraycopy(cbuf, off, cs, length, readLen);
                    length += readLen;
                    len -= readLen;
                    if (len > 0)
                    {
                        flush();
                    }
                }
                flush();
            }
            
            @Override
            public void flush() throws IOException {
                if (length > 0)
                {
                    resultTextArea.append(new String(cs, 0, length));
                }
                length = 0;
            }
            
            @Override
            public void close() throws IOException {
                flush();
            }
        });
        
        JMenuItem addFolderMonitorMenuItem = new JMenuItem("文件夹监视器");
        addFolderMonitorMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                addFolderMonitor(tabbedPane, pw);
            }
        });
        addMenu.add(addFolderMonitorMenuItem);
        
        JMenuItem addFileMonitorMenuItemMenuItem = new JMenuItem("文件监视器");
        addFileMonitorMenuItemMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                addFileMonitor(tabbedPane, pw);
            }
        });
        addMenu.add(addFileMonitorMenuItemMenuItem);
        
        JMenuItem addDelMonitorMenuItemMenuItem = new JMenuItem("删除监视器");
        addDelMonitorMenuItemMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                addDelMonitor(tabbedPane, pw);
            }
        });
        addMenu.add(addDelMonitorMenuItemMenuItem);
        
        GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(resultScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                        .addComponent(tabbedPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(resultScrollPane, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                    .addContainerGap())
        );
        
        addFolderMonitor(tabbedPane, pw);

        addFileMonitor(tabbedPane, pw);

        addDelMonitor(tabbedPane, pw);
        
        frame.getContentPane().setLayout(groupLayout);
    }

    private void addDelMonitor(JTabbedPane tabbedPane, final PrintWriter pw) {
        JPanel delPanel = new JPanel();
        delPanel.setBackground(null);
        delPanel.setOpaque(false);
        tabbedPane.addTab("删除监视器", null, delPanel, null);
        
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
        GroupLayout delGroupLayout = new GroupLayout(delPanel);
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
        delPanel.setLayout(delGroupLayout);
    }

    private void addFileMonitor(JTabbedPane tabbedPane, final PrintWriter pw) {
        JPanel filePanel = new JPanel();
        filePanel.setBackground(null);
        filePanel.setOpaque(false);
        tabbedPane.addTab("文件监视器", null, filePanel, null);
        
        JLabel label_3 = new JLabel("监视文件");
        
        fileToMonitorTextField = new JTextField();
        fileToMonitorTextField.setColumns(10);
        
        JLabel lblNewLabel = new JLabel("拷贝路径");
        
        fileCopyToTextField = new JTextField();
        fileCopyToTextField.setColumns(10);
        
        final JButton fileStartButton = new JButton("start");
        final FileMonitor[] fileMonitor = new FileMonitor[1];
        fileStartButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (fileStartButton.isEnabled())
                {
                    fileMonitor[0] = new FileMonitor(fileToMonitorTextField.getText(), fileCopyToTextField.getText());
                    fileMonitor[0].setLogger(pw);
                    fileMonitor[0].startAsync();
                    fileStartButton.setEnabled(false);
                    fileStartButton.setText("stop");
                }
                else
                {
                    if (fileMonitor[0] != null)
                    {
                        fileMonitor[0].stop();
                        fileMonitor[0] = null;
                    }
                    fileStartButton.setEnabled(true);
                    fileStartButton.setText("start");
                }
            }
        });
        GroupLayout filerGroupLayout = new GroupLayout(filePanel);
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
        filePanel.setLayout(filerGroupLayout);
    }

    private void addFolderMonitor(JTabbedPane tabbedPane, final PrintWriter pw) {
        JPanel folderPanel = new JPanel();
        folderPanel.setBackground(null);
        folderPanel.setOpaque(false);
        tabbedPane.addTab("文件夹监视器", null, folderPanel, null);
        
//        JPanel folderTab = new JPanel();
//        folderTab.setBackground(null);
//        folderTab.setOpaque(false);
//        JLabel folderTitleLabel = new JLabel("文件夹监视器");
//        folderTitleLabel.setBackground(null);
//        folderTitleLabel.setOpaque(false);
//        folderTab.add(folderTitleLabel);
//        JButton folderCloseButton = new JButton("x");
//        folderCloseButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//        folderTab.add(folderCloseButton);
//        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(folderPanel), folderTab);
        
        JLabel label = new JLabel("监视目录");

        JScrollPane folderMonitScrollPane = new JScrollPane();
        
        final JTextArea folderMonitTextArea = new JTextArea();
        folderMonitScrollPane.setViewportView(folderMonitTextArea);

        JLabel label_1 = new JLabel("拷贝目录");
        
        final JTextField folderCopyToTextField = new JTextField(50);
        
        final JCheckBox folderDelIfNotSyncCheckBox = new JCheckBox("同步删除 *慎用");
        folderDelIfNotSyncCheckBox.setBackground(null);
        folderDelIfNotSyncCheckBox.setOpaque(false);
        
        final JButton folderStartButton = new JButton("start");
        final FolderMonitor[] folderMonitor = new FolderMonitor[1];
        folderStartButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (folderStartButton.isEnabled())
                {
                    folderMonitor[0] = new FolderMonitor(folderMonitTextArea.getText().split("\\n|\\r\\n"), folderCopyToTextField.getText(), folderDelIfNotSyncCheckBox.isSelected());
                    folderMonitor[0].setLogger(pw);
                    folderMonitor[0].startAsync();
                    folderStartButton.setEnabled(false);
                    folderStartButton.setText("stop");
                }
                else
                {
                    if (folderMonitor[0] != null)
                    {
                        folderMonitor[0].stop();
                        folderMonitor[0] = null;
                    }
                    folderStartButton.setEnabled(true);
                    folderStartButton.setText("start");
                }
            }
        });
        
        GroupLayout folderGroupLayout = new GroupLayout(folderPanel);
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
        folderPanel.setLayout(folderGroupLayout);
    }
}
