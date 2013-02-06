package nam.doog.sylar.spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import nam.doog.sylar.entity.Recruit;
import nam.doog.sylar.search.SearchUtil;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Spider {
	private Logger log = LoggerFactory.getLogger(Spider.class);
	private ToVisitList<String> tovisit = new ToVisitList<String>(5120);
	private VisitedList<String> visited = new VisitedList<String>(5120);
	private boolean needProxy ; 
	private String proxyHost ;
	private int proxyPort ;
	public Spider(){
		
	}
	public Spider(String proxyHost,int proxyPort){
		this.needProxy = true ; 
		this.proxyHost = proxyHost ;
		this.proxyPort = proxyPort ;
	}
	/**
	 * crawl with proxy
	 * @param searchUrl
	 * @param proxy
	 * @throws Exception
	 */
	private void crawlSites(String searchUrl,HttpHost proxy) throws Exception{
		DefaultHttpClient hc = new DefaultHttpClient();
		if(proxy!=null)
			// 因为公司网络有代理(NTLM，我自己又搭了个NTLM转HTTP的代理)，
			hc.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		boolean hasNextPage = true ;
		
		NewHttpGet get = new NewHttpGet(searchUrl);
		while(hasNextPage){
			
			HttpResponse res = hc.execute(get);
			String html = EntityUtils.toString(res.getEntity());
			Document doc = Jsoup.parse(html);
			Elements nextPageNodes = doc.select(".next-page");
			log.debug("nextPageNodes:"+nextPageNodes);
			if(nextPageNodes.size()==0)
				hasNextPage = false;
			Elements eles = doc.select(".search-result-tab").select(".Jobname").select("a");
			for(Element ele : eles){
				String href = ele.attr("href");
				tovisit.add(href);
			}
			// 请求下一页

			String pageIdx = get.getQueryStringValue("p");
			System.out.println("pageIdx:"+pageIdx);
			int idx = Integer.parseInt(pageIdx);
			get.setQueryStringValue("p", String.valueOf(++idx));
			
		}
	}
	/**
	 * crawl without proxy
	 * @param searchUrl
	 * @throws Exception
	 */
	private void crawlSites(String searchUrl) throws Exception{
		boolean hasNextPage = true ;
		while(hasNextPage){
			Document doc = Jsoup.parse(new URL(searchUrl),1000*3);
			Elements nextPageNodes = doc.select(".next-page");
			log.debug("nextPageNodes:"+nextPageNodes);
			if(nextPageNodes.size()==0)
				hasNextPage = false;
			Elements eles = doc.select(".search-result-tab").select(".Jobname").select("a");
			for(Element ele : eles){
				String href = ele.attr("href");
				tovisit.add(href);
			}
			// 请求下一页
			int indexOfP = searchUrl.indexOf("&p=") == -1 ? searchUrl.indexOf("?p=") : searchUrl.indexOf("&p=");
			String pageIdx = searchUrl.substring(indexOfP+3).split("&")[0];
			System.out.println("pageIdx:"+pageIdx);
			int idx = Integer.parseInt(pageIdx);
			searchUrl = searchUrl.replace("&p="+pageIdx, "&p="+String.valueOf(++idx));
		}
	}
	/**
	 * 爬取简历链接信息，
	 * @param searchUrl
	 * @throws Exception
	 */
	public List<Recruit> crawl(String searchUrl) throws Exception{
		// 获取搜索出的站点，保存在tovisit中
		long crawlSitesTime1 = System.currentTimeMillis();
		if(needProxy){
			Extractor.proxy(proxyHost,proxyPort);
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			crawlSites(searchUrl, proxy);
		}else{
			crawlSites(searchUrl);
		}
		long crawlSitesTime2 = System.currentTimeMillis();
		log.info("招聘页面爬取完毕，爬取量："+tovisit.size()+"，耗时："+(crawlSitesTime2-crawlSitesTime1)+"毫秒");
		// 提取每个页面的信息
		String pageUrl = "";
		List<Recruit> recruits = new LinkedList<Recruit>(); 
		while((pageUrl = tovisit.poll())!=null){
			visited.add(pageUrl);
//			if(needProxy)
				
			// 提取一个页面的信息并封装成对象
			try{
			Recruit r = Extractor.extractData(pageUrl);
			if(r!=null)
				recruits.add(r);
			}catch(Exception e){
				log.error("提取页面封装对象发生严重错误",e);
			}
		}
		log.info("招聘信息提取封装完毕，提取量："+recruits.size()+"，耗时："+(System.currentTimeMillis()-crawlSitesTime2)+"毫秒");
		return recruits;
	}
	/**
	 * 爬取一个网页
	 * @param url
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void visit(String pageUrl) throws MalformedURLException, IOException{
		Document doc = Jsoup.parse(new URL(pageUrl),1000);
		
		
	}
	public static void main(String[] args) throws Exception {
		String indexPath = "D:/test_index";
		String searchUrl = "http://sou.zhaopin.com/Jobs/SearchResult.ashx?bj=160200&sj=053&in=210500&jl=%E5%8C%97%E4%BA%AC&kw=java&sm=0&p=1&sf=0";
		Spider s = new Spider("127.0.0.1",5865);
		List<Recruit> recruits = s.crawl(searchUrl);
		SearchUtil.createIndex(indexPath, recruits);
		SearchUtil.statics(indexPath);
//		String keyword = "weblogic";
//		SearchUtil.search(indexPath, keyword);
		
	}
}
