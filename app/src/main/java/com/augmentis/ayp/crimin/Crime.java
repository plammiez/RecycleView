package com.augmentis.ayp.crimin;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Rawin on 18-Jul-16.
 */
public class Crime {
    private UUID id;
    private String title;
    private Date crimeDate;
    private boolean solved;

    public Crime() {
        crimeDate = new Date();
        id = UUID.randomUUID();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getId() {

        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getCrimeDate() {
        return crimeDate;
    }

    public void setCrimeDate(Date crimeDate) {
        this.crimeDate = crimeDate;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UUID=").append(id);
        builder.append(",Title=").append(title);
        builder.append(",Crime Date=").append(crimeDate);
        builder.append(",Solved=").append(solved);
        return builder.toString();
    }
}
