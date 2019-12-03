package jaysc.example.com.chess.Adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import jaysc.example.com.chess.Pieces.*;
import jaysc.example.com.chess.R;


public class ChessboardAdapter extends BaseAdapter {
    public int selectedPieceIndex;
    private Context mContext;
    private Piece[] pieces;
    private TypedArray pieceImgs;
    public ChessboardAdapter(Context c, Piece[] pieces) {
        selectedPieceIndex = -1;
        mContext = c;
        this.pieces = pieces;
        pieceImgs = mContext.getResources().obtainTypedArray(R.array.piece_imgs);
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
        if (convertView == null){
            // if it's not recycled, initialize some attributes
            int side = (mContext.getResources().getDisplayMetrics().widthPixels)/8;
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(side,side));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
            //choose black or white for square
            int row = position/8;
            int col = position%8;
            //if row and col both even/odd then light, otherwise dark square
            String squareColor = (row%2 == col%2) ? "#d8d8d8" : "#30313e";
            imageView.setBackgroundColor(Color.parseColor(squareColor));
        }else{
            imageView = (ImageView)convertView;
        }
        //choose which image to assign it
        Piece p = pieces[position];
        //set image (piece image or "empty") for imageview
        int imageRes = (p == null) ? 0 : pieceImgs.getResourceId(p.getImageIndex(), 0);
        imageView.setImageResource(imageRes);
        //set highlight if position is selected
        imageView.setColorFilter((position == selectedPieceIndex) ? Color.CYAN : 0);
        return imageView;
    }
}