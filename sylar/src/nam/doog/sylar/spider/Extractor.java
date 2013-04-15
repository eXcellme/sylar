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
//		String html = doc.html();
//		FileUtils.writeStringToFile(new File("D:/test_index/file"),html ,"utf-8");
		// 工作描述
		Elements intros = doc.select(".company-introduction"); // Jsoup有时不能提取出结果
		Element jobdescEle = intros.first();
		System.out.println(jobdescEle);
		Recruit r = new Recruit();
		if(jobdescEle!=null)
			r.setJobDescription(jobdescEle.text());
		else 
			return null;
		// 工作名称
		Element jobNameEle = doc.select(".Terminal-title").first();
		if(jobNameEle!=null)
			r.setJobName(jobNameEle.text());
		else return null;
		// 公司名称 <dt>后的<dd>
		Element comanyNameEle = doc.getElementsMatchingOwnText("公司名称：").first().nextElementSibling();
		String companyName = comanyNameEle.text();
		r.setCompanyName(companyName);
		return r;
	}
	public static void main(String[] args) throws MalformedURLException, IOException {
		String url = "http://jobs.zhaopin.com/beijing/JA|VA%E5%BC%80%E5%8F%91%E5%B7%A5%E7%A8%8B%E5%B8%88_336124514250080.htm";
		url = "http://jobs.zhaopin.com/beijing/java%E5%BC%80%E5%8F%91%E5%B7%A5%E7%A8%8B%E5%B8%88_415833414250002.htm";
		url = "http://jobs.zhaopin.com/beijing/高级JAVA 工程师_336880117250099.htm";
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
			// 转义
			// TODO 地址转义问题 （|）
//			pageUrl = URLEncoder.encode(pageUrl, "gb2312");
//			pageUrl = URLDecoder.decode(pageUrl,"utf-8");
//			pageUrl = URLEncoder.encode(pageUrl,"utf-8");
			pageUrl = escape(pageUrl);
			
			System.out.println("pageUrl:"+pageUrl);
			HttpGet get = new HttpGet(pageUrl);
			HttpResponse res = hc.execute(get);
			String html = EntityUtils.toString(res.getEntity());
			return Jsoup.parse(html);
		}else{
			return Jsoup.parse(new URL(pageUrl),1000*3);
		}
	}
	private static String escape(String pageUrl) {
		System.out.println(pageUrl);
		if(pageUrl!=null && pageUrl.toLowerCase().startsWith("http://")){
			try {
//				pageUrl = "http://"+URLEncoder.encode(pageUrl.substring("http://".length()),"gb2312");
				pageUrl = pageUrl.replace("|", "%7C").replace(" ", "%20");
			} catch (RuntimeException e) {
				log.error("超链接转码异常",e);
				return null;
			}
		}
		return pageUrl;
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
