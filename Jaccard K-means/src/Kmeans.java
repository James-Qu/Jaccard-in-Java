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
		int k=10;
		int maxIter=25;
		Map<String,ArrayList<String>> points=importData();
		Map<Integer, ArrayList<String>> iniCentroids=iniCentroids(k,points);
		Map<String, ArrayList<ArrayList<String>>> iniCluster=clustering(points, iniCentroids, k);
		recalCentroids(iniCluster);
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
	public static Map<Integer,ArrayList<String>> iniCentroids(int k,Map<String,ArrayList<String>> points){
		Map<Integer,ArrayList<String>> result=new HashMap<Integer,ArrayList<String>>();
		Random rd=new Random();
		for(int i=0;i<k;i++){
			String index=Integer.toString(rd.nextInt(100)+1);
			result.put(i+1, points.get(index));
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
	public static float getIniSSE(Map<String,ArrayList<String>> points,Map<Integer,ArrayList<String>> iniCentroids){
		float initialSSE=0;
		for(Map.Entry<String, ArrayList<String>> entry:points.entrySet()){
			float minDistance=Float.MAX_VALUE;
			for(Map.Entry<Integer, ArrayList<String>> iniEntry:iniCentroids.entrySet()){
				float xOfCentroid=Float.parseFloat(iniEntry.getValue().get(0));
				float yOfCentroid=Float.parseFloat(iniEntry.getValue().get(1));
				float x=Float.parseFloat(entry.getValue().get(0));
				float y=Float.parseFloat(entry.getValue().get(1));
				float distance=getDistance(xOfCentroid, yOfCentroid, x, y);
				if(distance<minDistance){
					minDistance=distance;
				}
			}
			initialSSE+=minDistance*minDistance;
			
		}
		return initialSSE;
	}

	//get point from map with string input
	public static ArrayList<String> getPoint(Map<String,ArrayList<String>> points,String index){
		return points.get(index);
	}

	//Main clustering function
	public static Map<String,ArrayList<ArrayList<String>>> clustering
	(Map<String,ArrayList<String>> points,Map<Integer,ArrayList<String>> iniCentroids,int k){
		Map<String,ArrayList<ArrayList<String>>> cluster=new HashMap<String,ArrayList<ArrayList<String>>>();
		float iniSSE=getIniSSE(points, iniCentroids);
		float SSE=0;
		int i=0;
		while(i<k/*&&SSE<iniSSE*/){
			float currentSSE=0;
			for(Map.Entry<String, ArrayList<String>> entry:points.entrySet()){
				float minDistance=Float.MAX_VALUE;
				String centroidIndex=null;
				ArrayList<ArrayList<String>> emptyList=new ArrayList<ArrayList<String>>();
				for(Map.Entry<Integer, ArrayList<String>> iniEntry:iniCentroids.entrySet()){
					float xOfCentroid=Float.parseFloat(iniEntry.getValue().get(0));
					float yOfCentroid=Float.parseFloat(iniEntry.getValue().get(1));
					float x=Float.parseFloat(entry.getValue().get(0));
					float y=Float.parseFloat(entry.getValue().get(1));
					float distance=getDistance(xOfCentroid, yOfCentroid, x, y);
					if(distance<minDistance){
						minDistance=distance;
						centroidIndex=iniEntry.getKey().toString();
					}
				}
				currentSSE+=minDistance*minDistance;
				if(cluster.containsKey(centroidIndex)){
					cluster.get(centroidIndex).add(entry.getValue());
				}else{
					emptyList.add(entry.getValue());
					cluster.put(centroidIndex, emptyList);
				}
			}
			iniCentroids=recalCentroids(cluster);
			i++;
			System.out.println("CURRENT CLUSTERING "+ cluster);
			System.out.println("CURRENT SSE:"+currentSSE);
			SSE=currentSSE;
			
		}
		System.out.println("CURRENT CLUSTERING "+ cluster);
		System.out.println();
		return cluster;
	}

	//recalculate centroids
	public static Map<Integer,ArrayList<String>> recalCentroids(Map<String,ArrayList<ArrayList<String>>> initialCluster){
		float xTotal=0,yTotal=0;
		int index=1;
		Map<Integer,ArrayList<String>> newCentroids=new HashMap<Integer,ArrayList<String>>();
		for(Map.Entry<String,ArrayList<ArrayList<String>>> entry:initialCluster.entrySet()){
			int counter=0;
			for(ArrayList<String> point:entry.getValue()){
				xTotal+=Float.parseFloat(point.get(0));
				yTotal+=Float.parseFloat(point.get(1));
				counter++;
			}
			Float newX=xTotal/counter;
			Float newY=yTotal/counter;
			ArrayList<String> temp=new ArrayList<String>();
			temp.add(newX.toString());
			temp.add(newY.toString());
			newCentroids.put(index, temp);
			index++;

		}
		System.out.println("new centroids:"+newCentroids);
		return newCentroids;
	}






}
