package com.epam.training.microservicefoundation.songservice.domain;

import java.io.Serializable;

public class SongRecordId implements Serializable {
    private static final long serialVersionUID = 17_11_2022_22_51L;
    private final long id;

    public SongRecordId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
