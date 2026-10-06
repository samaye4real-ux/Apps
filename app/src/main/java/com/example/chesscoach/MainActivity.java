package com.example.chesscoach;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.widget.Toast;
import java.util.*;

public class MainActivity extends Activity {
  @Override public void onCreate(Bundle state) { super.onCreate(state); setTitle("Chess Coach"); setContentView(new BoardView()); }

  final class BoardView extends View {
    final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); final char[][] board = new char[8][8];
    int selectedRow=-1, selectedCol=-1; boolean whiteTurn=true; boolean showHints=true; final ArrayList<String> moves=new ArrayList<>(); final Random random=new Random();
    final int cream=Color.rgb(240,217,181), brown=Color.rgb(181,136,99), navy=Color.rgb(35,61,88), green=Color.rgb(64,145,91);
    BoardView(){ super(MainActivity.this); reset(); }
    void reset(){ String[] rows={"rnbqkbnr","pppppppp","........","........","........","........","PPPPPPPP","RNBQKBNR"}; for(int r=0;r<8;r++) for(int c=0;c<8;c++) board[r][c]=rows[r].charAt(c); selectedRow=selectedCol=-1; whiteTurn=true; moves.clear(); invalidate(); }
    @Override protected void onDraw(Canvas canvas){ super.onDraw(canvas); canvas.drawColor(Color.rgb(250,248,244)); float top=118, size=Math.min(getWidth()-24,getHeight()-top-178), left=(getWidth()-size)/2, cell=size/8;
      paint.setTextAlign(Paint.Align.CENTER); paint.setTypeface(Typeface.DEFAULT_BOLD); paint.setTextSize(27); paint.setColor(navy); canvas.drawText("CHESS COACH",getWidth()/2,42,paint); paint.setTypeface(Typeface.DEFAULT); paint.setTextSize(15); paint.setColor(Color.DKGRAY); canvas.drawText(whiteTurn?"Your turn · play White":"Coach is thinking…",getWidth()/2,73,paint);
      for(int r=0;r<8;r++) for(int c=0;c<8;c++){ paint.setColor((r+c)%2==0?cream:brown); canvas.drawRect(left+c*cell,top+r*cell,left+(c+1)*cell,top+(r+1)*cell,paint); if(r==selectedRow&&c==selectedCol){paint.setColor(Color.argb(170,green));canvas.drawRect(left+c*cell,top+r*cell,left+(c+1)*cell,top+(r+1)*cell,paint);} if(showHints&&selectedRow>=0&&legal(selectedRow,selectedCol,r,c)){paint.setColor(Color.argb(170,Color.BLUE));canvas.drawCircle(left+(c+.5f)*cell,top+(r+.5f)*cell,cell*.14f,paint);} char piece=board[r][c]; if(piece!='.'){paint.setTextSize(cell*.66f);paint.setColor(Character.isUpperCase(piece)?Color.WHITE:Color.BLACK);paint.setShadowLayer(3,1,2,Color.GRAY);canvas.drawText(symbol(piece),left+(c+.5f)*cell,top+(r+.73f)*cell,paint);paint.clearShadowLayer();}}
      paint.setTextSize(14);paint.setColor(Color.DKGRAY);canvas.drawText("Select a piece, then choose a highlighted square",getWidth()/2,top+size+30,paint);paint.setColor(navy);canvas.drawText("HINTS",getWidth()/2-108,top+size+76,paint);canvas.drawText("UNDO",getWidth()/2,top+size+76,paint);canvas.drawText("NEW GAME",getWidth()/2+108,top+size+76,paint);paint.setColor(Color.DKGRAY);paint.setTextSize(13);canvas.drawText("Offline beginner mode · Hints "+(showHints?"ON":"OFF"),getWidth()/2,top+size+110,paint);
    }
    String symbol(char p){ switch(Character.toLowerCase(p)){case 'k':return Character.isUpperCase(p)?"♔":"♚";case 'q':return Character.isUpperCase(p)?"♕":"♛";case 'r':return Character.isUpperCase(p)?"♖":"♜";case 'b':return Character.isUpperCase(p)?"♗":"♝";case 'n':return Character.isUpperCase(p)?"♘":"♞";default:return Character.isUpperCase(p)?"♙":"♟";} }
    boolean in(int r,int c){return r>=0&&r<8&&c>=0&&c<8;}
    boolean legal(int sr,int sc,int tr,int tc){ if(!in(sr,sc)||!in(tr,tc))return false; char a=board[sr][sc], b=board[tr][tc]; if(a=='.'||(Character.isUpperCase(a)!=whiteTurn)||(b!='.'&&Character.isUpperCase(a)==Character.isUpperCase(b)))return false; int dr=tr-sr, dc=tc-sc; char kind=Character.toLowerCase(a); if(kind=='p'){int d=Character.isUpperCase(a)?-1:1,start=Character.isUpperCase(a)?6:1;if(dc==0&&b=='.'&&(dr==d||(sr==start&&dr==2*d&&board[sr+d][sc]=='.')))return true;return Math.abs(dc)==1&&dr==d&&b!='.';} if(kind=='n')return Math.abs(dr)*Math.abs(dc)==2; if(kind=='k')return Math.max(Math.abs(dr),Math.abs(dc))==1; if(kind=='b'&&Math.abs(dr)!=Math.abs(dc))return false; if(kind=='r'&&dr!=0&&dc!=0)return false; if(kind=='q'&&!((dr==0)||(dc==0)||(Math.abs(dr)==Math.abs(dc))))return false; int rr=Integer.signum(dr),cc=Integer.signum(dc),r=sr+rr,c=sc+cc; while(r!=tr||c!=tc){if(board[r][c]!='.')return false;r+=rr;c+=cc;} return true; }
    @Override public boolean onTouchEvent(MotionEvent e){ if(e.getAction()!=MotionEvent.ACTION_UP)return true; float top=118,size=Math.min(getWidth()-24,getHeight()-top-178),left=(getWidth()-size)/2,x=e.getX(),y=e.getY(); if(y>=top&&y<top+size){int c=(int)((x-left)/(size/8)),r=(int)((y-top)/(size/8));if(!in(r,c))return true;if(selectedRow<0){if(board[r][c]!='.'&&Character.isUpperCase(board[r][c])&&whiteTurn){selectedRow=r;selectedCol=c;invalidate();}}else if(legal(selectedRow,selectedCol,r,c)){makeMove(selectedRow,selectedCol,r,c);selectedRow=selectedCol=-1;invalidate();if(!whiteTurn)postDelayed(this::coachMove,350);}else{selectedRow=selectedCol=-1;invalidate();}}else if(y>top+size+45&&y<top+size+102){if(x<getWidth()/3){showHints=!showHints;invalidate();}else if(x<getWidth()*2/3){undo();invalidate();}else reset();} return true; }
    void makeMove(int sr,int sc,int tr,int tc){moves.add(""+sr+sc+tr+tc+board[tr][tc]);char p=board[sr][sc];board[tr][tc]=p;board[sr][sc]='.';whiteTurn=!whiteTurn;if(Character.toLowerCase(p)=='k')Toast.makeText(MainActivity.this,"Keep your king safe!",Toast.LENGTH_SHORT).show();}
    void coachMove(){boolean old=whiteTurn;whiteTurn=false;ArrayList<int[]> options=new ArrayList<>();for(int r=0;r<8;r++)for(int c=0;c<8;c++)if(board[r][c]!='.'&&!Character.isUpperCase(board[r][c]))for(int tr=0;tr<8;tr++)for(int tc=0;tc<8;tc++)if(legal(r,c,tr,tc))options.add(new int[]{r,c,tr,tc});whiteTurn=old;if(!options.isEmpty()){int[]m=options.get(random.nextInt(options.size()));makeMove(m[0],m[1],m[2],m[3]);invalidate();}}
    void undo(){for(int i=0;i<2&&moves.size()>0;i++){String s=moves.remove(moves.size()-1);int sr=s.charAt(0)-48,sc=s.charAt(1)-48,tr=s.charAt(2)-48,tc=s.charAt(3)-48;board[sr][sc]=board[tr][tc];board[tr][tc]=s.charAt(4);whiteTurn=true;}}
  }
}
