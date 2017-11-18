package general;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.alee.laf.WebLookAndFeel;

import frames.VRBSFrame;

public class Main {

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(new WebLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		new VRBSFrame().setVisible(true);

	}

}
