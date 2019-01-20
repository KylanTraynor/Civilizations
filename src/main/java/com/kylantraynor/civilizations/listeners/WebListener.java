package com.kylantraynor.civilizations.listeners;

import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;

import org.bukkit.event.Listener;
import com.kylantraynor.civilizations.Civilizations;

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
		return Civilizations.currentInstance.getClass().getClassLoader().getResource("WebContent").toExternalForm();
	}
	/*
	@EventHandler
	public void onWebEvent(BukkitWebEvent e){
	    WebServer server = e.getHandler().getServer();
	    String target = e.getTarget();
	    Request baseReq = e.getBaseReq();
	    HttpServletRequest req = e.getRequest();
	    HttpServletResponse res = e.getResponse();
	   
	    if(Civilizations.getWebServer() != null){
	    	if(e.getPort() == ((WebServer) Civilizations.getWebServer()).getPort()){
	    		if(target.equals("/")){
	    			File f = new File(Civilizations.getPrivateWebDirectory(), "index.html");
	    			if(f.exists()){
	    				
	    			} else {
	    				try {
							f.createNewFile();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	    			}
	    			res.setContentType(getContentType(f.getName()));
	    			if(f != null){
	    			    try {
							Files.copy(f, res.getOutputStream());
						} catch (IOException e1) {
							e.setCancelled(true);
							e1.printStackTrace();
						}
	    			} else e.setCancelled(true);
	    		}
	    	}
	    }
	}*/
	
}
