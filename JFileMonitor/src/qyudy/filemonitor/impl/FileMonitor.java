package qyudy.filemonitor.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import qyudy.filemonitor.AbstractMonitor;

public class FileMonitor extends AbstractMonitor
{
    private File fileToMonit;
    private File fileCopyTo;
    private long lastModified = Long.MIN_VALUE;

    public FileMonitor(String fileToMonit, String fileCopyTo) {
        this.fileToMonit = new File(fileToMonit);
        if (!this.fileToMonit.isAbsolute()) throw new RuntimeException("监视文件必须设置为绝对路径:" + fileToMonit);
        this.fileCopyTo = new File(fileCopyTo);
        if (!this.fileCopyTo.isAbsolute()) throw new RuntimeException("拷贝文件必须设置为绝对路径:" + fileCopyTo);
        long lastModified;
        if (this.fileToMonit.exists() && this.fileCopyTo.exists() && (lastModified = this.fileToMonit.lastModified()) == this.fileCopyTo.lastModified())
        {
            this.lastModified = lastModified;
        }
    }

    @Override
    public void run() {
        if (isRunning())
        {
            try
            {
                if (fileToMonit.exists())
                {
                    long lastModified = this.fileToMonit.lastModified();
                    if (this.lastModified != lastModified)
                    {
                        fileCopyTo.mkdirs();
                        Files.copy(fileToMonit.toPath(), fileCopyTo.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        this.lastModified = lastModified;
                        logger.println("拷贝文件成功:" + fileCopyTo);
                    }
                }
                else if (fileCopyTo.exists())
                {
                    Files.deleteIfExists(fileCopyTo.toPath());
                    logger.println("删除文件成功:" + fileCopyTo);
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

}
