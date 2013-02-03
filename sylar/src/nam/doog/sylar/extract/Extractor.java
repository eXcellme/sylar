package nam.doog.sylar.extract;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Extractor {
	Logger log = LoggerFactory.getLogger(Extractor.class);
	public String extractData(String url) throws MalformedURLException, IOException{
		
		return null;
	}
	public static void main(String[] args) throws MalformedURLException, IOException {
		Extractor e = new Extractor();
		String url = "http://sou.zhaopin.com/Jobs/SearchResult.ashx?bj=160000&sj=044&in=210500&pd=1&jl=%E5%8C%97%E4%BA%AC&kw=java&sm=0&p=1&sf=5000&st=8000&we=0103&el=4&et=2";
		e.extractData(url);
	}
}
