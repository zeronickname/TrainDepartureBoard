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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.me.gman.trains.R;
import uk.me.gman.trains.model.DataObject;
import uk.me.gman.trains.model.TrainServices;


public class TrainsAdapter
        extends RecyclerView.Adapter<TrainsAdapter.TrainsViewHolder>
        implements View.OnClickListener {

    private List<DataObject> trains;
    private int rowLayout;
    private Context context;

    // Hold the position of the expanded item
    private int expandedPosition = -1;

    public static class TrainsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TableLayout llExpandArea;


        public TrainsViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.textView);
            description = v.findViewById(R.id.textView2);
            llExpandArea = v.findViewById(R.id.llExpandArea);
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

        TrainsViewHolder holder = new TrainsViewHolder(view);
        // Sets the click adapter for the entire cell
        // to the one in this class.
        holder.itemView.setOnClickListener(TrainsAdapter.this);
        holder.itemView.setTag(holder);

        return holder;
    }


    @Override
    public void onBindViewHolder(TrainsViewHolder holder, final int position) {
        if( trains.get(position).getLocInfo().getTrainServices() == null ) {
            return;
        }

        holder.title.setText(trains.get(position).getTitle(context));
        if( trains.get(position).getEtd().equals("On time") ) {
            holder.title.setBackgroundColor(0xcfd8dc);
        } else if( trains.get(position).getEtd().equals("Cancelled") ) {
            highlightTextPart(holder.title, Color.RED);
        } else {
            highlightTextPart(holder.title, Color.YELLOW);
        }

        holder.description.setText(trains.get(position).getDescription(context));

        if (position == expandedPosition) {
            holder.llExpandArea.setVisibility(View.VISIBLE);
        } else {
            holder.llExpandArea.setVisibility(View.GONE);
        }

        holder.llExpandArea.removeAllViews();
        TableRow tr_head = new TableRow(context);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView destH = new TextView(context);
        destH.setText("Destination");
        destH.setTextColor(Color.WHITE);
        destH.setPadding(5, 5, 5, 5);
        tr_head.addView(destH);// add the column to the table row here

        TextView stdH = new TextView(context);
        stdH.setText("STD"); // set the text for the header
        stdH.setTextColor(Color.WHITE); // set the color
        stdH.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(stdH); // add the column to the table row here

        TextView etdH = new TextView(context);
        etdH.setText("ETD"); // set the text for the header
        etdH.setTextColor(Color.WHITE); // set the color
        etdH.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(etdH); // add the column to the table row here

        holder.llExpandArea.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        int count =0;
        for(TrainServices train : trains.get(position).getLocInfo().getTrainServices() ) {

            TableRow tr = new TableRow(context);
            //if(count%2!=0) tr.setBackgroundColor(Color.DKGRAY);
            tr.setId(100+count);
            tr.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));


            TextView dest = new TextView(context);
            dest.setText(train.getDestination());
            dest.setPadding(2, 0, 5, 0);
            dest.setTextColor(Color.WHITE);
            tr.addView(dest);
            TextView std = new TextView(context);
            std.setText(train.getStd());
            std.setTextColor(Color.WHITE);
            tr.addView(std);
            TextView etd = new TextView(context);
            etd.setText(train.getEtd());
            etd.setTextColor(Color.WHITE);
            tr.addView(etd);

            // finally add this to the table row
            holder.llExpandArea.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            count++;
        }
    }

    @Override
    public void onClick(View view) {
        TrainsViewHolder holder = (TrainsViewHolder) view.getTag();

        // Check for an expanded view, collapse if you find one
        if (expandedPosition >= 0) {
            int prev = expandedPosition;
            expandedPosition = -1;
            notifyItemChanged(prev);
        }
        else {
            // Set the current position to "expanded"
            expandedPosition = holder.getAdapterPosition();
            notifyItemChanged(expandedPosition);
        }
    }

    @Override
    public int getItemCount() {
        return trains.size();
    }

    private void highlightTextPart(TextView textView, int highlightColor) {
        int index = 1;
        String fullText = textView.getText().toString();
        int startPos = 0;
        int endPos = fullText.length();
        String[] textParts = fullText.split(" ");

        if (textParts.length > 1) {
            startPos = fullText.indexOf(textParts[index]);
            endPos = fullText.indexOf(" ", startPos);
            if (endPos == -1) {
                endPos = fullText.length();
            }
        }
        Spannable spannable = new SpannableString(fullText);
        ColorStateList blackColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.BLACK });
        TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blackColor, null);
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(highlightColor);
        spannable.setSpan(textAppearanceSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(backgroundColorSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }
}