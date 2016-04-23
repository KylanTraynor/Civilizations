package com.kylantraynor.civilizations.listeners;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
	    				ServletContext sc = baseReq.getServletContext();
						sc.getRequestDispatcher(target).forward(req, res);
					} catch (ServletException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	    		}
	    	}
	    }
	}
	
}
