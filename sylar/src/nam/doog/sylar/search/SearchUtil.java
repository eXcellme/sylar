package nam.doog.sylar.search;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import nam.doog.sylar.entity.Recruit;

import org.apache.commons.io.FileUtils;
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
import org.apache.lucene.index.Term;
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
			 if(r.getJobDescription()!=null)
				 doc.add(new TextField("job_desc", r.getJobDescription(), Store.YES));
			 if(r.getJobName()!=null)
				 doc.add(new StringField("job_name",r.getJobName(),Store.YES));
			 if(r.getCompanyName()!=null)
				 doc.add(new StringField("company_name",r.getCompanyName(),Store.YES));
			 writer.addDocument(doc);
		 }
		 // 合一
		 writer.forceMerge(1);
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
		 public static void statics(String indexPath) throws IOException{
			 IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
			   //显示document数
			   log.debug(new Date()+"");
			   log.debug(reader+"该索引共含 "+reader.numDocs()+"篇文档");
			   // 对keywords.dic中的词进行整个索引中的词频统计
			   List<String> lines = FileUtils.readLines( new File("src/keywords.dic"),"utf-8");
			   
			   Map< String,Long> tm = new TreeMap<String,Long>();
			   Map< String,Long> tm2 = new TreeMap<String,Long>();
			   for(String line : lines){
				   long tf = reader.totalTermFreq(new Term("job_desc",line));
				   long df = reader.docFreq(new Term("job_desc",line));
				   if(tf>0){
					   tm.put(line,tf);
				   }
				   if(df>0){
					   tm2.put(line, df);
				   }
			   }
			   tm = sortMapByValue(tm,true);
			   log.info("文档频率：");
			   tm2 = sortMapByValue(tm2,true);
			   System.out.println(tm);
			   System.out.println(tm2);
		 }
		 
		 
		 /** 排序map
		 * @param tm
		 * @param desc 是否降序
		 * @return
		 */
		public static Map<String, Long> sortMapByValue(Map<String, Long> tm,final boolean desc) {
			 List<Map.Entry<String, Long>> list = new LinkedList<Map.Entry<String,Long>>(tm.entrySet());
			 Collections.sort(list, new Comparator<Map.Entry<String,Long>>() {
				@Override
				public int compare(Entry<String, Long> o1,
						Entry<String, Long> o2) {
					if(!desc)
						return (int) (o1.getValue() - o2.getValue());
					else
						return  (int) (o2.getValue() - o1.getValue());
				}
			 });
			 Map<String, Long>  ret = new LinkedHashMap<String, Long> ();
			 for(Entry<String,Long> e : list){
					 ret.put(e.getKey(),e.getValue());
					 log.debug(e.getKey() + "出现："+e.getValue()+"次");
			 }
			 return ret;
		}

		public static void extractDataFromIndex(String indexPath,String filename) throws IOException{
			 IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
			}
	 public static void main(String[] args) throws IOException, ParseException {
		 String indexPath = "D:/test_index";
//		search(indexPath, "java");''
		statics(indexPath);
	}
}
