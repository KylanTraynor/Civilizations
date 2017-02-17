package com.kylantraynor.civilizations.menus.pages;

import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.Menu;

public interface MenuPage {
	public int getRows();
	public void refresh(Menu menu);
	public Button getIconButton();
	public String getTitle();
	
}