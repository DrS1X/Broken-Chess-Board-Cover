package code;

class AutoBoard{
	int r,c,len,tempType;
	static int color[][] = new int[32][32];
	static int stepList[][][] = new int[350][3][2];
	static int order;
	
	public AutoBoard(int r,int c,int size) {
		this.r = r;
		this.c = c;
		this.len = size;			
	}	
	
	static void Division(AutoBoard rt) {
		AutoBoard[] A= new AutoBoard[4];		
		int row,col,i,tempType=-1;
		
		if(rt.len == 2) {
			for(i=0; i<4; i++) {
				row = rt.r + MainWin.spatialTemp[i][0];
				col = rt.c + MainWin.spatialTemp[i][1];
				if(color[row][col] >= 0) 
					tempType = i;
			}
			Record(tempType,rt.r,rt.c);
		}
		else {
			A[0] = new AutoBoard(rt.r,rt.c,rt.len/2);
			A[1] = new AutoBoard(rt.r,rt.c+rt.len/2,rt.len/2);
			A[2] = new AutoBoard(rt.r+rt.len/2,rt.c,rt.len/2) ; // left and down
			A[3] = new AutoBoard(rt.r+rt.len/2,rt.c+rt.len/2,rt.len/2) ; // right and down
			tempType = -1;
			for (i=0; i<4; i++) {
				for( row=A[i].r; row< A[i].r + A[i].len; row++){
					for( col=A[i].c; col< A[i].c + A[i].len; col++) {
						if(color[row][col] >= 0) {
							tempType = i;						
							break;
						}
					}
					if(tempType >= 0)
						break;
				}
				if(tempType >= 0)
					break;
			}				
			Record(tempType,A[3].r-1,A[3].c-1);
			for (i = 0 ; i < 4 ; ++ i) {
				Division(A[i]);
			}
		}
	}
	
	static void Record(int tempType,int r,int c) {
		int col=0;
		for(int i=r;i<=r+1;i++) {
			for(int j=c;j<=c+1;j++) {
				if(i!= r+MainWin.spatialTemp[tempType][0] || j!= c+MainWin.spatialTemp[tempType][1]) {
					color[i][j] = tempType;
					stepList[order][col][0] = i;
					stepList[order][col][1] = j;
					col++;		
				}
			}
		}
		order++;
	}
}
