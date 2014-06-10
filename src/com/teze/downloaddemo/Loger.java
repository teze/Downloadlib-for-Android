
package com.teze.downloaddemo;

import android.util.Log;




/**功能：不带TAG 参数的 运行速度慢，适合临时调试，带TAG参数的，运行速度较快。 
 * Loger
 * @author   by Fooyou  2014年6月3日   上午9:00:39
 */
public class Loger{

	private static String className;
	private static String methodName;
	private static int lineNumber;
	private static boolean isDebug = BuildConfig.DEBUG;
	
    private Loger(){
        /* Protect from instantiations */
    }

	public static boolean isDebuggable(){
		return isDebug;
	}

	private static String createLog(String log){

		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(methodName);
		buffer.append(":");
		buffer.append(lineNumber);
		buffer.append("]");
		buffer.append(" >> ");
		buffer.append(log);

		return buffer.toString();
	}
	
	private static void getMethodNames(StackTraceElement[] sElements){
		className = sElements[1].getFileName();
		methodName = sElements[1].getMethodName();
		lineNumber = sElements[1].getLineNumber();
	}

	public static void e(String message){
		if (!isDebuggable())
			return;

		// Throwable instance must be created before any methods  
		getMethodNames(new Throwable().getStackTrace());
		Log.e(className, createLog(message));
	}

	
	public static void i(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.i(className, createLog(message));
	}
	
	public static void d(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.d(className, createLog(message));
	}
	
	public static void v(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.v(className, createLog(message));
	}
	
	public static void w(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.w(className, createLog(message));
	}
	
	public static void wtf(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.wtf(className, createLog(message));
	}
	
	public static void print(){
		if (!isDebuggable())
			return;
		
		getMethodNames(new Throwable().getStackTrace());
		Log.v(className, "");
	}
	
	
	public static void d(String tag, String msg) {
		if (isDebug) {
			Log.d(tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (isDebug) {
			Log.d(tag, msg, tr);
		}
	}

	public static void e(String tag, String msg) {
		if (isDebug) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (isDebug) {
			Log.e(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (isDebug) {
			Log.i(tag, msg, tr);
		}
	}

	public static void log() {
		if (isDebug) {
			System.out.println();
		}
	}

	public static void log(Exception e) {
		if (isDebug) {
			System.out.println(e.toString());
		}
	}

	public static void log(String log) {
		if (isDebug) {
			System.out.println(log);
		}
	}

	public static void v(String tag, String msg) {
		if (isDebug) {
			Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (isDebug) {
			Log.v(tag, msg, tr);
		}
	}

	public static void w(String tag, String msg) {
		if (isDebug) {
			Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (isDebug) {
			Log.w(tag, msg, tr);
		}
	}

	public static void w(String tag, Throwable tr) {
		if (isDebug) {
			Log.w(tag, tr);
		}
	}

	public static void printMemory() {
		Loger.log("Memory usage >>  " + getMemory());
	}
	
	private static String getMemory() {
		
		long totalMemory= Runtime.getRuntime().totalMemory();
		long freeMemory= Runtime.getRuntime().freeMemory();
		long maxMemory= Runtime.getRuntime().maxMemory() ;
		return (totalMemory - freeMemory) / 1024L
				+ " KB used / " + totalMemory / 1024L+"  KB total ."
				+ " Max  memory available to the VM is " + maxMemory / 1024L + " KB.";
	}
	
}
