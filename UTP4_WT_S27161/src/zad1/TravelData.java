package zad1;

import java.io.*;
import java.text.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;

public class TravelData {
    private File dataDir;
    private Set<String> files;
    private List<String> offersDescriptionsList;

    public TravelData(File dataDir) {
        this.dataDir = dataDir;
        try {
            files = listFilesUsingFilesList(dataDir.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        offersDescriptionsList = new ArrayList<>();
    }

    private Set<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    public List<String> getOffersDescriptionsList(String desiredLocale, String dateFormat) {
        offersDescriptionsList.clear();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        for (String file : files) {
            File inputFile = new File(dataDir, file);
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\t");

                    // Parse input data
                    String sourceLocale = parts[0];
                    String country = parts[1];
                    Date startDate = sdf.parse(parts[2]);
                    Date endDate = sdf.parse(parts[3]);
                    String place = parts[4];
                    String price = parts[5];
                    String currency = parts[6];

                    // Create Locale objects
                    Locale inputLocale = createLocaleFromString(sourceLocale);
                    Locale outputLocale = createLocaleFromString(desiredLocale);

                    // Translate and format
                    String translatedCountry = translateCountry(inputLocale, country, outputLocale);
                    String translatedPlace = translatePlace(place, desiredLocale);
                    String formattedPrice = formatPrice(inputLocale, price, outputLocale);

                    // Format dates
                    String formattedStartDate = sdf.format(startDate);
                    String formattedEndDate = sdf.format(endDate);

                    // Create final string
                    String offer = String.format("%s %s %s %s %s %s",
                            translatedCountry,
                            formattedStartDate,
                            formattedEndDate,
                            translatedPlace,
                            formattedPrice,
                            currency
                    );

                    offersDescriptionsList.add(offer);
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return offersDescriptionsList;
    }

    private Locale createLocaleFromString(String localeStr) {
        String[] parts = localeStr.split("_");
        return parts.length > 1 ? new Locale(parts[0], parts[1]) : new Locale(parts[0]);
    }

    private String translateCountry(Locale inputLocale, String country, Locale outputLocale) {
        return Arrays.stream(Locale.getAvailableLocales())
                .filter(l -> l.getDisplayCountry(inputLocale).equals(country))
                .findFirst()
                .map(l -> l.getDisplayCountry(outputLocale))
                .orElse(country);
    }

    private String translatePlace(String place, String desiredLocale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("languages.CountryInfo",
                    createLocaleFromString(desiredLocale));

            for (String key : bundle.keySet()) {
                if (bundle.getString(key).equals(place)) {
                    return bundle.getString(key);
                }
            }

            if (bundle.containsKey(place)) {
                return bundle.getString(place);
            }
        } catch (MissingResourceException e) {
            System.err.println("Warning: Could not find translation for: " + place);
        }
        return place;
    }

    private String formatPrice(Locale inputLocale, String price, Locale outputLocale) {
        try {
            NumberFormat inputFormat = NumberFormat.getInstance(inputLocale);
            NumberFormat outputFormat = NumberFormat.getInstance(outputLocale);
            Number number = inputFormat.parse(price);
            return outputFormat.format(number.doubleValue());
        } catch (ParseException e) {
            return price;
        }
    }
}