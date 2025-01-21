package languages;

import java.util.ListResourceBundle;

public class CountryInfo_en_GB extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return contents;
    }

    private Object[][] contents = {
            // Countries
            {"Japan", "Japan"},
            {"Italy", "Italy"},
            {"United States", "United States"},
            // Places
            {"lake", "lake"},
            {"sea", "sea"},
            {"mountains", "mountains"},

            {"jezioro", "lake"},
            {"morze", "sea"},
            {"góry", "mountains"},

            // Currencies
            {"PLN", "PLN"},
            {"USD", "USD"}
    };
}