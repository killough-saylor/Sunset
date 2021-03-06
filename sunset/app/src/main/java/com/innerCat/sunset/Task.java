package com.innerCat.sunset;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.innerCat.sunset.room.Converters;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

//table name is 'entries'
@Entity(tableName = "tasks")
public class Task implements Comparable<Task> {

    //set the primary key to auto generate and increment
    @PrimaryKey(autoGenerate = true)
    //placeholder id
    private int id = 0;
    private String name;
    private LocalDate date;
    private boolean complete = false;
    private boolean late = false;

    @ColumnInfo(defaultValue = "0")
    private int repeatTimes = 0;


    //constructor
    public Task( String name ) {
        this.name = name;
        this.date = LocalDate.now();

    }


    public void setId( int id ) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate( LocalDate date ) {
        this.date = date;
    }

    public String getDateString() {
        return Converters.dateToTimestamp(this.date);
    }

    public void setComplete( boolean complete ) {
        this.complete = complete;
    }

    public void toggleComplete() {
        this.complete = (this.complete == false);
        if (this.complete == true) {
            if ((int)DAYS.between(this.date, LocalDate.now()) != 0) {
                late = true;
            }
        }
    }

    public boolean getComplete() {
        return this.complete;
    }

    public boolean isLate() {
        return this.late;
    }

    public void setLate( boolean late ) {
        this.late = late;
    }

    public void setRepeatTimes(int repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    public int getRepeatTimes() {
        return this.repeatTimes;
    }

    @Override
    public int compareTo( Task o ) {
        return (o.getId() - this.getId());
    }

    @Override
    public boolean equals( Object o ) {
        if (!(o instanceof Task)) {
            return false;
        }
        Task c = (Task) o;
        if (c.getId() == this.getId() && c.getName().equals(this.getName())) {
            return true;
        } else {
            return false;
        }

    }
}

