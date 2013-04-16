package nam.doog.sylar;


import java.io.IOException;
import java.util.List;

import nam.doog.sylar.entity.Recruit;
import nam.doog.sylar.search.SearchUtil;
import nam.doog.sylar.spider.Spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	static String indexPath = "./test_index";
	//http://sou.zhaopin.com/Jobs/SearchResult.ashx?bj=160200&sj=053&in=210500&jl=%E5%8C%97%E4%BA%AC&kw=java&sm=0&p=1&sf=0 100 127.0.0.1:5865
	private static Logger log = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args) throws Exception {
		if(args.length>0)
			createAndStatics(args);
		else 
			statics();
	}
	private static void statics() throws IOException {
		SearchUtil.statics(indexPath);
	}
	/**
	 * @param args : args[0] 搜索url
	 * 							   args[2] 代理 如127.0.0.1:5865
	 * @throws Exception
	 */
	public static void createAndStatics(String[] args) throws Exception {
		if(args==null || args.length<1){
			usage();
			return;
		}
			//throw new IllegalArgumentException("请别忘了指定智联搜索url哦~~");
		String searchUrl = args[0];
		boolean isProxy = false;
		String proxyHost = null;
		int proxyPort = 0;
		int count = -1;// 需要爬取的url数量，默认所有
		if(args.length>1){
			count = Integer.parseInt(args[1]);
		}
		if(args.length>2){
			isProxy = true ;
			String[] proxyArgs = args[2].split(":");
			proxyHost = proxyArgs[0];
			proxyPort = Integer.parseInt(proxyArgs[1]);
		}
		Spider s = null ;
		if(isProxy){
			log.debug("使用HTTP代理："+args[2]);
			s = new Spider(proxyHost,proxyPort);//"127.0.0.1",5865
		}
		else{ 
			s = new Spider();
		}
		List<Recruit> recruits = s.crawl(searchUrl,count);
		SearchUtil.createIndex(indexPath, recruits);
		SearchUtil.statics(indexPath);
//		String keyword = "weblogic";
//		SearchUtil.search(indexPath, keyword);
		
	}
	private static void usage() {
		System.out.println("Usage : java Main url count [HttpProxyHost:HttpProxyPort]");
		System.out.println("             url : 智联搜索地址 如sou.zhaopin.com/....&kw=java&sm=0...");
		System.out.println("             count : 索引量 ");
		System.out.println("             proxy : HTTP代理 如127.0.0.1:5865 ");
	}
}
