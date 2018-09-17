/**
 * 
 */
package qyudy.filemonitor;

import qyudy.filemonitor.panel.DelMonitorPanel;
import qyudy.filemonitor.panel.FileMonitorPanel;
import qyudy.filemonitor.panel.FolderMonitorPanel;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author qyudy
 *
 */
public class FileMonitorWindow
{

    private JFrame frame;

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
        frame.setTitle("JFileMonitor " + qyudy.filemonitor.Constants.version);
        frame.setMinimumSize(new Dimension(600, 450));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu addMenu = new JMenu("新增");
        menuBar.add(addMenu);

//        JMenu propMenu = new JMenu("配置");
//        menuBar.add(propMenu);
//
//        JMenuItem loadPropMenuItem = new JMenuItem("读取配置");
//        loadPropMenuItem.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                JOptionPane.showMessageDialog(null, "暂未实现", "暂未实现", JOptionPane.INFORMATION_MESSAGE);
//            }
//        });
//        propMenu.add(loadPropMenuItem);
//
//        JMenuItem savePropMenuItem = new JMenuItem("保存配置");
//        savePropMenuItem.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                JOptionPane.showMessageDialog(null, "暂未实现", "暂未实现", JOptionPane.INFORMATION_MESSAGE);
//            }
//        });
//        propMenu.add(savePropMenuItem);

        JMenu helpMenu = new JMenu("帮助");
        menuBar.add(helpMenu);

        JMenuItem readmeMenuItem = new JMenuItem("readme");
        readmeMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("readme.txt");
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
                JOptionPane.showMessageDialog(null, "JFileMonitor " + qyudy.filemonitor.Constants.version + "\nedit by qyudy\nstart from 2018/3", "about", JOptionPane.INFORMATION_MESSAGE);
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
                if (title != null) {
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), title);
                }
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
                new FolderMonitorPanel().addToPane(tabbedPane, pw);
            }
        });
        addMenu.add(addFolderMonitorMenuItem);
        
        JMenuItem addFileMonitorMenuItemMenuItem = new JMenuItem("文件监视器");
        addFileMonitorMenuItemMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                new FileMonitorPanel().addToPane(tabbedPane, pw);
            }
        });
        addMenu.add(addFileMonitorMenuItemMenuItem);
        
        JMenuItem addDelMonitorMenuItemMenuItem = new JMenuItem("删除监视器");
        addDelMonitorMenuItemMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                new DelMonitorPanel().addToPane(tabbedPane, pw);
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
                    .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 232, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(resultScrollPane, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                    .addContainerGap())
        );
        
        new FolderMonitorPanel().addToPane(tabbedPane, pw);

        new FileMonitorPanel().addToPane(tabbedPane, pw);

        new DelMonitorPanel().addToPane(tabbedPane, pw);
        
        frame.getContentPane().setLayout(groupLayout);
    }

}
