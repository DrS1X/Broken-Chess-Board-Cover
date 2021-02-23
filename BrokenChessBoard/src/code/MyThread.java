package code;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MyThread extends Thread{
	@Override
	public void run() {
		while(true) {
			try {
				if(!MainWin.pSuspend) MainWin.Next();			
				Thread.sleep(MainWin.speed);
			}
			catch(Exception e) {}
		}
	}

	static MyThread p;
	public static void main(String[] agrs) {
		String lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";//"com.sun.java.swing.plaf.windows.WindowsLookAndFeel";//
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			UIManager.put("OptionPane.messageFont", new java.awt.Font("Times New Roman", 0, 20));
//			UIManager.put("Button.font", new java.awt.Font("·ÂËÎ", 0, 25));
//			UIManager.put("OptionPane.messageFont", new java.awt.Font("·ÂËÎ", 0, 25));
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		new MainWin();
		p = new MyThread();
		p.start();
	}
}
