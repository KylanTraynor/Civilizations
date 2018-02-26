package com.kylantraynor.civilizations.menus.pages;

import java.util.HashMap;
import java.util.Map;

import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.Menu;

public class GroupBlueprintsPage implements MenuPage{

	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();
	
	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void refresh(Menu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Button getIconButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Button> getButtons() {
		return buttons;
	}

	@Override
	public String getTitle() {
		return null;
	}
	
}