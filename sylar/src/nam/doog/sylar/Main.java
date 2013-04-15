package nam.doog.sylar;


import java.util.List;

import nam.doog.sylar.entity.Recruit;
import nam.doog.sylar.search.SearchUtil;
import nam.doog.sylar.spider.Spider;

public class Main {
	
	/**
	 * @param args : args[0] 搜索url
	 * 							   args[1] 代理 如127.0.0.1:5865
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String indexPath = "D:/test_index";
		if(args==null || args.length<1){
			usage();
			return;
		}
			//throw new IllegalArgumentException("请别忘了指定智联搜索url哦~~");
		String searchUrl = args[0];
		boolean isProxy = false;
		String proxyHost = null;
		int proxyPort = 0;
		if(args.length>1){
			String[] proxyArgs = args[1].split(":");
			proxyHost = proxyArgs[0];
			proxyPort = Integer.parseInt(proxyArgs[1]);
		}
		Spider s = null ;
		if(isProxy)
			s = new Spider(proxyHost,proxyPort);//"127.0.0.1",5865
		else 
			s = new Spider();
		List<Recruit> recruits = s.crawl(searchUrl);
		SearchUtil.createIndex(indexPath, recruits);
		SearchUtil.statics(indexPath);
//		String keyword = "weblogic";
//		SearchUtil.search(indexPath, keyword);
		
	}

	private static void usage() {
		System.out.println("Usage : java  -jar sylar.jar url [HttpProxyHost:HttpProxyPort]");
	}
}
