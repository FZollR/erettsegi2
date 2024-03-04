import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {


    @Test
    void saveCityWindFiles_shouldCreateValidFiles() throws IOException {
        // Előkészítés: Minta adatok létrehozása

        Main underTest = new Main();
        List<String> data = new ArrayList<>();
        data.add("BC 0000 000000");
        data.add("BC 0130 13006");
        data.add("BC 0515 02004");

        // Tesztelendő művelet végrehajtása
        underTest.saveCityWindFiles(underTest.extractCityWindData(data));

        // Ellenőrzés: Fájlok létezésének ellenőrzése és tartalmának ellenőrzése
        assertFileContents("BC.txt", "BC\n0000 \n0130 ######\n0515 ####\n");

        // Takarítás: Fájlok törlése
        deleteFile("BC.txt");
    }

    private void assertFileContents(String filename, String expectedContents) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder actualContents = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            actualContents.append(line).append("\n");
        }
        reader.close();
        assertEquals(expectedContents, actualContents.toString());
    }

    private void deleteFile(String filename) {
        java.io.File file = new java.io.File(filename);
        if (!file.delete()) {
            System.err.println("Failed to delete file: " + filename);
        }
    }
}
