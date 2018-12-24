package jaysc.example.com.chess.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import jaysc.example.com.chess.Pieces.*;
import jaysc.example.com.chess.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public int selectedPieceIndex = -1;
    private Piece[] pieces;
    public ImageAdapter(Context c, Piece[] pieces) {
        mContext = c;
        this.pieces = pieces;
    }

    public int getCount() {
        return pieces.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(90, 90));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(8, 8, 8, 8);
            //choose black or white for square
            int row = position/8;
            int col = position%8;
            if ((row%2==0&&col%2==0)||(row%2!=0&&col%2!=0)){//white space
                imageView.setBackgroundColor(Color.parseColor("#d8d8d8")); //#9E9DA3
            }else{//black space
                imageView.setBackgroundColor(Color.parseColor("#30313e")); //#716792
            }
        } else {
            imageView = (ImageView) convertView;
        }
        //choose which image to assign it
        Piece p = pieces[position];
        if (p==null) {
            imageView.setImageResource(0);
        }else if (p instanceof Pawn){
            if (p.getOwner()=='w'){
                imageView.setImageResource(R.drawable.pawn_white);
            }else{
                imageView.setImageResource(R.drawable.pawn_black);
            }
        }else if (p instanceof Rook){
            if (p.getOwner()=='w'){
                imageView.setImageResource(R.drawable.rook_white);
            }else{
                imageView.setImageResource(R.drawable.rook_black);
            }
        }else if (p instanceof Bishop){
            if (p.getOwner()=='w'){
                imageView.setImageResource(R.drawable.bishop_white);
            }else{
                imageView.setImageResource(R.drawable.bishop_black);
            }
        }else if (p instanceof Knight){
            if (p.getOwner()=='w'){
                imageView.setImageResource(R.drawable.knight_white);
            }else{
                imageView.setImageResource(R.drawable.knight_black);
            }
        }else if (p instanceof Queen){
            if (p.getOwner()=='w'){
                imageView.setImageResource(R.drawable.queen_white);
            }else{
                imageView.setImageResource(R.drawable.queen_black);
            }
        }else if (p instanceof King){
            if (p.getOwner()=='w'){
                imageView.setImageResource(R.drawable.king_white);
            }else{
                imageView.setImageResource(R.drawable.king_black);
            }
        }
        if (position == selectedPieceIndex) {
           // imageView.setBackgroundColor(Color.parseColor("#50e0ff"));
            imageView.setColorFilter(Color.CYAN);
        }else{
            imageView.setColorFilter(0);
        }
        return imageView;
    }
}

