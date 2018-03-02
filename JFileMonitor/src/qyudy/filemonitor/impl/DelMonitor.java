package qyudy.filemonitor.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import qyudy.filemonitor.AbstractMonitor;

public class DelMonitor extends AbstractMonitor
{
    private List<File> toDel;
    
    public DelMonitor(String[] toDel) {
        this.toDel = new ArrayList<File>();
        for (String s : toDel)
        {
            this.toDel.add(new File(s));
        }
    }

    @Override
    public void run() {
        if (isRunning())
        {
            try
            {
                for (File f : toDel)
                {
                    if (f.exists())
                    {
                        del(f, true);
                        logger.println("删除成功:" + f);
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
}
