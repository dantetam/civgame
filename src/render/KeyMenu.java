package render;

import system.InputSystem;
import system.InputSystem.KeyPressBind;

public class KeyMenu extends Menu {

	public InputSystem input;
	
	public KeyMenu(InputSystem system, String name) 
	{
		super(name);
		input = system;
	}

	public void setupButtons()
	{
		buttons.clear();
		int i = 0;
		for (KeyPressBind kb: InputSystem.KeyPressBind.values())
		{
			Button b = new Button("bind"+kb, kb + ": [" + kb.key1 + "] ; [", "" + kb.key2 + "]", 100, 100 + 30*i, 200, 30);
			b.autoClear = false;
			buttons.add(b);
			i++;
		}
	}
	
}
