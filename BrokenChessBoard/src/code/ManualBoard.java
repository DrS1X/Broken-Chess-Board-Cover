package code;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

class ManualBoard extends MouseInputAdapter {
	Point point = new Point();
	Point originPt =  MainWin.chess[0][0].getLocationOnScreen();
	Point originPt2 =  MainWin.chess[1][1].getLocationOnScreen();
	Point pressedPoint = new Point();
	Point movePt = new Point();
	
	Template moveTemp = new Template();
    int height,width;
	int row,col,lastRow, lastCol,relPos, moveTempType;
	boolean isTemp;
	
	public ManualBoard(){
		width = originPt2.x - originPt.x;
		height = originPt2.y - originPt.y;
	}
   
    public void mousePressed(MouseEvent e) {
    	if(!MainWin.manualRadioButton.isSelected()) return;
		isTemp = false;
		lastRow = lastCol = -1;
    	pressedPoint = e.getLocationOnScreen();
    	for(int k=0; k<4; k++) {
    		for(int row=MainWin.template[k].row; row<=MainWin.template[k].row+1; row++) {
    			for(int col=MainWin.template[k].col; col<=MainWin.template[k].col+1; col++) {
    				if(e.getSource()==MainWin.chess[row][col]) {
    					for(int n=0; n<4; n++) {
    						if(MainWin.spatialTemp[n][0]==row-MainWin.template[k].row 
    								&& MainWin.spatialTemp[n][1]==col-MainWin.template[k].col){
    							relPos= n;
    							break;
    						}
    					}
    					moveTemp = MainWin.template[k];
    					moveTempType = k;
    					isTemp = true;
    				}
    			}
    		}
    	}
    }
    
    public void mouseReleased(MouseEvent e) {
    	if(!MainWin.manualRadioButton.isSelected()) return;
    	boolean fix = true;
    	movePt = e.getLocationOnScreen();
    	col= Math.round((movePt.x - originPt.x)/width);
	    row= Math.round((movePt.y - originPt.y)/height);
		row -= MainWin.spatialTemp[relPos][0];
		col -= MainWin.spatialTemp[relPos][1];
		if(row>=31 || col >=31) return;
    	for(int r=row; r<= row+1; r++) {
		   for(int c = col; c <= col + 1; c++) {
			   if(r != moveTemp.whiteRow + row || c != moveTemp.whiteCol + col) { 
				   if(!(r>=MainWin.bia && r<MainWin.bia+MainWin.size && c>= MainWin.bia 
						   &&  c<MainWin.bia+MainWin.size && MainWin.color[r][c]<= -1 && 
						   (r!=MainWin.brokenPos[0] || c!=MainWin.brokenPos[1]))) {
					   fix = false;
					   break;
				   }
			   }
		   }
	    }
    	if(!fix) {
    		for(int r=row; r<= row+1; r++) {
 			   for(int c = col; c <= col + 1; c++) {
 				   if( MainWin.color[r][c]== -1) {
	 					  MainWin.chess[r][c].setBackground(Color.white);
 				   }
 				   else if( MainWin.color[r][c]== -2){
	 					  MainWin.chess[r][c].setBackground(Color.black);
 				   }
 			   }
    		}
    	}else {
    		MainWin.Statistics(true,moveTempType);
    		int three = 0;
    		for(int r=row; r<= row+1; r++) {
 			   for(int c = col; c <= col + 1; c++) {
				   if(r != moveTemp.whiteRow + row 
						   || c != moveTemp.whiteCol + col) {						   
					   MainWin.color[r][c] = moveTempType;
					   MainWin.stepList[MainWin.current][three][0]=r;
					   MainWin.stepList[MainWin.current][three][1]=c;
					   three++;
				   } 				   
 			   }
 		   }
    		MainWin.current++;
    		MainWin.manualStep= MainWin.current;
    	}	    	
    }
    
    public void mouseDragged(MouseEvent e) {
    	if(!isTemp || !MainWin.manualRadioButton.isSelected()) return;
    	movePt = e.getLocationOnScreen();
    	col= Math.round((movePt.x - originPt.x)/width);
	   row= Math.round((movePt.y - originPt.y)/height);
	   row -= MainWin.spatialTemp[relPos][0];
	   col -= MainWin.spatialTemp[relPos][1];
	   if( row != lastRow || col != lastCol) {
		   for(int r=lastRow; r<= lastRow+1; r++) {
			   for(int c = lastCol; c <= lastCol + 1; c++) {
				   if(r>=0 && r<32 && c>=0 && c<32 && MainWin.color[r][c]<=-1 &&
						   (r!=MainWin.brokenPos[0] || c!=MainWin.brokenPos[1])) {
					   if(MainWin.color[r][c]== -2)
							   MainWin.chess[r][c].setBackground(Color.black);	
					   else
						   MainWin.chess[r][c].setBackground(Color.white);	
				   }			   			   
			   }
		   }
		   for(int r=row; r<= row+1; r++) {
			   for(int c = col; c <= col + 1; c++) {
				   if(r>=0 && r<32 && c>=0 && c<32 && MainWin.color[r][c]<= -1 &&
						   (r!=MainWin.brokenPos[0] || c!=MainWin.brokenPos[1]) 
						   && (r != moveTemp.whiteRow + row 
							   || c != moveTemp.whiteCol + col)) 						   
					   MainWin.chess[r][c].setBackground(moveTemp.color);
			   }
		   }  
	    }
	   lastRow = row;
	   lastCol = col;
	   }
}
