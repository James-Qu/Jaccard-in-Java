import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.*;
public class Kmeans {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		parseJson();
	}
	
	//parse json
	public static void parseJson(){
		String fileName="Tweets.json";
		
		try {
			BufferedReader br=new BufferedReader(new FileReader(fileName));
			String line;
			while((line=br.readLine())!=null){
				System.out.println(line);
				JSONObject jobj=new JSONObject(line);
				JSONArray jarray=new JSONArray(jobj.getJSONArray("text"));
				for(int i = 0; i < jarray.length(); i++) {
			        System.out.println("Keyword: " + jarray.getString(i));
			    }
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
