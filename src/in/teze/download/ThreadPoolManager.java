package in.teze.download;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**功能：线程池管理
 * ThreadPoolManager
 * @author   by Fooyou  2014年6月6日   上午11:22:19
 */
public class ThreadPoolManager {

	protected static final String TAG = "MyThreadPoolManager----";
	private ThreadPoolExecutor poolExecutor;
	private int corePoolSize=2;
	private int maximumPoolSize=5;
	private int keepAliveTime=0;
	private Queue<Runnable> runnables=new LinkedList<Runnable>();
	private ScheduledExecutorService scheduledExecutorService;
	private static ThreadPoolManager instance;
	
	private final RejectedExecutionHandler defaultHandler = new AbortPolicy(){

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
			Loger.i(TAG, "rejectedExecution");
			try {
				if (r!=null) {
					runnables.offer(r);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			
		}
		
	};
	private Runnable reloadCommand=new Runnable() {
		
		@Override
		public void run() {
			if (runnables.size()>0) {
				Runnable runnable=runnables.poll();
				poolExecutor.execute(runnable);
				Loger.i(TAG, "reload");
			}
		}
	};

	private ThreadPoolManager() {
		Loger.i(TAG, "init");
		poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(10), defaultHandler);
		scheduledExecutorService=Executors.newScheduledThreadPool(2);
		scheduledExecutorService.scheduleAtFixedRate(reloadCommand, 0,1, TimeUnit.SECONDS);
		instance=this;
	}
	
	public static ThreadPoolManager getInstance(){
		if (instance==null) {
			new ThreadPoolManager();
		}
		return instance;
	}
	
	public void addThread(Runnable runnable){
		Loger.i(TAG, "addThread");
		poolExecutor.execute(runnable);
	}
	
	public void removeThread(Runnable runnable){
		Loger.i(TAG, "removeThread");
		if (runnable!=null) {
			poolExecutor.remove(runnable);
		}
	}
	
	
}
