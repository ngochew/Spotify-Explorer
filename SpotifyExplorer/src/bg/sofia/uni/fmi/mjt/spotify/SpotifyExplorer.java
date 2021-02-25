package bg.sofia.uni.fmi.mjt.spotify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.Comparator;
import java.util.NoSuchElementException;


public class SpotifyExplorer {

    private final List<SpotifyTrack> spotifyTracks;


    public SpotifyExplorer(Reader dataInput) {
        try (var bufferedReader = new BufferedReader(dataInput)) {
            bufferedReader.readLine();
            spotifyTracks = bufferedReader.lines().map(SpotifyTrack::of).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public Collection<SpotifyTrack> getAllSpotifyTracks() {
        return spotifyTracks;
    }

    public Collection<SpotifyTrack> getExplicitSpotifyTracks() {
        return spotifyTracks.stream()
                .filter(SpotifyTrack::explicit)
                .collect(Collectors.toUnmodifiableList());
    }

    public Map<Integer, Set<SpotifyTrack>> groupSpotifyTracksByYear() {
        return spotifyTracks.stream()
                .collect(Collectors.groupingBy(SpotifyTrack::year, Collectors.toSet()));
    }

    public int getArtistActiveYears(String artist) {
        Optional<Integer> startYear = spotifyTracks.stream()
                .filter(p -> p.artists().contains(artist))
                .map(SpotifyTrack::year)
                .min(Comparator.comparing(Integer::valueOf));
        Optional<Integer> endYear = spotifyTracks.stream()
                .filter(p -> p.artists().contains(artist))
                .map(SpotifyTrack::year)
                .max(Comparator.comparing(Integer::valueOf));

        if (startYear.isPresent() && endYear.isPresent()) {
            return endYear.get() - startYear.get() + 1;
        }
        return 0;
    }

    public List<SpotifyTrack> getTopNHighestValenceTracksFromThe80s(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        return spotifyTracks.stream()
                .filter(p -> p.year() > 1979 && p.year() < 1990)
                .sorted(Comparator.comparing(SpotifyTrack::valence).reversed())
                .limit(n)
                .collect(Collectors.toUnmodifiableList());
    }

    public SpotifyTrack getMostPopularTrackFromThe90s() {
        return spotifyTracks.stream()
                .filter(p -> p.year() > 1989 && p.year() < 2000)
                .max(Comparator.comparing(SpotifyTrack::popularity))
                .orElseThrow(NoSuchElementException::new);
    }

    public long getNumberOfLongerTracksBeforeYear(int minutes, int year) {
        if (minutes < 0 || year < 0) {
            throw new IllegalArgumentException();
        }

        final int SECONDS_IN_MINUTE = 60;
        final int MILLISECONDS_IN_SECOND = 1000;
        long ms = minutes * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND;

        return spotifyTracks.stream()
                .filter(p -> p.year() < year && p.duration() > ms)
                .count();
    }

    public Optional<SpotifyTrack> getTheLoudestTrackInYear(int year) {
        if (year < 0) {
            throw new IllegalArgumentException();
        }
        return spotifyTracks.stream()
                .filter(p -> p.year() == year)
                .max(Comparator.comparing(SpotifyTrack::loudness));
    }
}