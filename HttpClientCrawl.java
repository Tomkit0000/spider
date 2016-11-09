import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
public class HttpClientCrawl {
	public static void main(String[] args) throws Exception{
		sourceXXX();
		BufferedReader reader= new BufferedReader(new InputStreamReader(new FileInputStream("sss.txt")));
		String line=null;
		Map<String,String> map = new HashMap<String,String>();
		double remark ;
		String mark;
		String value;
		Set<Double> set = new TreeSet<Double>();
		Object[] db = set.toArray();
		for(;(line=reader.readLine())!=null;){  
			mark = line.split("`")[2];
			remark = Double.valueOf(mark);
			if(map.containsKey(mark)){
				value = map.get(mark);
				map.put(mark, value+"\n"+line+"\n");
			}else{
				map.put(mark, line+"\n");
				set.add(remark);
			}
		}
		
		
		StringBuffer sb = new StringBuffer("序号,书名,评分,评价人数,作者,出版社,出版日期,价格");
		String content ;
		String[] itemArr;
		String[] detailArr;
		int count = 0;
		for(int i=db.length-1;i>=0;i--){
			content = map.get(db[i]);
			itemArr = content.split("\n");
			for(String str : itemArr){
			   sb.append(count).append(",")
			     .append(str.split("`")[0]).append(",")
			     .append(str.split("`")[2]).append(",")
			     .append(str.split("`")[3]).append(",");
			   count++;
			   detailArr = str.split("`")[1].split("/");
			   if(detailArr.length==5){
				   sb.append(detailArr[0]).append("/").append(detailArr[1]).append(",")
				     .append(detailArr[2]).append(",")
				     .append(detailArr[3]).append(",")
				     .append(detailArr[4]).append(",");
			   }else if(detailArr.length==4){
				   sb.append(detailArr[0]).append(",")
				     .append(detailArr[1]).append(",")
				     .append(detailArr[2]).append(",")
				     .append(detailArr[3]).append(",");
			   }else{
				   sb.append(str);
			   }
			   sb.append("\n");
			}
		}
		FileWriter fw = new FileWriter("result.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(sb.toString());
		bw.close();
		fw.close();
		
		reader.close();
	}
	
	public static void sourceXXX() throws Exception{
		String url = "https://book.douban.com/subject_search?start=";
		StringBuffer sb = new StringBuffer();
		String result = "";
		String[] sArr = {"计算机","算法","互联网"};
		for(String type : sArr){
			Lable: 
			for(int i=0 ; ; i=i+15){
		    	System.out.println(type+" -- current circle mark number : "+i);
		    	url += i +"&search_text="+type;
		    	result = parseXXX(url);
		    	url = "https://book.douban.com/subject_search?start=";
		    	boolean flag = "".equals(result);
		    	boolean f = "current circle".equals(result);
		    	if(!flag && !f){
		    		sb.append(result);
		    	}else if(f){
		    		break Lable;
		    	}else if(flag){
		    		continue;
		    	}
		    }
		}
	    
		FileWriter fw = new FileWriter("sss.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(sb.toString());
		bw.close();
		fw.close();
	}
	
	public static String parseXXX(String url_str) {
        URLConnection url_con = null;
        InputStream htm_in = null;
        int sec_cont = 10000;
        BufferedReader buff = null;
        StringBuffer sb = new StringBuffer();
        URL url = null;
        try {
        	url = new URL(url_str);
            url_con = url.openConnection();
            url_con.setDoOutput(true);
            url_con.setReadTimeout(10 * sec_cont);
            url_con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            htm_in = url_con.getInputStream();
            buff = new BufferedReader(new InputStreamReader(htm_in,"utf-8"));
            StringBuffer res = new StringBuffer();
            String line = "";
            while((line = buff.readLine()) != null){
                res.append(line);
            }
            String result = res.toString();
            result = result.replaceAll("/n", "");
            result = result.replaceAll(" ", "");
            String content = result.split("<ulclass=\"subject-list\">")[1];
            String[] bookArray = content.split("<liclass=\"subject-item\">");
            String name,remark,amounts,detail;
            String s1,s0,s2,s3,s4,s5,s6;
            for(String book : bookArray){
            	if(book.length()>0){
            		if(book.indexOf("title=\"")<0){
            			return "current circle";
            		}
            		s0 =  book.split("title=\"")[1];
            		name = s0.split("\"onclick=")[0];
            		
            		s1 = s0.split("\"onclick=")[1];
            		s2 = s1.split("<divclass=\"pub\">")[1];
            		detail = s2.split("</div><divclass=\"starclearfix\">")[0];
            		if(s2.indexOf("</div><divclass=\"starclearfix\">")<0){
            			continue;
            		}else{
                		s3 = s2.split("</div><divclass=\"starclearfix\">")[1];
            		}
            		if(s3.indexOf("<spanclass=\"rating_nums\">")>0){
            			s4 = s3.split("<spanclass=\"rating_nums\">")[1];
            			remark = s4.split("</span><spanclass=\"pl\">")[0];
            			s5 = s4.split("</span><spanclass=\"pl\">")[1];
            			s6 = s5.split("人评价")[0];
            			amounts = s5.split("人评价")[0].substring(1, s6.length());
            			if(Integer.valueOf(amounts)>=1000){
            				sb.append(name).append("`");
            				sb.append(detail).append("`");
	            			sb.append(remark).append("`");
            				sb.append(amounts).append("\n");
            			}
            		}else{
            			continue;
            		}
            	}
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
        	try {
				htm_in.close();
				buff.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return sb.toString();
	}
}
