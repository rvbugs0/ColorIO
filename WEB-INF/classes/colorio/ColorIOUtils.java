package colorio;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.util.regex.*;
	public class ColorIOUtils {

	private final String USER_AGENT = "Mozilla/5.0";


	// HTTP GET request
	private String sendGetRequest(String url) throws Exception 
	{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//print result
		return response.toString();
	}

	// HTTP POST request
	private String sendPost(String url,String urlParameters) throws Exception {
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//print result
		return response.toString();
	}


	public Hashtable<String,Collection> findColors(String url) throws Exception
	{
		Hashtable<String,Collection> urlToColorSetMapping; 
		urlToColorSetMapping=new Hashtable<String,Collection>();	
		/*
		mapping from url to a set of colors, 
		we'll be returning a set of colors associated with each url
		*/
		Collection<String> visited=new HashSet<String>();
		/*
		url's/nodes that have been visited during breadth first search
		*/

		Collection<String> colors = new HashSet<String>();
		/*
		a set of colors found during extraction from a particular url
		with each url this set is put into urlToColorSetMapping as url->colors
		and colors is initialized again as a new set for the next url.		 
		*/
		visited.add(url.trim());

		ArrayList<String> queue=new ArrayList<>();
		/*
		Breadth first search queue.
		*/
		queue.add(url.trim());
		
		queue.add(null);
		/*
		null is added after a url is processed and all its children url's are inserted in queue.
		it is used to identify arrival of a new level. 
		*/
		int level=0;
		while(!queue.isEmpty())
		{
			String temp=queue.remove(0);
			if(temp==null)
			{
				if(queue.size()!=0)
				{
					queue.add(null);
				}
				level++;
				if(level==2)
				{
					break;
				}
			}
			else
			{

				String response = sendGetRequest(temp);
				String regexp="(background|color|colour) *-? *[coulr]* *: *[#]?\\w* *\\(? *\\d* *,? *\\d* *,? *\\d* *,? *\\d*\\.?\\d* *\\)? *;";
				/*
				matches tags like  
				background-color:#fff 
				background:rgba(0,0,0); // however ignores use case where opacity is used eg. background:rgba(0,0,0,0.5); 
				and many more

				*/
				Pattern pattern=Pattern.compile(regexp);
				Matcher matcher=pattern.matcher(response);
				while(matcher.find())
				{
					String k=matcher.group().split(":")[1].split(";")[0].trim();
					colors.add(k);
				}

				colors.remove("transparent");
				colors.remove("none");
				colors.remove("inherit");
				/*
				removing unnecessary colors from the set.
				*/

				urlToColorSetMapping.put(temp,colors);
				/*
				mapped this set of colors to this url
				*/
				colors=new HashSet<String>();
				/*
				initialized set for next usage
				*/


				regexp =  "href=['\"]\\S*.css[^\"/.]*['\"]";
				/*
				this time extracting new url's from the above received response, this will be our url to css files
				*/

				pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE);
				matcher=pattern.matcher(response);
				while(matcher.find())
				{
					String k=matcher.group();
					k=k.substring(6,k.length()-1);
					if(k.startsWith("http")  && !visited.contains(k))
					{
						//  <link rel="stylesheet" type="text/css" href="https://cdn.sstatic.net/Sites/stackoverflow/all.css?v=743e70f26396">
						visited.add(k);
						queue.add(k);
					}
					else if(k.startsWith("//"))
					{
						//   <link rel="stylesheet" href="//s.ytimg.com/yts/cssbin/www-core-webp-vfl-I9L6K.css" name="www-core">
						visited.add("http:"+k);
						queue.add("http:"+k);
					}
					else if(k.startsWith("../"))
					{
						String tempUrl=new String(k);
						String newUrl=new String(url);

						newUrl=newUrl.substring(0,newUrl.lastIndexOf("/"));
						while(tempUrl.startsWith("../"))
						{
							tempUrl=tempUrl.substring(3);
							newUrl=newUrl.substring(0,newUrl.lastIndexOf("/"));
						}
							newUrl=newUrl+"/"+tempUrl;
							queue.add(newUrl);
					}
					else if(k.startsWith("/"))
					{
						// <link rel="stylesheet" type="text/css" href="/browserref.css"> 
						String tempUrl=new String(k);
						String front=url.substring(0,url.indexOf("//")+2);
						String end= url.substring(url.indexOf("//")+2);
						end=end.substring(0,end.indexOf("/"));
						String newUrl=front+end+tempUrl;
						queue.add(newUrl);
					}
					else
					{
						// <LINK REL=StyleSheet href="regex.css" TYPE="text/css">
						String tempUrl=new String(k);
						String newUrl=url.substring(0,url.lastIndexOf("/")+1)+tempUrl;
						queue.add(newUrl);	
					}



				}


			}


		}

		return urlToColorSetMapping;

	}


}

