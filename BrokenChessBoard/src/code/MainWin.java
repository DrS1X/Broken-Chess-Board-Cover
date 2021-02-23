package code;

import java.util.Random;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

class MainWin implements ActionListener,ItemListener{
	Font numFont = new java.awt.Font("Times New Roman",0,20);
	JPanel menuPanel = new JPanel();
	JPanel boardPanel = new JPanel(new GridLayout(32,36));
	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,20));
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();	

	Font chianeseFont = new java.awt.Font("Times New Roman", 0, 20);
	static JFrame jf = new JFrame("Broken Chess Board Cover");
	JButton begin = new JButton("Run");
	JButton stop = new JButton("Stop");
	JButton Continue = new JButton("Continue");
	JButton lastStep = new JButton("Back");
	JLabel sizeLabel = new JLabel("Scale");
	JLabel speedLabel = new JLabel("Speed");
	static JRadioButton manualRadioButton = new JRadioButton("Manual Cover",false);
	static JRadioButton autoRadioButton = new JRadioButton("AUTO Cover",true);
	JRadioButton defaultCoBut = new JRadioButton("Default Color",true);
	JRadioButton randCoBut = new JRadioButton("Random Color",false);
	String s = "Please drag templates at right to cover the chess board";
	
//	Font chianeseFont = new java.awt.Font("仿宋", 0, 20);
//	static JFrame jf = new JFrame("残缺棋盘覆盖");
//	JButton begin = new JButton("开始");
//	JButton stop = new JButton("暂停");
//	JButton Continue = new JButton("继续");
//	JButton lastStep = new JButton("回退");
//	JLabel sizeLabel = new JLabel("棋盘边长");
//	JLabel speedLabel = new JLabel("覆盖速度");
//	static JRadioButton manualRadioButton = new JRadioButton("手动模式",false);
//	static JRadioButton autoRadioButton = new JRadioButton("自动模式",true);
//	JRadioButton defaultCoBut = new JRadioButton("默认配色",true);
//	JRadioButton randCoBut = new JRadioButton("随机配色",false);
//	String s = "请拖动右侧模板进行覆盖";
	
	JSlider speedSlider = new JSlider(20,1000);
	String[] ranks ={"2^1", "2^2", "2^3", "2^4", "2^5"};	
	JComboBox<String> comboBox = new JComboBox<String>(ranks);
	
	ButtonGroup modelButtonGroup = new ButtonGroup();
	ButtonGroup coButtonGroup = new ButtonGroup();
	JOptionPane inf = new JOptionPane();	
	
	static JLabel[][] chess= new JLabel[32][36];
	static int speed=20,size=1,bia,current=0,manualStep;
	static int[] colorStatistics = new int[5], brokenPos= new int[2],colorTemp = {0xaeed1a,0xed1aca,0x441aed,0x1acded};
	static int[][] color= new int [32][32], colorStaPos = new int[4][2],spatialTemp= {{0,0},{0,1},{1,0},{1,1}};
	static int[][][] stepList= new int[350][3][2];
	static Template[] template = new Template[4];
	static boolean pSuspend = true;
	int lastSize = -1;
	
	public MainWin() {
		begin.setFont(chianeseFont);
		stop.setFont(chianeseFont);
		Continue.setFont(chianeseFont);
		lastStep.setFont(chianeseFont);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = (int) screensize.getHeight();
		jf.setLayout(new BorderLayout());
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(jf.getGraphicsConfiguration());
		screenHeight -= screenInsets.bottom;		
		jf.setSize(new Dimension((int)(screenHeight*1350/1020),screenHeight));
//		jf.setSize(1350,1020);
		jf.setResizable(false);
		menuPanel.setLayout(gbl);	
		setGBC(0,0,4,1,sizeLabel,new Insets(0,0,0,0));
		setGBC(1,1,3,1,comboBox,new Insets(10,0,90,0));
		comboBox.setFont(numFont);
		setGBC(0,2,4,1,speedLabel,new Insets(0,0,0,0));
		setGBC(0,3,4,1,speedSlider,new Insets(10,0,90,0));
		setGBC(0,4,2,1,autoRadioButton,new Insets(0,20,0,0));
		setGBC(2,4,2,1,manualRadioButton,new Insets(0,20,0,10));
		setGBC(0,5,2,1,defaultCoBut,new Insets(90,20,0,0));
		setGBC(2,5,2,1,randCoBut,new Insets(90,20,0,10));
		
		comboBox.setEditable(false);
		comboBox.setSelectedIndex(3);
		comboBox.addItemListener(this);
		
		speedSlider.setValue(20);
		speedSlider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				speed = speedSlider.getValue();
			}
		});
		
		begin.addActionListener(this);
		buttonPanel.add(begin);
		stop.addActionListener(this);
		buttonPanel.add(stop);
		Continue.addActionListener(this);
		buttonPanel.add(Continue);
		lastStep.addActionListener(this);
		buttonPanel.add(lastStep);
		
		autoRadioButton.addItemListener(this);
		manualRadioButton.addItemListener(this);
		modelButtonGroup.add(autoRadioButton);
		modelButtonGroup.add(manualRadioButton);
		
		defaultCoBut.addItemListener(this);
		randCoBut.addItemListener(this);
		coButtonGroup.add(defaultCoBut);
		coButtonGroup.add(randCoBut);		
		for(int i = 0;i<32;++i) {
			for(int j=0;j<36;++j) {
				chess[i][j] = new JLabel();
				chess[i][j].setOpaque(true);
				boardPanel.add(chess[i][j]);
			}
		}
		boardPanel.setBorder(BorderFactory.createEtchedBorder());
		jf.add(menuPanel,BorderLayout.WEST);
		jf.add(boardPanel,BorderLayout.CENTER);
		jf.add(buttonPanel,BorderLayout.SOUTH);
//		jf.pack();
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ManualBoard listener = new ManualBoard();
		int i,j,k,row;
		for( k=0;k<4;k++) {
			row= 5+6*k;
			template[k] = new Template(33,row,spatialTemp[k][0],spatialTemp[k][1],new Color(colorTemp[k]));
			for(i= row; i<= row+1; i++) {
				for(j = 33;j<= 34; j++) {
					if(i!=template[k].whiteRow+template[k].row || j!=template[k].whiteCol+template[k].col)
						chess[i][j].setBackground(template[k].color);
						MainWin.chess[i][j].setBorder(BorderFactory.createEtchedBorder());
						chess[i][j].addMouseListener(listener);
						chess[i][j].addMouseMotionListener(listener);	
				}
			}			
			chess[row+3][33].setFont(numFont);
//			chess[row+3][34].setFont(chianeseFont);
//			chess[row+3][34].setText("个");			
			colorStaPos[k][0]= row+3;
			colorStaPos[k][1]= 33;
		}
	}
	
	void setGBC(int x,int y,int w,int h,Component comp,Insets ins) {
		gbc.insets= ins;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		comp.setFont(chianeseFont);
		gbl.setConstraints(comp, gbc);
		menuPanel.add(comp);
	}
	
	void Unit() {
		current = manualStep= 0;
		int n,i,j,k,row;
		n = 1 + comboBox.getSelectedIndex();
		size = 1;
		size <<= n;
		bia = 16 - size/2;
		if(randCoBut.isSelected()) {
			Random rand = new Random();
			for(k = 0; k < 4; k++) {
				template[k].color= new Color(rand.nextInt(256),
						rand.nextInt(256),rand.nextInt(256));
				row= 5+6*k;
				for(i= row; i<= row+1; i++) {
					for(j = 33;j<= 34; j++) {
						if(i!=template[k].whiteRow+template[k].row || j!=template[k].whiteCol+template[k].col)
							chess[i][j].setBackground(template[k].color);	
					}
				}
			}			
		}
		else {
			for(k = 0; k < 4; k++) {
				template[k].color= new Color(colorTemp[k]);
				row= 5+6*k;
				for(i= row; i<= row+1; i++) {
					for(j = 33;j<= 34; j++) {
						if(i!=template[k].whiteRow+template[k].row || j!=template[k].whiteCol+template[k].col)
							chess[i][j].setBackground(template[k].color);	
					}
				}
			}
		}
		
		for(i = 0;i<32;++i) {
			for(j=0;j<32;++j) {
				color[i][j] = -1;
				chess[i][j].setBorder(null);
				chess[i][j].setBackground(Color.white);
				if(i>= bia && i< size+bia && j>= bia && j< size+bia) {
					chess[i][j].setBorder(BorderFactory.createEtchedBorder());
					if(!(i%2==0 && j%2==1 || i%2==1 && j%2==0)){						
						chess[i][j].setBackground(Color.black);
						color[i][j] = -2;						
					}
				}				
			}	
		}
		Random rand = new Random();
		brokenPos[0] = rand.nextInt(size)+bia;
		brokenPos[1] = rand.nextInt(size)+bia;
//		brokenPos[0] = 17;
//		brokenPos[1] = 10;
		chess[brokenPos[0]][brokenPos[1]].setBorder(null);
		chess[brokenPos[0]][brokenPos[1]].setBackground(Color.red);
		chess[brokenPos[0]][brokenPos[1]].setBorder(BorderFactory.createLoweredBevelBorder());
		color[brokenPos[0]][brokenPos[1]]= 5;		

		for( i= 0; i< 4; i++){
			chess[colorStaPos[i][0]][colorStaPos[i][1]].setText("0");
			colorStatistics[i]=0;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(begin)) {
			Unit();
			if(autoRadioButton.isSelected()) {
				if(lastSize != size || pSuspend == false) {
//					Unit(size);
					AutoBoard input = new AutoBoard(bia,bia,size);
					for(int i = 0;i<32;++i) {
						for(int j=0;j<32;++j) {
							AutoBoard.color[i][j] = -1;
						}
					}
					AutoBoard.color[brokenPos[0]][brokenPos[1]]= 5;
					AutoBoard.order = 0;
					AutoBoard.Division(input);
					color = AutoBoard.color;
					stepList = AutoBoard.stepList;
				}
				if(pSuspend) {
					pSuspend = false; 
					MyThread.p.resume();
				}
			}
		}else if(e.getSource().equals(stop) && autoRadioButton.isSelected()) {
			MyThread.p.suspend();
			pSuspend = true;
		}
		else if(e.getSource().equals(Continue)) {
			if(pSuspend) {	
				pSuspend = false;
				MyThread.p.resume();
			}
		}
		else if(e.getSource().equals(lastStep)) {
//			if(current == (size*size-1)/3 || autoRadioButton.isSelected()) {
			if(!pSuspend) {	
				pSuspend = true;
				MyThread.p.suspend();
			}
			Last();
		}
		lastSize = size;
	}
	
	public void itemStateChanged(ItemEvent e) {
		if(manualRadioButton.isSelected()) {			
			if(e.getSource()==comboBox ) {
				Unit();
			}
			if(e.getSource()==manualRadioButton ) {
				Unit();
				JOptionPane.showMessageDialog(null,s);
			}
	//		if(e.getSource()==autoRadioButton && autoRadioButton.isSelected()) {
	//			Unit(size);
	//		}
			else if(e.getSource()==randCoBut || e.getSource()==defaultCoBut) {
				Unit();
			}
		}
	}
	
	void Last() {
		int col,i=0,j=0;
		if(current> 0) {
			current--;
			Statistics(false,color[stepList[current][0][0]][stepList[current][0][1]]);
			for(col=0;col<3;col++) {
				i= stepList[current][col][0];
				j= stepList[current][col][1];
				if(i%2==0 && j%2==1 || i%2==1 && j%2==0) {
					chess[i][j].setBackground(Color.white);
					if(manualRadioButton.isSelected())
						color[i][j]= -1;
				}
				else{
					chess[i][j].setBackground(Color.black);
					if(manualRadioButton.isSelected())
						color[i][j]= -2;
				}				
			}
		}
	}
	
	static void Next() {
		int i=0,j=0;
//		System.out.printf("%d\n",current);
		if(current<(size*size-1)/3 && autoRadioButton.isSelected()
				|| current<manualStep && manualRadioButton.isSelected()) {
			for(int col=0;col<3;col++) {
				i= stepList[current][col][0];
				j= stepList[current][col][1];					
				chess[i][j].setBackground(template[color[i][j]].color);
			}
			Statistics(true,color[i][j]);
			current++;
		}
		else return;
	}
	
	static void Statistics(boolean nextStep,int colorId) {
		if(nextStep) {
			colorStatistics[colorId] += 1;
		}else {
			colorStatistics[colorId] -= 1;
		}
			String s = Integer.toString(colorStatistics[colorId]);
			chess[colorStaPos[colorId][0]][colorStaPos[colorId][1]].setText(s);
		
	}
}

