import java.rmi.RemoteException;
import java.util.ArrayList;

public class Database {
    ArrayList<User> users = new ArrayList<>();
    int next_user_id = 0;

    ArrayList<Album> albums = new ArrayList<>();
    int next_album_id = 0;

    ArrayList<Song> songs = new ArrayList<>();
    int next_song_id = 0;

    ArrayList<Artist> artists = new ArrayList<>();
    int next_artist_id = 0;

    ArrayList<Genre> genres = new ArrayList<>();
    int next_genre_id = 0;

    // User
    int user_findIndexUsername(User new_user) {
        for (User user : this.users) {
            if (user.username.equals(new_user.username)) {
                return this.users.indexOf(user);
            }
        }

        return -1;
    }

    void user_create(User user) throws CustomException {
        int index = this.user_findIndexUsername(user);

        if (index == -1) {
            if (this.users.size() == 0) user.becomeEditor();

            user.id = this.next_user_id;
            this.next_user_id += 1;
            this.users.add(user);
        } else {
            throw new CustomException("Username already exists");
        }
    }

    User user_findByUsername(String username) throws CustomException {
        for (User user : this.users) {
            if (user == null) continue;

            if (user.username.equals(username)) {
                return user;
            }
        }

        throw new CustomException("Username not found");
    }

    ArrayList<User> normal_users() {
        ArrayList<User> normal_users = new ArrayList<>();

        for (User user : this.users) {
            if (!user.isEditor) normal_users.add(user);
        }

        return normal_users;
    }

    User user_find(int id) throws CustomException {
        for (User user : this.users) {
            if (user.id == id) return user;
        }

        throw new CustomException("User not found");
    }

    void user_promote(int id) throws CustomException {
        User user = this.user_find(id);
        user.becomeEditor();
    }

    // Album
    ArrayList<Album> album_all() {
        return this.albums;
    }

    void album_create(Album album) {
        album.id = this.next_album_id;
        this.next_album_id += 1;
        this.albums.add(album);
    }

    Album album_find(int id) throws CustomException {
        for(Album album : this.albums) {
            if (album == null) continue;

            if (album.id == id) return album;
        }

        throw new CustomException("Album not found");
    }

    int album_findIndex(Album new_album) {
        for (Album album : this.albums) {
            if (album.id == new_album.id) {
                return this.albums.indexOf(album);
            }
        }

        return -1;
    }

    void album_update(Album new_album) throws CustomException {
        int index = this.album_findIndex(new_album);

        if (index == -1) {
            this.albums.set(new_album.id, new_album);
        } else {
            throw new CustomException("Album not found");
        }
    }

    void album_delete(int id) {
        Album album = null;
        try {
            album = this.album_find(id);
        } catch (CustomException e) {
            // Return if album not found
            return;
        }
        this.albums.remove(album);
    }

    String album_artists(int id) throws CustomException {
        Album album = this.album_find(id);
        ArrayList<Integer> artist_ids = new ArrayList<>();
        String artists = "";
        Artist artist;

        for (int song_id : album.song_ids) {
            Song song;

            try {
                song = this.song_find(song_id);
            } catch (CustomException ce) {
                // Skip song if not found
                continue;
            }

            for (int artist_id : song.artist_ids) {
                if (!artist_ids.contains(artist_id)) {
                    artist_ids.add(artist_id);
                }
            }
        }

        for (int artist_id : artist_ids) {
            try {
                artist = this.artist_find(artist_id);

                if (artists.length() == 0) {
                    artists = artist.name;
                } else {
                    artists += ", " + artist.name;
                }
            } catch (CustomException ce) {
                // Ignore if artist is missing
            }
        }

        return artists;
    }

    String album_genres(int id) throws CustomException {
        Album album = this.album_find(id);
        ArrayList<Integer> genre_ids = new ArrayList<>();
        String genres = "";
        Genre genre;

        for (int song_id : album.song_ids) {
            Song song;

            try {
                song = this.song_find(song_id);
            } catch (CustomException ce) {
                continue;
            }

            for (int genre_id : song.genres_ids) {
                if (!genre_ids.contains(genre_id)) {
                    genre_ids.add(genre_id);
                }
            }
        }

        for (int genre_id : genre_ids) {
            try {
                genre = this.genre_find(genre_id);

                if (genres.length() == 0) {
                    genres = genre.name;
                } else {
                    genres += ", " + genre.name;
                }
            } catch (CustomException ce) {
                // Ignore if genre is missing
            }
        }

        return genres;
    }

    // Critic
    ArrayList<Critic> album_critics(int album_id) throws CustomException {
        for(Album album : this.albums) {
            if (album == null) continue;

            if (album.id == album_id) return album.critics;
        }

        throw new CustomException("Album not found");
    }

    void album_critic_create(Critic critic) throws CustomException {
        Album album = this.album_find(critic.album.id);
        album.points += critic.rating;
        album.addCritic(critic);
    }

    Critic album_critic_find(int album_id, int critic_pos) throws CustomException {
        try {
            Album album = this.album_find(album_id);
            return album.critics.get(critic_pos);
        } catch (CustomException ce) {
            ce.extraFlag = 1;
            throw ce;
        } catch (IndexOutOfBoundsException ioobe) {
            throw new CustomException("Critic not found");
        }
    }

    // Song
    ArrayList<Song> album_song_all(int album_id) throws CustomException {
        Album album = this.album_find(album_id);
        ArrayList<Song> songs = new ArrayList<>();
        Song song;

        for (int song_id : album.song_ids) {
            try {
                song = this.song_find(song_id);
                songs.add(song);
            } catch (CustomException ce) {
                // ignore if song not found
            }
        }

        return songs;
    }

    void album_song_create(int album_id, Song song) throws CustomException {
        Album album;

        try {
            album = this.album_find(album_id);
        } catch (CustomException ce) {
            ce.extraFlag += 1;
            throw ce;
        }

        song.id = this.next_song_id;
        this.songs.add(song);
        this.next_song_id += 1;
        album.addSong(song.id);
    }

    int song_findIndex(Song new_song) {
        for (Song song : this.songs) {
            if (song.id == new_song.id) {
                return this.songs.indexOf(song);
            }
        }

        return -1;
    }

    int song_findIndexByName(Song new_song) {
        for (Song song : this.songs) {
            if (song.name.equals(new_song.name)) {
                return this.songs.indexOf(song);
            }
        }

        return -1;
    }

    void album_song_update(Song new_song) throws CustomException {
        int index = this.song_findIndex(new_song);
        int index2 = this.song_findIndexByName(new_song);

        if (index != -1 || new_song.id == this.songs.get(index2).id) {
            this.songs.set(index, new_song);
        } else {
            throw new CustomException("Song not found");
        }
    }

    void album_song_delete(int album_id, int song_id) {
        try {
            this.album_find(album_id).removeSong(song_id);
        } catch (CustomException ce) {
            // ignore if album not found
        }

        try {
            this.songs.remove(this.song_find(song_id));
        } catch (CustomException ce) {
            // ignore if song not found
        }
    }

    Song song_find(int song_id) throws CustomException {
        for (Song song : this.songs) {
            if (song.id == song_id) return song;
        }

        throw new CustomException("Song not found");
    }

    // Genre
    ArrayList<Genre> genre_all() { return this.genres; }

    Genre genre_find(int id) throws CustomException {
        for (Genre genre : this.genres) {
            if (genre.id == id) return genre;
        }

        throw new CustomException("Genre not found");
    }

    void genre_create(Genre genre) throws CustomException {
        int index = this.genre_findIndexByName(genre);

        if (index == -1) {
            genre.id = next_genre_id;
            this.genres.add(genre);
            next_genre_id += 1;
        } else {
            throw new CustomException("Genre already exists");
        }
    }

    int genre_findIndexByName(Genre new_genre) {
        for (Genre genre : this.genres) {
            if (genre.name.equals(new_genre.name)) {
                return this.genres.indexOf(genre);
            }
        }

        return -1;
    }

    // Artist
    ArrayList<Artist> artist_all() { return this.artists; }

    int artist_findIndex(Artist new_artist) {
        for (Artist artist : this.artists) {
            if (artist.id == new_artist.id) {
                return this.artists.indexOf(artist);
            }
        }

        return -1;
    }

    int artist_findIndexByName(Artist new_artist) {
        for (Artist artist : this.artists) {
            if (artist.name.equals(new_artist.name)) {
                return this.artists.indexOf(artist);
            }
        }

        return -1;
    }

    void artist_create(Artist artist) throws CustomException {
        int index = this.artist_findIndexByName(artist);

        if (index == -1) {
            artist.id = this.next_artist_id;
            this.artists.add(artist);
            this.next_artist_id += 1;
        } else {
            throw new CustomException("Artist already exists");
        }
    }

    Artist artist_find(int id) throws CustomException {
        for(Artist artist : this.artists) {
            if (artist.id == id) return artist;
        }

        throw new CustomException("Artist not found");
    }

    void artist_update(Artist new_artist) throws CustomException {
        int index = this.artist_findIndex(new_artist);

        if (index == -1) {
            this.artists.set(new_artist.id, new_artist);
        } else {
            throw new CustomException("Artist name already exists");
        }
    }

    void artist_delete(int id) {
        Artist artist;

        try {
            artist = this.artist_find(id);
            this.artists.remove(artist);
        } catch (CustomException ce) {
            // Ignore if artist doesnt exist
        }
    }

    void album_song_genre_add(int song_id, int genre_id) throws CustomException {
        Song song;
        Genre genre = this.genre_find(genre_id);

        try {
            song = this.song_find(song_id);
        } catch (CustomException ce) {
            ce.extraFlag += 1;
            throw ce;
        }

        song.addGenre(genre);
    }

    void album_song_genre_remove(int song_id, int genre_id) {
        Song song;
        Genre genre;

        try {
            song = this.song_find(song_id);
            genre = this.genre_find(genre_id);
            song.removeGenre(genre);
        } catch (CustomException ce) {
            // if song, album or genre not found ignore
        }
    }

    // Song Artists
    void album_song_artist_add(int song_id, int artist_id) throws CustomException {
        Song song;
        Artist artist = this.artist_find(artist_id);

        try {
            song = this.song_find(song_id);
        } catch (CustomException ce) {
            ce.extraFlag += 1;
            throw ce;
        }

        song.addArtist(artist);
        artist.addSong(song);
    }

    void album_song_artist_remove(int song_id, int artist_id) {
        Song song;
        Artist artist;

        try {
            song = this.song_find(song_id);
            artist = this.artist_find(artist_id);
            song.removeArtist(artist);
            artist.removeSong(song);
        } catch (CustomException ce) {
            // if song, album or genre not found ignore
        }
    }
}