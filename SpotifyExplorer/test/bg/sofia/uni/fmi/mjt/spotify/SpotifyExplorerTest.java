package bg.sofia.uni.fmi.mjt.spotify;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.LinkedHashMap;



import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SpotifyExplorerTest {
    @Before
    public void createFile() {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of("fileToRead"))) {
            bufferedWriter.write("id	artists	name	year	popularity	duration_ms	tempo	loudness	" +
                    "valence	acousticness	danceability	energy	liveness	" +
                    "speechiness	explicit" + System.lineSeparator());
            bufferedWriter.write("4BJqT0Pr,['SergeiPiano Co']," +
            "Piano Concerto No. 3 in D Minor Op. 30: III. Finale. Alla breve,1921,4,83166,80.954," +
            "-20.096,0.0594,0.982,0.279,0.211,0.665,0.0366,0" + System.lineSeparator());
            bufferedWriter.write("7xPhfUan,['DennisClancy L'],Clancy Lowered the Boom,1921,5,180533,60.936," +
                    "-12.441,0.963,0.732,0.819,0.341,0.16,0.415,0" + System.lineSeparator());
            bufferedWriter.write("1o6I8Bgl,['KHP KrGati Bal'],Gati Bali,1921,5,500062,110.339," +
                    "-14.85,0.0394,0.961,0.328,0.166,0.101,0.0339,0" + System.lineSeparator());
            bufferedWriter.write("3ftBPsC5,['Frank Danny Bo'],Danny Boy,1983,3,210000,100.109," +
                    "-9.316,0.165,0.967,0.275,0.309,0.381,0.0354,1" + System.lineSeparator());
            bufferedWriter.write("4d6HGyGT,['Phil RWhen Iri'],When Irish Eyes Are Smiling,1983,2,166693,101.665," +
                    "-10.096,0.253,0.957,0.418,0.193,0.229,0.038,1" + System.lineSeparator());
            bufferedWriter.write("4pyw9DVH,['KHP KrGati Mar'],Gati Mardika,1996,6,395076,119.824," +
                    "-12.506,0.196,0.579,0.697,0.346,0.13,0.07,0" + System.lineSeparator());
            bufferedWriter.write("5uNZnElq,['John MThe Wear'],The Wearing of the Green,1921,4,159507,66.221," +
                    "-10.589,0.406,0.996,0.518,0.203,0.115,0.0615,0" + System.lineSeparator());
            bufferedWriter.write("02GDntOX,['KHP KrGati Mar']," +
                    "Morceaux de fantaisie Op. 3: No. 2 PrГ©lude in C-Sharp Minor. Lento,1999,2,218773,92.867," +
                    "-21.091,0.0731,0.993,0.389,0.088,0.363,0.0456,0");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
    @Before
    public void createEmptyFile() {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of("emptyFile"))) {
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testGetAllSpotifyTracks() {
        Path pathOfFileToRead = Path.of("fileToRead");
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathOfFileToRead)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(bufferedReader);
            Set<String> result = new LinkedHashSet<>(Set.of("4BJqT0Pr", "7xPhfUan", "1o6I8Bgl",
                    "3ftBPsC5", "4d6HGyGT", "4pyw9DVH", "5uNZnElq", "02GDntOX"));
            if (result.size() != spotifyExplorer.getAllSpotifyTracks().size()) {
                fail();
            }
            for (SpotifyTrack x : spotifyExplorer.getAllSpotifyTracks()) {
                assertTrue(result.contains(x.id()));
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testGetExcplicitSpotifyTracks() {
        Path pathOfFileToRead = Path.of("fileToRead");
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathOfFileToRead)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(bufferedReader);
            Set<String> result = new LinkedHashSet<>(Set.of("3ftBPsC5", "4d6HGyGT"));
            if (result.size() != spotifyExplorer.getExplicitSpotifyTracks().size()) {
                fail();
            }
            for (SpotifyTrack x : spotifyExplorer.getExplicitSpotifyTracks()) {
                assertTrue(result.contains(x.id()));
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testGroupSpotifyTracksByYears() {
        Path pathOfFileToRead = Path.of("fileToRead");
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathOfFileToRead)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(bufferedReader);
            Map<Integer, Set<String>> result = new LinkedHashMap<>();
            result.put(1921, Set.of("4BJqT0Pr", "7xPhfUan", "1o6I8Bgl", "5uNZnElq"));
            result.put(1996, Set.of("4pyw9DVH"));
            result.put(1999, Set.of("02GDntOX"));
            result.put(1983, Set.of("3ftBPsC5", "4d6HGyGT"));
            for (Map.Entry<Integer, Set<SpotifyTrack>> x : spotifyExplorer.groupSpotifyTracksByYear().entrySet()) {
                if (result.get(x.getKey()).size() != x.getValue().size()) {
                    fail();
                }
                for (SpotifyTrack spotifyTrack : x.getValue()) {
                    assertTrue(result.get(x.getKey()).contains(spotifyTrack.id()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testGetArtistActiveYears() {
        Path pathOfFileToRead = Path.of("fileToRead");
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathOfFileToRead)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(bufferedReader);
            assertEquals(4, spotifyExplorer.getArtistActiveYears("KHP KrGati Mar"));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80s() {
        Path pathOfFileToRead = Path.of("fileToRead");
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathOfFileToRead)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(bufferedReader);
            assertEquals("4d6HGyGT", spotifyExplorer.getTopNHighestValenceTracksFromThe80s(1).get(0).id());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testGetMostPopularTrackFromThe90s() {
        Path pathOfFileToRead = Path.of("fileToRead");
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathOfFileToRead)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(bufferedReader);
            assertEquals("4pyw9DVH", spotifyExplorer.getMostPopularTrackFromThe90s().id());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Test (expected = NoSuchElementException.class)
    public void testGetMostPopularTrackFromThe90sThrowsException() {
        Path pathOfFileToRead = Path.of("emptyFile");
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathOfFileToRead)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(bufferedReader);
            spotifyExplorer.getMostPopularTrackFromThe90s();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testGetNumberOfLongerTracksBeforeYear() {
        Path pathOfFileToRead = Path.of("fileToRead");
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathOfFileToRead)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(bufferedReader);
            assertEquals(2, spotifyExplorer.getNumberOfLongerTracksBeforeYear(3, 1922));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testGetTheLoudestTrackInYear() {
        Path pathOfFileToRead = Path.of("fileToRead");
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathOfFileToRead)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(bufferedReader);
            assertEquals("5uNZnElq", spotifyExplorer.getTheLoudestTrackInYear(1921).get().id());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
