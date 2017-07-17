package cn.ctv.ctv;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.dj.ctv.R;
public class Common {
	public static int log_online_id = 0;
	private int haha;
	public static int getVersionCode(Context context)//获取版本号(内部识别号)  
	{  
	    try {  
	        PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
	        return pi.versionCode;  
	    } catch (NameNotFoundException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	        return 0;  
	    }  
	}


	/**
	 * 获取当前系统连接网络的网卡的mac地址
	 * @return
	 */
	public static final String getMac() {
		byte[] mac = null;
		StringBuffer sb = new StringBuffer();
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();

				while (address.hasMoreElements()) {
					InetAddress ip = address.nextElement();
					if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address) || ip.isLoopbackAddress())
						continue;
					if (ip.isSiteLocalAddress())
						mac = ni.getHardwareAddress();
					else if (!ip.isLinkLocalAddress()) {
						mac = ni.getHardwareAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		if(mac != null){
			for(int i=0 ;i<mac.length ;i++){
				sb.append(parseByte(mac[i]));
			}
			return sb.substring(0, sb.length()-1);
		}else{
			return null;
		}
	}

	private static String parseByte(byte b) {
		String s = "00" + Integer.toHexString(b)+":";
		return s.substring(s.length() - 3);
	}
	public static String getApiUrl(Context context) {
			return context.getResources().getString(R.string.server_prefix);
	}


	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
		} else {
			// 如果仅仅是用来判断网络连接
			// 则可以使用 cm.getActiveNetworkInfo().isAvailable();
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static final boolean ping(String ip) {

		String result = null;

		try {

			Process p = Runtime.getRuntime().exec("/system/bin/ping -c 1 " + ip);// ping3次
																				// ——ping
																				// -c
																				// 3
																				// -w
																				// 100

			// 读取ping的内容，可不加。

			
			 /* InputStream input = p.getInputStream();
			  
			  BufferedReader in = new BufferedReader(new
			  InputStreamReader(input));
			  
			  StringBuffer stringBuffer = new StringBuffer();
			  
			  String content = "";
			  
			  while ((content = in.readLine()) != null) {
			  
			  stringBuffer.append(content);
			  
			 }
			  
			  Log.i("TTT", "result content : " + stringBuffer.toString());*/
			 

			// PING的状态

			int status = p.waitFor();

			if (status == 0) {

				result = "successful~";

				return true;

			} else {

				result = "failed~ cannot reach the IP address";

			}

		} catch (IOException e) {

			result = "failed~ IOException";

		} catch (InterruptedException e) {

			result = "failed~ InterruptedException";

		} finally {

		}

		return false;

	}
}
