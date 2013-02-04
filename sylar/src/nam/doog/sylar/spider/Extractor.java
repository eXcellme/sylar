package nam.doog.sylar.spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import nam.doog.sylar.entity.Recruit;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  抽取信息的类，如一个简历页面的信息
 *
 */
public class Extractor {
	private static Logger log = LoggerFactory.getLogger(Extractor.class);
	public static Recruit extractData(String pageUrl) throws MalformedURLException, IOException{
		Document doc = pageDoc(pageUrl);
		Elements intros = doc.select(".company-introduction");
		Element jobdescEle = intros.first();
		Recruit r = new Recruit();
		if(jobdescEle!=null)
			r.setJobDescription(jobdescEle.text());
		Element jobNameEle = doc.select(".Terminal-title").first();
		if(jobNameEle!=null)
			r.setJobName(jobNameEle.text());
		return r;
	}
	public static void main(String[] args) throws MalformedURLException, IOException {
		String url = "http://jobs.zhaopin.com/beijing/JA|VA%E5%BC%80%E5%8F%91%E5%B7%A5%E7%A8%8B%E5%B8%88_336124514250080.htm";
		url = "http://jobs.zhaopin.com/beijing/java%E5%BC%80%E5%8F%91%E5%B7%A5%E7%A8%8B%E5%B8%88_415833414250002.htm";
		url = "http://jobs.zhaopin.com/beijing/java%e7%a8%8b%e5%ba%8f%e5%91%98_000579890250163.htm";
		Extractor.proxy("127.0.0.1", 5865);
		Recruit r = extractData(url);
		System.out.println(r);
	}
	
	/** 获取某个页面对应的doc对象
	 * @param pageUrl
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static Document pageDoc(String pageUrl) throws ClientProtocolException, IOException{
		if(needProxy){
			HttpHost proxy = new HttpHost(proxyHost,proxyPort);
			DefaultHttpClient hc = new DefaultHttpClient();
			hc.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			HttpResponse res = hc.execute(new HttpGet(pageUrl));
			String html = EntityUtils.toString(res.getEntity());
			return Jsoup.parse(html);
		}else{
			return Jsoup.parse(new URL(pageUrl),1000*3);
		}
	}
	private static boolean needProxy = false;
	private static String proxyHost ;
	private static int proxyPort ; 
	public static void proxy(String host,int port){
		needProxy = true ;
		proxyHost = host ;
		proxyPort = port ;
	}
}
