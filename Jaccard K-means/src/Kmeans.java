import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Kmeans {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int k=3;
		int maxIter=25;
		Map<String,ArrayList<String>> points=importData();
		ArrayList<String> iniCentroids=iniCentroids(k);
		Map<String,ArrayList<String>> iniCluster=initialClustering(points, iniCentroids, k);
	}

	//Read data. Return ArrayList of Arraylist of String
	public static Map<String,ArrayList<String>> importData(){
		String fileName="test_data.txt";
		//ArrayList<ArrayList<String>> result=new ArrayList<ArrayList<String>>();
		Map<String,ArrayList<String>> result=new HashMap<String,ArrayList<String>>();
		try {
			BufferedReader br=new BufferedReader(new FileReader(fileName));
			String line;
			br.readLine();
			while((line=br.readLine())!=null){
				//System.out.println(line);
				//ArrayList<String> sublist=new ArrayList<String>();
				//sublist.add(line);
				//result.add(sublist);
				String[] temp=line.split("\t");
				//System.out.println(temp[0]+" "+temp[1]+" "+temp[2]);
				ArrayList<String> tempList=new ArrayList<String>();
				tempList.add(temp[1]);
				tempList.add(temp[2]);
				result.put(temp[0], tempList);
			}
			System.out.println("POINTS: "+result);
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//Random initial centroids(result arraylist store point index)
	public static ArrayList<String> iniCentroids(int k){
		ArrayList<String> result=new ArrayList<String>();
		Random rd=new Random();
		for(int i=0;i<k;i++){
			result.add(Integer.toString(rd.nextInt(100)+1));
		}
		System.out.println("INITIAL CENTROIDS: "+result);
		return result;
	}

	//calculate distance of two points
	public static float getDistance(float x,float y,float cx,float cy){
		float distance=(float) Math.sqrt(Math.pow(x-cx, 2)+Math.pow(y-cy, 2));
		//System.out.println("calculate distance: "+x+" "+y+" "+cx+ " "+cy+"="+distance);
		return distance;
	}

	//calculate sse
	public static float getSSE(){
		return 0;
	}

	//get point from map with string input
	public static ArrayList<String> getPoint(Map<String,ArrayList<String>> points,String index){
		return points.get(index);
	}

	//Main clustering function
	public static Map<String,ArrayList<ArrayList<String>>> initialClustering(Map<String,ArrayList<String>> points,ArrayList<String> iniCentroids,int k){
		Map<String,ArrayList<ArrayList<String>>> cluster=new HashMap<String,ArrayList<ArrayList<String>>>();
		Map<Integer,ArrayList<String>> centroids=new HashMap<Integer,ArrayList<String>>();
		ArrayList<ArrayList<String>> emptyList=new ArrayList<ArrayList<String>>();
		int temp=0;
		for(String s:iniCentroids){
			centroids.put(++temp, points.get(s));
		}
		System.out.println("centroids map: "+centroids);
		float currentSSE=getSSE();
		float minSSE=Float.MAX_VALUE;
		int i=0;
		//while(i<k&&currentSSE<minSSE){
			for(Map.Entry<String, ArrayList<String>> entry:points.entrySet()){
				float minDistance=Float.MAX_VALUE;
				String centroidIndex=null;
				for(String s:iniCentroids){
					float xOfCentroid=Float.parseFloat(points.get(s).get(0));
					float yOfCentroid=Float.parseFloat(points.get(s).get(1));
					float x=Float.parseFloat(entry.getValue().get(0));
					float y=Float.parseFloat(entry.getValue().get(1));
					float distance=getDistance(xOfCentroid, yOfCentroid, x, y);
					if(distance<minDistance){
						minDistance=distance;
						centroidIndex=s;
					}
				}
				if(cluster.containsKey(centroidIndex)){
					cluster.get(centroidIndex).add(entry.getValue());
				}else{
					emptyList.add(entry.getValue());
					cluster.put(centroidIndex, emptyList);
				//}
			}
		//}
		System.out.println("INITIAL CLUSTERING "+ cluster);
		return cluster;
	}








}
