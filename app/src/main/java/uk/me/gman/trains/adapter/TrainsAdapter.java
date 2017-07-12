package uk.me.gman.trains.adapter;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.me.gman.trains.R;
import uk.me.gman.trains.model.DataObject;


public class TrainsAdapter extends RecyclerView.Adapter<TrainsAdapter.TrainsViewHolder> {

    private List<DataObject> trains;
    private int rowLayout;
    private Context context;


    public static class TrainsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;


        public TrainsViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.textView);
            description = v.findViewById(R.id.textView2);
        }
    }

    public TrainsAdapter(List<DataObject> trains, int rowLayout, Context context) {
        this.trains = trains;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public TrainsAdapter.TrainsViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new TrainsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TrainsViewHolder holder, final int position) {
        holder.title.setText(trains.get(position).getTitle(context));
        if( trains.get(position).getEtd().equals("On time") ) {
            holder.title.setBackgroundColor(0xcfd8dc);
        } else {
            //holder.title.setBackgroundColor(Color.RED);
            highlightTextPart(holder.title, 1, " ,");
        }

        holder.description.setText(trains.get(position).getDescription(context));
    }

    @Override
    public int getItemCount() {
        return trains.size();
    }

    private void highlightTextPart(TextView textView, int index, String regularExpression) {
        String fullText = textView.getText().toString();
        int startPos = 0;
        int endPos = fullText.length();
        String[] textParts = fullText.split(regularExpression);
        if (index < 0 || index > textParts.length - 1) {
            return;
        }
        if (textParts.length > 1) {
            startPos = fullText.indexOf(textParts[index]);
            endPos = fullText.indexOf(regularExpression, startPos);
            if (endPos == -1) {
                endPos = fullText.length();
            }
        }
        Spannable spannable = new SpannableString(fullText);
        ColorStateList blackColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.BLACK });
        TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, Typeface.BOLD_ITALIC, -1, blackColor, null);
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.RED);
        spannable.setSpan(textAppearanceSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(backgroundColorSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }
}