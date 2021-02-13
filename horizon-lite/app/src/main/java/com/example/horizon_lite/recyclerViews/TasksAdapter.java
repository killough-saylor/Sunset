package com.example.horizon_lite.recyclerViews;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.horizon_lite.R;
import com.example.horizon_lite.Task;
import com.example.horizon_lite.activities.MainActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.temporal.ChronoUnit.DAYS;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class TasksAdapter extends
        RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private List<Task> tasks;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView bulletPoint;
        public TextView nameTextView;
        public CheckBox checkBox;
        public Task task;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder( View itemView, Context context) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            bulletPoint = (TextView) itemView.findViewById(R.id.bulletPoint);
            nameTextView = (TextView) itemView.findViewById(R.id.nameView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task.toggleComplete();
                    int currentPosition = tasks.indexOf(task);
                    if (task.getComplete() == true) {
                        boolean found = false;
                        while ( found == false ) {
                            for (int i = currentPosition; i < tasks.size()-1  ; i++) {
                                if (tasks.get(i+1).getComplete() == false) {
                                    Collections.swap(tasks, i, i+1);
                                } else {
                                    found = true;
                                    break;
                                }
                            }
                            if (found == false) {
                                //reached end
                                found = true;
                            }
                        }
                        int newPosition = tasks.indexOf(task);
                        notifyItemMoved(currentPosition, newPosition);
                    } else {
                        Task task = tasks.get(currentPosition);
                        tasks.remove(currentPosition);
                        tasks.add(0, task);
                        notifyItemMoved(currentPosition, 0);
                    }
//                    StringBuilder printOut = new StringBuilder();
//                    for (Task task : tasks) {
//                        printOut.append(task.getName()).append(", ");
//                    }
//                    System.out.println(printOut.toString());
                    //ROOM Threads
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    executor.execute(() -> {
                        //Background work here
                        MainActivity.taskDatabase.taskDao().update(task);
                        handler.post(() -> {
                            ((MainActivity)context).updateStreak();
                        });
                    });
                }
            });
        }
    }


    /**
     * Pass in the tasks array into the Adapter
     * @param tasks
     */
    public TasksAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }


    /**
     * Add a task
     * @param position the position of the new Task in the List
     * @param task the Task to add
     */
    public void addTask(int position, Task task) {
        tasks.add(position, task);
    }

    public void removeAllChecked() {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getComplete() == true) {
                tasks.remove(i);
                notifyItemRemoved(i);
                i = i-1;
            }
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public TasksAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View taskView = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(taskView, context);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TasksAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        holder.task = tasks.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(holder.task.getName());
        CheckBox checkBox = holder.checkBox;
        checkBox.setChecked(holder.task.getComplete());
        if ((int)DAYS.between(holder.task.getDate(), LocalDate.now()) == 0) {
            holder.bulletPoint.setVisibility(View.GONE);
        }

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return tasks.size();
    }


}
