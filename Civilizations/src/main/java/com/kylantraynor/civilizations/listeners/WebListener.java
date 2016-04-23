package com.kylantraynor.civilizations.listeners;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.eclipse.jetty.server.Request;

import com.kylantraynor.civilizations.Civilizations;

import fr.rhaz.webservers.Bukkit.BukkitWebEvent;
import fr.rhaz.webservers.WebServers.API.WebServer;

public class WebListener implements Listener{
	
	@EventHandler
	public void onWebEvent(BukkitWebEvent e){
	    WebServer server = e.getHandler().getServer();
	    String target = e.getTarget();
	    Request baseReq = e.getBaseReq();
	    HttpServletRequest req = e.getRequest();
	    HttpServletResponse res = e.getResponse();
	   
	    if(Civilizations.getWebServer() != null){
	    	if(e.getPort() == Civilizations.getWebServer().getPort()){
	    		if(target.equals("/")){
	    			target = "/Index.jsp";
	    			try {
						res.getWriter().println("<h1>Civilizations' Web Interface is still a work in progress.</h1>");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	    		}
	    	}
	    }
	}
	
}
