package com.radiance.android.chatapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.radiance.android.chatapp.R;
import com.radiance.android.chatapp.model.Event;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Event> EventArrayList;
    private static String today, nowyear;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message, startTime;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message);
            startTime = (TextView) view.findViewById(R.id.startTime);
        }
    }

    public EventsAdapter(Context mContext, ArrayList<Event> eventArrayList) {
        this.mContext = mContext;
        this.EventArrayList = eventArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                String.valueOf(calendar.get(Calendar.MONTH)) + " " +
                String.valueOf(calendar.get(Calendar.YEAR));
        nowyear = String.valueOf(calendar.get(Calendar.YEAR));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = EventArrayList.get(position);
        holder.name.setText(event.getName());

        String start = "Start " + getStartAt(event.getStartAt());
        holder.startTime.setText(start);
    }

    @Override
    public int getItemCount() {
        return EventArrayList.size();
    }

    public static String getStartAt(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd mm yyyy");
            String dateToday = todayFormat.format(date);

            SimpleDateFormat nowyearFormat = new SimpleDateFormat("yyyy");
            String yearToday = nowyearFormat.format(date);

            if(dateToday.equals(today)){
                format = new SimpleDateFormat("hh:mm");
            }else if(yearToday.equals(nowyear)){
                format = new SimpleDateFormat("dd LLL, hh:mm");
            }else{
                format = new SimpleDateFormat("dd LLL yy, hh:mm");
            }

            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private EventsAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final EventsAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
