package com.kylantraynor.civilizations.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.eclipse.jetty.server.Request;

import com.google.common.io.ByteStreams;
import com.kylantraynor.civilizations.Civilizations;

import fr.rhaz.webservers.Bukkit.BukkitWebEvent;
import fr.rhaz.webservers.WebServers.API.WebServer;

public class WebListener implements Listener{
	
	public static String getContentType(String filename){
	    MimetypesFileTypeMap map = new MimetypesFileTypeMap();
	    map.addMimeTypes("text/html html htm jsp");
	    map.addMimeTypes("text/javascript js json");
	    map.addMimeTypes("text/css css");
	    map.addMimeTypes("image/jpeg jpg jpeg");
	    map.addMimeTypes("image/gif gif");
	    map.addMimeTypes("image/png png");
	    return map.getContentType(filename.toLowerCase());
	}
	
	public static InputStream getResource(String resource){
	    return Civilizations.currentInstance.getClass().getClassLoader().getResourceAsStream(resource);
	}
	
	public static String getResourceBase(){
		return Civilizations.currentInstance.getClass().getClassLoader().getResource("WEB-INF").toExternalForm();
	}
	
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
	    			e.getHandler().getContext().setResourceBase(getResourceBase());
	    			Civilizations.DEBUG(e.getHandler().getContext().getResourceBase());
	    			
	    			try {
						e.getHandler().getContext().getServletContext().getRequestDispatcher("/index.jsp").forward(req,res);
					} catch (ServletException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
	    			/*
	    			String file = "index.jsp";
	    			InputStream stream = getResource(file);
	    			res.setContentType(getContentType(file));
	    			if(stream != null){
	    			    try {
							ByteStreams.copy(stream, res.getOutputStream());
						} catch (IOException e1) {
							e.setCancelled(true);
							e1.printStackTrace();
						}
	    			} else e.setCancelled(true);
	    			*/
	    		}
	    	}
	    }
	}
	
}
