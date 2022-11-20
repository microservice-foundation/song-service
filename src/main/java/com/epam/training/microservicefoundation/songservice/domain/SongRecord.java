package com.epam.training.microservicefoundation.songservice.domain;

import java.io.Serializable;

public class SongRecord implements Serializable {
    private static final long serialVersionUID = 2022_10_24_19_44L;
    private long resourceId;
    private String name;
    private String artist;
    private String album;
    private String length;
    private int year;

    protected SongRecord() {}
    private SongRecord(Builder builder) {
        this.resourceId = builder.resourceId;
        this.name = builder.name;
        this.artist = builder.artist;
        this.album = builder.album;
        this.length = builder.length;
        this.year = builder.year;
    }
    public static class Builder {
        private final long resourceId;
        private final String name;
        private String artist;
        private String album;
        private final String length;
        private int year;

        public Builder(long resourceId, String name, String length) {
            this.resourceId = resourceId;
            this.name = name;
            this.length = length;
        }

        public Builder artist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder album(String album) {
            this.album = album;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public SongRecord build() {
            return new SongRecord(this);
        }
    }

    public long getResourceId() {
        return resourceId;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getLength() {
        return length;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "SongRecord{" +
                "resourceId=" + resourceId +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", length='" + length + '\'' +
                ", year=" + year +
                '}';
    }
}
