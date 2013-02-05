package nam.doog.sylar.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nam.doog.sylar.entity.Recruit;
import nam.doog.sylar.spider.Extractor;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * @author bflee
 *
 */
public class SearchUtil {
	private static Logger log = LoggerFactory.getLogger(SearchUtil.class);
	 public static void createIndex(String indexPath,List<Recruit> list) throws IOException{
		 long s1 = System.currentTimeMillis();
		 IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_41,new IKAnalyzer());
		 conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
		 Directory dir = FSDirectory.open(new File(indexPath));
		 IndexWriter writer = new IndexWriter(dir,conf);
		 for(Recruit r : list){
			 System.out.println(r);
			 Document doc =new Document();
			 doc.add(new TextField("job_desc", r.getJobDescription(), Store.YES));
			 doc.add(new StringField("job_name",r.getJobName(),Store.YES));
			 doc.add(new StringField("company_name",r.getCompanyName(),Store.YES));
			 writer.addDocument(doc);
		 }
		 writer.commit();
		 writer.close();
		 long s2 = System.currentTimeMillis();
		 log.info("建立索引完毕，耗时："+(s2-s1)+"毫秒");
	 }
	 
	 public static void search(String indexPath,String keyword) throws IOException, ParseException{
		 IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
		 IndexSearcher searcher = new IndexSearcher(reader);
		 Analyzer analyzer = new IKAnalyzer();
		 QueryParser parser = new QueryParser(Version.LUCENE_41, "job_desc", analyzer);
		 Query query = parser.parse(keyword);
//		 Query query = new TermQuery(new Term(""));
		 TopDocs docs = searcher.search(query, 50);
		 for(int i=0;i<docs.totalHits;i++){
			 Document doc = searcher.doc(docs.scoreDocs[i].doc);
			 String jobName = doc.get("job_name");
			 String jobDesc = doc.get("job_desc");
			 String comName = doc.get("company_name");
			 log.debug("Search Result : "+jobName+"\n\t"+jobDesc+"\n\t"+comName);
		 }
		 
	 }
	 
	 public static void main(String[] args) throws IOException, ParseException {
		 String indexPath = "D:/test_index";
		 String pageUrl = "http://jobs.zhaopin.com/beijing/JA|VA%E5%BC%80%E5%8F%91%E5%B7%A5%E7%A8%8B%E5%B8%88_336124514250080.htm";
		 Extractor.proxy("127.0.0.1", 5865);
		 Recruit r = Extractor.extractData(pageUrl);
		 System.out.println(r);
		 List<Recruit> list = new ArrayList<Recruit>();
		 list.add(r);
		 createIndex(indexPath, list);
		search(indexPath, "java");
	}
}
