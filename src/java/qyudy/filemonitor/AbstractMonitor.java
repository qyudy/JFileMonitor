package qyudy.filemonitor;

import java.io.File;
import java.io.PrintWriter;

public abstract class AbstractMonitor implements Runnable
{
    /**
     * 每次循环至少的等待时间
     */
    private static final long MIN_TIME_WAIT = 1000L;
    /**
     * 每次循环之间的等待时间
     */
    private long timeWait = MIN_TIME_WAIT;
    public long getTimeWait() {
        return timeWait;
    }
    public void setTimeWait(long timeWait) {
        if (timeWait < MIN_TIME_WAIT)
        {
            timeWait = MIN_TIME_WAIT;
        }
        this.timeWait = timeWait;
    }

    /**
     * 取true时循环等待时间从每个循环启动时开始计时，否则从任务处理完成时开始计时
     */
    private boolean completeCycle = false;
    public boolean isCompleteCycle() {
        return completeCycle;
    }
    public void setCompleteCycle(boolean completeCycle) {
        this.completeCycle = completeCycle;
    }
    
    /**
     * 运行状态
     */
    private boolean running = false;
    public boolean isRunning() {
        return running;
    }
    /**
     * 日志处理器
     */
    protected PrintWriter logger = new PrintWriter(System.out);
    public PrintWriter getLogger() {
        return logger;
    }
    public void setLogger(PrintWriter logger) {
        this.logger = logger;
    }

    /**
     * 异步处理时的任务线程
     */
    private Thread thread;
    
    public AbstractMonitor() {
    }
    
    public AbstractMonitor(PrintWriter logger) {
        this.logger = logger;
    }
    
    public AbstractMonitor(PrintWriter logger, long timeWait, boolean completeCycle) {
        this(logger);
        setTimeWait(timeWait);
        setCompleteCycle(completeCycle);
    }
    
    /**
     * 开启同步任务，只修改运行状态，实际运行需要调用者处理
     */
    public void startSync() {
        running = true;
    }

    /**
     * 开启异步任务，一个任务实例只能运行一次
     */
    public void startAsync() {
        if (!running)
        {
            running = true;
            thread = new Thread(new Runnable()
            {
                @Override
                public void run() {
                    run0();
                }
            });
            thread.start();
            logger.println(this.getClass().getSimpleName() + " started");
        }
    }
    
    /**
     * 结束异步任务，也能暂时将同步任务记为失效
     */
    public void stop() {
        if (running) {
            running = false;
            if (thread != null)
            {
                thread.interrupt();
                thread = null;
            }
        }
    }

    private void run0() {
        preRun();
        for (;;)
        {
            try
            {
                if (!running)
                {
                    break;
                }
                long startTime = System.currentTimeMillis();
                run();
                try
                {
                    if (completeCycle)
                    {
                        long wait = timeWait + startTime - System.currentTimeMillis();
                        if (wait < MIN_TIME_WAIT)
                        {
                            wait = MIN_TIME_WAIT;
                        }
                        Thread.sleep(wait);
                    }
                    else
                    {
                        Thread.sleep(timeWait);
                    }
                }
                catch (InterruptedException e)
                {
                    // 用户主动停止，不需要处理异常
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
        logger.println(this.getClass().getSimpleName() + " stopped");
    }
    
    public void preRun() {}
    
    public static void del(File del, boolean delFolder) {
        if (del.isDirectory())
        {
            for (File f : del.listFiles())
            {
                del(f, true);
            }
        }
        if (delFolder)del.delete();
    }
}
