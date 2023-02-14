package com.esigelec.myrecycleview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroceryItemArrayAdapter extends RecyclerView.Adapter<GroceryItemArrayAdapter.ViewHolder>{
    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }
    private ClickListener clickListener;
    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView leftTextView;
        TextView rightTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftTextView = itemView.findViewById(R.id.leftTextView);
            rightTextView = itemView.findViewById(R.id.rightTextView);
            itemView.setOnClickListener(view -> {
                if(clickListener != null){
                    clickListener.onItemClick(view,getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(view -> {
                if(clickListener != null){
                    clickListener.onItemLongClick(view,getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.grocery_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        Log.d("ADAPTER","Item created");
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroceryItem groceryItem = Singleton.getInstance().groceryItems.get(position);
        holder.leftTextView.setText(groceryItem.name);
        holder.rightTextView.setText(""+groceryItem.quantity);
        Log.d("ADAPTER","Item binded:"+position);

    }
    @Override
    public int getItemCount() {
        return Singleton.getInstance().groceryItems.size();
    }
}
