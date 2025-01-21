package languages;

import java.util.ListResourceBundle;

public class CountryInfo_pl_PL extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return contents;
    }

    private Object[][] contents = {

            // Countries
            {"Japan", "Japonia"},
            {"Italy", "Włochy"},
            {"United States", "Stany Zjednoczone Ameryki"},
            // Places
            {"lake", "jezioro"},
            {"sea", "morze"},
            {"mountains", "góry"},

            {"jezioro", "jezioro"},
            {"morze", "morze"},
            {"góry", "góry"},

            // Currencies
            {"PLN", "PLN"},
            {"USD", "USD"}
    };
}