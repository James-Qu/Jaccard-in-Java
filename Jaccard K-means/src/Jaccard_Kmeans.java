import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.*;
public class Jaccard_Kmeans {
	static double sse=0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//K clusters
		int k=25;
		Map<Long,HashSet<String>> map=parseJson();
		List<String> list=importCentroids(k);
		Map<String,ArrayList<String>> km=kmeans(map, list);
		for(Map.Entry<String, ArrayList<String>> entry:km.entrySet()){
			System.out.println("cluster center:"+entry.getKey()+"cluster member:"+entry.getValue());
		}
		output(km);
	}

	//split string and put in set<String>
	public static HashSet<String> returnSet(String str){
		String[] wordsList=str.split(" ");
		HashSet<String> set=new HashSet<String>(Arrays.asList(wordsList));
		return set;
	}


	//remove url from String
	private static String removeUrl(String commentstr)
	{
		String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(commentstr);
		int i = 0;
		while (m.find()) {
			commentstr = commentstr.replaceAll(m.group(i),"").trim();
			i++;
		}
		return commentstr;
	}

	//parse json. Return id-tweet map
	public static Map<Long,HashSet<String>> parseJson(){
		String fileName="Tweets.json";
		JSONParser parser=new JSONParser();
		//Set set=new TreeSet();
		Map<Long, HashSet<String>> map=new HashMap<Long,HashSet<String>>();
		try {
			BufferedReader br=new BufferedReader(new FileReader(fileName));
			String line;
			while((line=br.readLine())!=null){
				//System.out.println(line);
				Object obj = parser.parse(line);
				JSONObject jsonObject = (JSONObject) obj;
				String text = (String) jsonObject.get("text");
				text=removeUrl(text);
				text=text.replaceAll("[^a-zA-Z]", " ").toLowerCase().replace("rt", "");
				HashSet<String> set=returnSet(text);
				//System.out.println(text);
				Long id=(Long) jsonObject.get("id");
				//set.add(id);
				map.put(id, set);
				//System.out.println(id);
				//System.out.println("---------------------");
			}
			for(Map.Entry<Long,HashSet<String>> entry:map.entrySet()){
				System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			}
			//System.out.println("Set size:"+set.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	// import initial centroids. Return centroids arraylist.
	public static List<String> importCentroids(int k){
		String fileName="InitialSeeds.txt";
		List<String> list=new ArrayList<String>();
		try {
			BufferedReader br=new BufferedReader(new FileReader(fileName));
			String line;
			int i=0;
			while((line=br.readLine())!=null&&i<k){
				line=line.replaceAll("[\\D.]", "");
				list.add(line);
				i++;
			}
			System.out.println(list);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	// calculate Jaccard distance. Take two Tweets as input, return Jaccard distance
	public static double calculateJaccard(Set<String> a,Set<String> b){
		int common=0;
		Set<String> compareSet=new HashSet<String>(a);
		compareSet.retainAll(b);
		common=compareSet.size();
		int union=a.size()+b.size()-common;
		double result=1-(double)common/union;
		return result;
	}

	//Kmeans main
	public static Map<String,ArrayList<String>> kmeans(Map<Long,HashSet<String>> tweetMap, List<String> iniCentroids){
		//loop map, for each entry, compare the words set with each iniCentroids words set and mark the least jaccard distance one.
		//Create a map to store clustering result.
		Map<String,ArrayList<String>> clusterMap=new HashMap<String,ArrayList<String>>();
		//double sse=0;


		for(Map.Entry<Long, HashSet<String>> entry:tweetMap.entrySet()){
			double minJaccard=Double.MAX_VALUE;
			String center=null;
			ArrayList<String> clusterElement=new ArrayList<String>();
			for(String str:iniCentroids){
				Long centroidID=Long.parseLong(str);
				HashSet<String> centroidSet=tweetMap.get(centroidID);
				double jaccard=calculateJaccard(centroidSet, entry.getValue());
				if(jaccard<minJaccard){
					minJaccard=jaccard;
					center=str;
					//sse+=jaccard*jaccard;
					//System.out.println("The distance is:"+jaccard+" Current SSE:"+sse);
				}
			}
			sse+=minJaccard*minJaccard;
			System.out.println("The distance is:"+minJaccard+" Current SSE:"+sse);
			//check if center already in map.
			if(clusterMap.containsKey(center)){
				clusterMap.get(center).add(entry.getKey().toString());
			}else{
				clusterElement.add(entry.getKey().toString());
				clusterMap.put(center, clusterElement);
			}
		}
		System.out.println("Total SSE: "+sse);
		return clusterMap;
	}

	public static void output(Map<String,ArrayList<String>> clusterMap){
		try {
			File file=new File("tweets-k-means-output.txt");
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fw=new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw=new BufferedWriter(fw);
			for(Map.Entry<String, ArrayList<String>> entry:clusterMap.entrySet()){
				System.out.println("can you see?"+entry.getKey());
				bw.write("Centroids: "+entry.getKey()+" Cluster members:"+entry.getValue()+"\n");
			}
			bw.write("Total SSE="+sse);
			bw.close();
			System.out.println("output file done.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
