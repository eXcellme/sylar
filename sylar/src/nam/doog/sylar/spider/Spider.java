package nam.doog.sylar.spider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Spider {
	private ToVisitList<String> tovisit = new ToVisitList<String>(512);
	private VisitedList<String> visited = new VisitedList<String>(512);
	public void crawl(String searchUrl) throws Exception{
		// 1 add href to tovisitlist
		Document doc = Jsoup.parse(new URL(searchUrl), 1000*3);
		Elements eles = doc.select(".search-result-tab").select(".Jobname").select("a[href]");
		for(Element ele : eles){
			String href = ele.attr("href");
			tovisit.add(href);
		}
		String url = tovisit.poll();
		visited.add(url);
		visit(url);
		
	}
	/**
	 * 爬去一个网页
	 * @param url
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void visit(String url) throws MalformedURLException, IOException{
		Document doc = Jsoup.parse(new URL(url),1000);
		
		
	}
	public static void main(String[] args) throws Exception {
		String searchUrl = "http://sou.zhaopin.com/Jobs/SearchResult.ashx?bj=160000&sj=044&in=210500&pd=1&jl=%E5%8C%97%E4%BA%AC&kw=java&sm=0&p=1&sf=5000&st=8000&we=0103&el=4&et=2";
		Spider s = new Spider();
		s.crawl(searchUrl);
		
	}
}
