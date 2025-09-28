package translation;

import javax.swing.*;
import java.awt.event.*;

public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Translator translator = new JSONTranslator("sample.json");
            CountryCodeConverter countryConv = new CountryCodeConverter("country-codes.txt");
            LanguageCodeConverter languageConv = new LanguageCodeConverter("language-codes.txt");

            JPanel countryPanel = new JPanel();
            String[] codes = translator.getCountryCodes().toArray(new String[0]);
            String[] countryNames = new String[codes.length];
            for (int i = 0; i < codes.length; i++) {
                countryNames[i] = countryConv.fromCountryCode(codes[i]);
            }
            JList countryList = new JList(countryNames);  // raw JList, no generics
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane countryScroll = new JScrollPane(countryList);
            countryPanel.add(new JLabel("Country:"));
            countryPanel.add(countryScroll);


            JPanel languagePanel = new JPanel();
            String[] langCodes = translator.getLanguageCodes().toArray(new String[0]);
            String[] langNames = new String[langCodes.length];
            for (int i = 0; i < langCodes.length; i++) {
                langNames[i] = languageConv.fromLanguageCode(langCodes[i]);
            }

            JComboBox languageCombo = new JComboBox(langNames);
            languagePanel.add(new JLabel("Language:"));
            languagePanel.add(languageCombo);

            JPanel resultPanel = new JPanel();
            JLabel resultLabel = new JLabel("Translation: ");
            resultPanel.add(resultLabel);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(resultPanel);
            mainPanel.add(countryPanel);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

            Runnable update = () -> {
                String languageName = (String) languageCombo.getSelectedItem();
                String countryName = (String) countryList.getSelectedValue();

                if (languageName == null || countryName == null) {
                    resultLabel.setText("Translation: ");
                    return;
                }

                String langCode = languageConv.fromLanguage(languageName);
                String countryCode = countryConv.fromCountry(countryName);

                String result = translator.translate(countryCode, langCode);
                if (result == null || result.isEmpty()) {
                    result = "no translation found!";
                }

                resultLabel.setText("Translation: " + result);
            };

            languageCombo.addActionListener(e -> update.run());
            countryList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) update.run();
            });
        });
    }
}