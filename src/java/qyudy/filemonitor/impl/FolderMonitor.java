package qyudy.filemonitor.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import qyudy.filemonitor.AbstractMonitor;

public class FolderMonitor extends AbstractMonitor
{
    private List<File> folderToMonit;
    private File folderCopyTo;
    private Map<File, Long> filelastModifieds = new HashMap<>();
    private Map<File, File> fileRoots = new HashMap<>();
    /**
     * 是否同步删除，开启时监视器开启时拷贝目录会被清理，而且可以保证目录也被同步删除（关闭时为避免删除顺序导致出错，不作删除），关闭时拷贝文件夹内比监视文件夹多的内容不会被删除，运行时的删除操作不会受这个开关影响
     */
    private boolean delIfNotSync;

    public FolderMonitor(String[] folderToMonit, String folderCopyTo, boolean delIfNotSync) {
        this.folderToMonit = new ArrayList<File>();
        for (String s : folderToMonit)
        {
            File f = new File(s);
            if (!f.isAbsolute()) throw new RuntimeException("监视文件夹必须设置为绝对路径:" + s);
            this.folderToMonit.add(f);
        }
        this.folderCopyTo = new File(folderCopyTo);
        if (!this.folderCopyTo.isAbsolute()) throw new RuntimeException("拷贝文件夹必须设置为绝对路径:" + folderCopyTo);
        this.delIfNotSync = delIfNotSync;
    }
    
    @Override
    public void preRun() {
        if (delIfNotSync)
        {
            DelMonitor.del(folderCopyTo, false);
            logger.println("初始化清理成功:" + folderCopyTo);
        }
        else
        {
            logger.println("预清理开关为" + delIfNotSync + "跳过清理操作");
        }
    }

    @Override
    public void run() {
        if (isRunning())
        {
            try
            {
                Set<File> notExistFile = new HashSet<>(filelastModifieds.keySet());
                for (File folder : folderToMonit)
                {
                    runInFolder(folder, folder, notExistFile);
                }
                for (File f : notExistFile)
                {
                    try
                    {
                        Path fileTo = folderCopyTo.toPath().resolve(fileRoots.get(f).toPath().relativize(f.toPath()));
                        Files.deleteIfExists(fileTo);
                        filelastModifieds.remove(f);
                        logger.println("删除文件成功:" + fileTo);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace(logger);
                    }
                }
            }
            catch (Exception e)
            {
                if (logger != null)
                {
                    e.printStackTrace(logger);
                }
            }
        }
    }

    private void runInFolder(File rootFolder, File folder, Set<File> notExistFile) {
        if (delIfNotSync && !folder.exists())
        {
            Path folderTo = folderCopyTo.toPath().resolve(rootFolder.toPath().relativize(folder.toPath()));
            DelMonitor.del(folderTo.toFile(), true);
            logger.println("删除目录成功:" + folderTo);
            return;
        }
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File f : files)
        {
            if (f.isFile())
            {
                notExistFile.remove(f);
                Long lastModified = filelastModifieds.get(f);
                long currentModified = f.lastModified();
                Path fPath = f.toPath();
                Path fileTo = folderCopyTo.toPath().resolve(rootFolder.toPath().relativize(fPath));
                boolean exist = fileTo.toFile().exists();
                if (lastModified == null || lastModified != currentModified || !exist)
                {
                    fileRoots.put(f, rootFolder);
                    try
                    {
                        fileTo.toFile().mkdirs();
                        Files.copy(fPath, fileTo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        filelastModifieds.put(f, currentModified);
                        logger.println("拷贝文件成功:" + fileTo);
                    }
                    catch (IOException e)
                    {
                        if (logger != null)
                        {
                            e.printStackTrace(logger);
                        }
                    }
                }
            }
            else if (f.isDirectory())
            {
                runInFolder(rootFolder, f, notExistFile);
            }
        }
    }
}
