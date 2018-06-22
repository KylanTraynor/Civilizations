package com.kylantraynor.civilizations.menus.pages;

import java.util.Map;

import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.Menu;

public interface MenuPage {
	int getRows();
	void refresh(Menu menu);
	Button getIconButton();
	Map<Integer, Button> getButtons();
	String getTitle();
	
}