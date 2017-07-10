package colorio;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Iterator;
public class GetColors extends HttpServlet
{
	public void doGet(HttpServletRequest req,HttpServletResponse res)
	{

		String url=(String)req.getParameter("URL");
		System.out.println("Request Arrived for URL "+url);
		res.setContentType("text/json");
		
		Hashtable<String,Collection> urlToColorSetMapping=null;
		/*
		will retrieve the same object type from ColorIOUtils
		*/


		PrintWriter pw=null;
		try
		{
			pw=res.getWriter();
		if(url!=null && url.trim().length()!=0)
		{
			ColorIOUtils cu=new ColorIOUtils();
			urlToColorSetMapping=cu.findColors(url);
			StringBuffer sb=new StringBuffer();
			sb.append("[");
			int x=0;
			int s=urlToColorSetMapping.size();
			Collection<String> colorSet=null;
			String str = null;
			Enumeration urls=urlToColorSetMapping.keys();
			while(urls.hasMoreElements()) 
			{
		        String key = (String)urls.nextElement();
		        colorSet =  urlToColorSetMapping.get(key);
		        int y=0;
		        int ss=colorSet.size();
		        Iterator<String> colors=colorSet.iterator();
			    sb.append("{\"url\":\""+key+"\",\"colors\":[");

		        while(colors.hasNext()  ) 
		        {
		        str = (String) colors.next();
	 			sb.append("\""+str+"\",");         
	      		y++;
	      		}
	      		if(y!=0)
	      		{
					sb.setLength(sb.length() - 1);	      		
	      		}
				sb.append("]},");
	      		x++;
	      	}
	      	if(x!=0)
	      	{
				sb.setLength(sb.length() - 1);
	      	}

			sb.append("]");
			pw.println("{\"success\":true,\"content\":"+sb.toString()+"}");
		}

		else
		{
			pw.println("{\"success\":false,\"message\":\""+"Received URL == null\"}");
		}
		}
		catch(Exception e)
		{
			pw.println("{\"success\":false,\"message\":\""+ e.getMessage()+"\"}");
		}
	
	}

	public void doPost(HttpServletRequest req,HttpServletResponse res)
	{
		doGet(req,res);
	}



}