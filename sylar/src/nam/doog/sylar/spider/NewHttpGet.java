package nam.doog.sylar.spider;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.client.methods.HttpGet;

/**
 * 为HttpGet增添了特殊方法
 * @author "bflee"
 *
 */
public class NewHttpGet extends HttpGet {
		/**
		 *  the querystrings of get method
		 */
		HashMap<String,String> queries = new HashMap<String,String>();
		
		public NewHttpGet(String url){
			super(url);
			String q = url.substring(url.indexOf("?")+1);
			String[] qargs = q.split("&");
			for(String qarg : qargs){
				String key = qarg.substring(0,qarg.indexOf("="));
				String value = qarg.substring(qarg.indexOf("=")+1);
				queries.put(key,value);
			}
		}
		public String getQueryStringValue(String key){
			return queries.get(key);
		}
		public void setQueryStringValue(String key,String value){
			queries.put(key,value);
			super.setURI(URI.create(getUrlString()));
		}
		/**
		 * 获取url完整地址
		 */
		public String getUrlString(){
			StringBuilder sb = new StringBuilder();
			URI uri = this.getURI();
			if(uri.getScheme()!=null)
				sb.append(uri.getScheme()).append(":");
			if(uri.getHost()!=null){
				String host = uri.getHost();
				sb.append("//");
				sb.append(host);
			}
			if(uri.getPath()!=null){
				sb.append(uri.getPath());
			}
			sb.append("?");
			for(Entry<String,String> en : queries.entrySet()){
				sb.append(en.getKey()).append("=").append(en.getValue()).append("&");
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}
		public static void main(String[] args) {
			String url = "http://www.abc.com?a=3&b=5&c=9&b=1";
			NewHttpGet g = new NewHttpGet(url);
			System.out.println(g.getUrlString());
			String a = "abc";
			String b = "abc&";
			String[] as = a.split("&");
			String[] bs = b.split("&");
			System.out.println("as:"+Arrays.toString(as)+","+Arrays.toString(bs));
		}
}
