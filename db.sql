CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

---
-- Tabla Country
---
CREATE TABLE Country (
    country_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    iso_code2 VARCHAR(2) NOT NULL UNIQUE,
    iso_code3 VARCHAR(3) UNIQUE,
);
CREATE INDEX idx_country_name ON Country (name);
CREATE INDEX idx_country_iso2 ON Country (iso_code2);

---
-- Tabla Artist
---
CREATE TABLE Artist (
    artist_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    bio TEXT,
    country_id UUID,
    image_url VARCHAR(512),
    FOREIGN KEY (country_id) REFERENCES Country(country_id) ON DELETE SET NULL
);
CREATE INDEX idx_artist_name ON Artist (name);
CREATE INDEX idx_artist_country_id ON Artist (country_id);

---
-- Tabla Genre
---
CREATE TABLE Genre (
    genre_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);
CREATE INDEX idx_genre_name ON Genre (name);

---
-- Tabla Album
---
CREATE TABLE Album (
    album_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    release_date DATE,
    cover_art_url VARCHAR(512),
    artist_id UUID,
    total_tracks INT,
    record_label VARCHAR(255),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id) ON DELETE SET NULL
);
CREATE INDEX idx_album_artist_id ON Album (artist_id);
CREATE INDEX idx_album_title ON Album (title);

---
-- Tabla Song
---
CREATE TABLE Song (
    song_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    duration_seconds INT CHECK (duration_seconds > 0),
    audio_file_path VARCHAR(512) NOT NULL,
    is_explicit BOOLEAN DEFAULT FALSE,
    play_count BIGINT DEFAULT 0,
    album_id UUID,
    FOREIGN KEY (album_id) REFERENCES Album(album_id) ON DELETE SET NULL
);
CREATE INDEX idx_song_album_id ON Song (album_id);
CREATE INDEX idx_song_title ON Song (title);

-- Tabla intermedia Song_Artist
CREATE TABLE Song_Artist (
    song_id UUID NOT NULL,
    artist_id UUID NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (song_id, artist_id),
    FOREIGN KEY (song_id) REFERENCES Song(song_id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id) ON DELETE CASCADE
);
CREATE INDEX idx_song_artist_song_id ON Song_Artist (song_id);
CREATE INDEX idx_song_artist_artist_id ON Song_Artist (artist_id);

-- Tabla intermedia Song_Genre
CREATE TABLE Song_Genre (
    song_id UUID NOT NULL,
    genre_id UUID NOT NULL,
    PRIMARY KEY (song_id, genre_id),
    FOREIGN KEY (song_id) REFERENCES Song(song_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES Genre(genre_id) ON DELETE CASCADE
);
CREATE INDEX idx_song_genre_song_id ON Song_Genre (song_id);
CREATE INDEX idx_song_genre_genre_id ON Song_Genre (genre_id);
