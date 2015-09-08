package com.easypoli.contentAutomator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * View-Controller class responsible for handling all the UI creation and logic behind each interactive component
 */
public class ApplicationUI implements ActionListener{

    private JFrame rootFrame;
    /**
     * TextField for excel file path
     */
    private JTextField excelField;
    /**
     * AWT FileDialog to load files
     */
    private FileDialog fileDialog;
    /**
     * Panel where all the components lay
     */
    private JPanel mainPanel;
    /**
     * Arraylist used to store all the dynamically generated JPanel references that contain forms for adding PDF files
     * need to
     * be analysed
     */
    private ArrayList<JPanel> contentsPanelList;
    /**
     * Panel where all the PDF-forms JPanels lay
     */
    private JPanel contentPanel;
    /**
     * TextArea used to show generated HTML code after working on all the data
     */
    private JTextArea htmlTextArea;
    /**
     * ArrayList used to store all the data read from specified excel file. Each cell is an ArrayList of Strings
     */
    private ArrayList<ArrayList<String>> excelData = new ArrayList<>();

    /**
     * ApplicationUI Class constructor
     */
    public ApplicationUI() {

        /**
         * Header Panel creation. It's supposed to contain a welcome/how-to message and a form for loading excel file
         * from the HardDisk. After loading the file, its absolute path is written into a JTextField aside.
         */
        JPanel              headerPanel     = new JPanel();
        GridBagLayout       headerLayout    = new GridBagLayout();
        GridBagConstraints  gbc             = new GridBagConstraints();
        headerPanel.setLayout(headerLayout);

        //Header welcome/how-to text
        String              headerTxt       = "Load Excel file below, add related PDF files, press Generate and let " +
                                              "the magic begin";
        JLabel              headerLabel     = new JLabel(headerTxt);
        JLabel              excelLabel      = new JLabel("Excel File:");
        excelField                          = new JTextField();
        JButton             excelBtn        = new JButton("...");
        excelBtn.setName("excelBtn"); //Name property is set in order to be recognizable
                                      // by the ActionListener method invoked when pressed

        /**
         * Disposing elements in the GridBag Layout
         */
        //Header text
        gbc.fill        = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth   = 3;
        gbc.weightx     = 1;
        gbc.insets      = new Insets(10, 10, 10, 10);
        headerPanel.add(headerLabel, gbc);

        //Header ExcelLabel
        gbc.fill        = GridBagConstraints.RELATIVE;
        gbc.weightx     = 0;
        gbc.gridwidth   = 1;
        gbc.gridy       = 1;
        gbc.insets      = new Insets(0, 10, 10, 5);
        headerPanel.add(excelLabel, gbc);

        //Header ExcelField
        gbc.fill        = GridBagConstraints.HORIZONTAL;
        gbc.weightx     = 1;
        gbc.gridx       = 1;
        gbc.insets      = new Insets(0, 0, 10, 0);
        headerPanel.add(excelField, gbc);

        //Header ExcelBtn
        gbc.fill        = GridBagConstraints.RELATIVE;
        gbc.weightx     = 0;
        gbc.gridx       = 2;
        gbc.insets      = new Insets(0, 0, 10, 0);
        headerPanel.add(excelBtn, gbc);
        excelBtn.addActionListener(this); //Register this class as ActionListener
        fileDialog      = new FileDialog(rootFrame, "Load a file", FileDialog.LOAD); // Instantiating AWT File dialog
                                                                                     // in loading mode and single
                                                                                     // selection
        fileDialog.setMultipleMode(false);

        /**
         * Content Panel creation. It's supposed to contain all the PDF-loading-forms JPanels created dynamically
         * after knowing how many contents will be added. At this part of creation we just wanna add a simple
         * notification to the user that this is the place where he will be able to see those forms.
         */
        contentPanel                            = new JPanel();
        JLabel              noExcelLabel        = new JLabel("No Excel file added yet", SwingConstants.CENTER);
        GridBagLayout       contentGridBag      = new GridBagLayout();
        GridBagConstraints  contentGridConst    = new GridBagConstraints();

        /**
         * Disposing notification text in the center of the available space
         */
        contentGridConst.fill       = GridBagConstraints.HORIZONTAL;
        contentGridConst.weightx    = 1;
        contentPanel.setLayout(contentGridBag);
        contentPanel.add(noExcelLabel, contentGridConst);
        // We wanna wrap the contentPanel in a ScrollPane in case (an it will) contents JPanel are too much for the
        // window height
        JScrollPane contentScrollPanel = new JScrollPane(contentPanel);
        contentScrollPanel.setPreferredSize(new Dimension(1024, 250)); //Make it wide enough to contain file path string
        contentsPanelList = new ArrayList<>(); //Instantiating contentsPanelList

        /**
         * Disposing "Generate" button.
         */
        JPanel              generatePanel       = new JPanel();
        JButton             generateBtn         = new JButton("Generate");
        GridBagLayout       generateGridBag     = new GridBagLayout();
        GridBagConstraints  generateGridConst   = new GridBagConstraints();
        generateBtn.setName("generateBtn"); // Name property is set in order to be recognizable
                                            // by the ActionListener method invoked when pressed
        generateBtn.addActionListener(this);
        generateGridConst.fill      = GridBagConstraints.HORIZONTAL;
        generateGridConst.weightx   = 1;
        generatePanel.setLayout(generateGridBag);
        generatePanel.add(generateBtn, generateGridConst); // We wanna use a GridBagLayout to have the button to extend
                                                           // its width to match window width. Any other solution?

        /**
         * Disposing JTextArea. It's supposed to show to user the HTML Code generated at Generate-Button pressing
         */
        htmlTextArea                = new JTextArea();
        JScrollPane     htmlPane    = new JScrollPane(htmlTextArea);
        htmlPane.setPreferredSize(new Dimension(1024, 250)); //Make it wide enough to contain good portion of code
        htmlPane.setBorder(BorderFactory.createCompoundBorder( //Code courtesy of Oracle's Java Examples
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Ready-to-paste HTML Code"),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ),
                htmlPane.getBorder()
        ));

        /**
         * Adding elements to the MainPanel. It's been decided to use a BoxLayout top-to-bottom layout, mostly for
         * studying reasons.
         */
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(headerPanel);
        mainPanel.add(contentScrollPanel);
        mainPanel.add(generatePanel);
        mainPanel.add(htmlPane);

        /*
         * Setting up main JFrame and disposing it to the kind user
         */
        rootFrame = new JFrame("Content Automator");
        rootFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        rootFrame.add(mainPanel);
        rootFrame.pack();
        rootFrame.setVisible(true);

    }

    /**
     * Overriding actionPerformed method after implementing {@link ActionListener ActionListener} interface. It's the
     * headquarter of all events generated by pressing a button. The event source is caught and according to its
     * name, a specific private method is invoked.
     * @param e Action event generated
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (((JButton) e.getSource()).getName()) {
            case "excelBtn":
                LoadExcelFile();
                break;
            case "addRowBtn":
                AddRow((JButton) e.getSource());
                break;
            case "generateBtn":
                GenerateHTML();
                break;
            case "pdfBtn":
                AddPdf((JButton) e.getSource());
                break;
            default:
                break;
        }
    }

    /**
     * This method is invoked after the "Load-Excel-File" button has been fired. This is really important as it's
     * responsible of:
     * - Loading all the data from the excel file once forever
     * - Creating and disposing every PDF-loading-form JPanel based on how many contents we got from excel file. Cool
     * right?
     */
    private void LoadExcelFile() {

        fileDialog.setVisible(true); //Dispose AWT fileDialog and let the user choose excel file
        if (fileDialog.getFiles().length != 0) { //Check if the user selected something
            File excelFile = fileDialog.getFiles()[0]; //Get selected file reference
            excelField.setText(excelFile.getAbsolutePath()); //update header "excel-file-path" text field

            try (FileInputStream excelInputStream = new FileInputStream(excelFile)) { // Try-with-resources block
                                                                                      // Open a stream from excel file

                XSSFWorkbook    argomentiBook   = new XSSFWorkbook(excelInputStream);
                XSSFSheet       argomentiSheet  = argomentiBook.getSheetAt(0); // Load first sheet from the excel file
                                                                               // where contents data should be

                Iterator<Row>   rowIterator     = argomentiSheet.iterator(); // Excel rows iterator

                int             numberOfItems   = 0; // "I'll be counting rows" - No really, we need to know how many
                                                     // JPanels we gotta create
                excelData.clear(); // IMPORTANT: In case we loaded another excel file previously
                while (rowIterator.hasNext()) {
                    Iterator<Cell> cellIterator = rowIterator.next().cellIterator(); // For each row, iterate on cells
                    excelData.add(new ArrayList<>()); // Instantiating the ArrayList<String> for current content
                    while (cellIterator.hasNext()) {
                        // Retrieving cells data and save them
                        excelData.get(numberOfItems).add(cellIterator.next().getStringCellValue());
                    }
                    numberOfItems++; // Update the counter
                }

                //Get rid of notification message in contentPanel. Now we have something to show.
                contentPanel.removeAll();
                //Update the layout to a BoxLayout top-bottom
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

                /**
                 * Now we wanna create and dispose a JPanel for each content we're going to generate HTML code for.
                 * We took notes of how many they are for a for-cycle will do that for us
                 */
                for (int i = 0; i < numberOfItems; i++) {
                    JPanel              tmpPanel    = new JPanel(new GridBagLayout());
                    GridBagConstraints  gbc         = new GridBagConstraints();
                    JTextField          tmpTxtField = new JTextField();
                    JButton             pdfBtn      = new JButton("...");
                    JButton             addRowBtn   = new JButton("+");
                    JLabel              contentText = new JLabel("Add PDF file(s) of: " + excelData.get(i).get(0));
                    pdfBtn.setName("pdfBtn"); // Name property is set in order to be recognizable
                                              // by the ActionListener method invoked when pressed
                    pdfBtn.addActionListener(this);
                    addRowBtn.setName("addRowBtn");
                    addRowBtn.addActionListener(this);
                    // How-to message
                    gbc.fill        = GridBagConstraints.HORIZONTAL;
                    gbc.weightx     = 1;
                    tmpPanel.add(contentText, gbc);
                    // PDF file path text field
                    gbc.gridy       = 1;
                    tmpPanel.add(tmpTxtField, gbc);
                    // Load-PDF-file button
                    gbc.fill        = GridBagConstraints.RELATIVE;
                    gbc.weightx     = 0;
                    gbc.gridx       = 1;
                    tmpPanel.add(pdfBtn, gbc);
                    // "Add a new pdf loading form" button
                    gbc.gridwidth   = 2;
                    gbc.gridy       = 2;
                    gbc.anchor      = GridBagConstraints.EAST;
                    tmpPanel.add(addRowBtn, gbc);
                    // Set border for the JPanel
                    tmpPanel.setBorder(BorderFactory.createCompoundBorder( //Code courtesy of Oracle's Java Examples
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createTitledBorder(excelData.get(i).get(0)),
                                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                            ),
                            tmpPanel.getBorder()
                    ));

                    contentsPanelList.add(tmpPanel); // Add the JPanel reference to our PDF-panels list
                    contentPanel.add(tmpPanel); // Add the JPanel to Contents Panel

                    mainPanel.revalidate(); //Refresh mainPanel to show new components just added
                    mainPanel.repaint();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is invoked after "Add a new PDF-loading-form" button is fired. As this can happen from any content
     * JPanel and after it has already been pressed an unknown number of times in the same JPanel we need to find who
     * call this and which GridBagLayout row to place it in
     *
     * @param jButton "Add a new PDF-loading-form" button reference
     */
    private void AddRow(JButton jButton) {
        JPanel              contentPanel    = (JPanel) jButton.getParent();
        int                 nextRow         = HowManyTextFields(contentPanel);
        GridBagConstraints  gbc             = new GridBagConstraints();
        JTextField          tmpTxtField     = new JTextField();
        JButton             pdfBtn          = new JButton("...");
        pdfBtn.setName("pdfBtn"); // Name property is set in order to be recognizable
                                  // by the ActionListener method invoked when pressed
        pdfBtn.addActionListener(this);

        contentPanel.remove(jButton); // Remove "Add a new PDF-loading-form" button. It will be added back after the
                                      // new load field has been added

        // PDF-file path text field
        gbc.fill        = GridBagConstraints.HORIZONTAL;
        gbc.weightx     = 1;
        gbc.gridx       = 0;
        gbc.gridy       = nextRow+1; // First JPanel row is dedicated to how-text so we need a +1
        contentPanel.add(tmpTxtField, gbc);
        // Load pdf-file button
        gbc.fill = GridBagConstraints.RELATIVE;
        gbc.weightx     = 0;
        gbc.gridx       = 1;
        contentPanel.add(pdfBtn, gbc);
        // "Add a new PDF-loading-form" button
        gbc.anchor      = GridBagConstraints.EAST;
        gbc.gridy       = nextRow+2; // Same as above ;)
        gbc.gridwidth   = 2;
        contentPanel.add(jButton, gbc);

        contentPanel.getParent().revalidate(); // Refresh content panel to show new components just added
        contentPanel.getParent().repaint();
    }

    /**
     * Utility method to calculate the number of text fields in a JPanel. It's used to know which JPanel GridBagLayout
     * row has to be assigned to new loading form.
     *
     * @param jPanel PDF-loading-form JPanel
     * @return number of JTextFields objects in the JPanel parameter
     */
    private int HowManyTextFields(JPanel jPanel) {
        int n                   = 0; // Counter
        int numberOfComponents  = jPanel.getComponents().length; // Number of components in the JPanel.
        for (int i = 0; i < numberOfComponents; i++) {
            if (jPanel.getComponents()[i] instanceof JTextField) { // Not all of them are JTextFields, ofc.
                n++; // Update the counter
            }
        } return n;
    }

    /**
     * This method is invoked when the loading PDF button is fired. What we want is to let the user choose a PDF file
     * and then write its absolute path to the JTextField right near to it. As already seen in the addRow method we
     * can't predict which button will invoke this method and we have to find the matching JTextField too.
     *
     * @param jButton "Load PDF" button reference
     */
    private void AddPdf(JButton jButton) {
        JPanel      jPanel                      = (JPanel) jButton.getParent(); // Getting a reference to the
                                                                                // container of the fired button
        int         whichBtnAmI                 = WhichBntAmI(jButton); // We wanna know the order number of the fired
                                                                        // button so we know the order number
                                                                        // of the matching JTextField too
        int         numberOfCurrentTextField    = 0; // Text fields counter
        Component   components[]                = jPanel.getComponents(); // Getting components in the JPanel
        fileDialog.setVisible(true); // Show the fileDialog to let the user choose a PDF file
        if (fileDialog.getFiles().length != 0) { // Check if the user selected something
            for (Component c : components) { // Cycle through components
                if (c instanceof JTextField) { // and consider only those who are JTextFields
                    numberOfCurrentTextField++; // Update the counter
                    if (numberOfCurrentTextField == whichBtnAmI) { // If the order number of this text field is the same
                                                                   // of the fired button one we found the right one
                        ((JTextField) c).setText(fileDialog.getFiles()[0].getAbsolutePath()); // Set it to pdf file path
                        break; // Nothing more to do
                    }
                }
            }
        }
    }

    /**
     * Utility method to calculate the order number of jButton parameter inside its container
     *
     * @param jButton JButton reference
     * @return order number of jButton in its container. -1 if no button has been found
     */
    private int WhichBntAmI(JButton jButton) {
        JPanel      jPanel              = (JPanel) jButton.getParent(); // Getting a reference to container of jButton
        Component   components[]        = jPanel.getComponents(); // Getting components in jPanel
        int         currentBtnNumber    = 0; // Current button order number
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JButton) { // Not all components are JButtons
                currentBtnNumber++; // Update counter
                if (components[i] == jButton) { // If current component is the one we're looking for...
                    return currentBtnNumber; // ... return the current button order number
                }
            }
        } return -1; // Return -1 if jButton parameter could not be found (unlikely, but who knows)
    }

    /**
     * This method is called when "Generate" button is fired. It will generate the (potentially) super-long HTML
     * String that we wanted to automatize its creation through this software. It's extremely specific for our usage
     * but can be easily customized by changing data and html tags.
     */
    private void GenerateHTML() {

        StringBuilder htmlCode = new StringBuilder(); // Our slave. Good boy.

        for (int i = 0; i < contentsPanelList.size(); i++) { // Cycle through all the content JPanels and generate a
                                                             // HTML block for each. Then concatenate them together

            //As said before most of these lines are extremely specific for our needs
            int             pagesNumber = 0; // PDF number of pages
            long            pdfSize     = 0; // PDF file size

            Component       component[] = contentsPanelList.get(i).getComponents(); // Getting current JPanel components
            ArrayList<File> files       = new ArrayList<>(); // ArrayList to store PDF file references
            for (int k = 0; k < component.length; k++ ) {
                if (component[k] instanceof JTextField) { // Retrieve pdf files paths from JTextFields
                    File tmpPdfFile = new File(((JTextField) component[k]).getText()); // Create a temporary file
                    files.add(tmpPdfFile); // Save its reference in our ArrayList
                    try (PDDocument tmpPDF = PDDocument.load(tmpPdfFile)){ // Open a PDDocument from temporary file
                        pagesNumber = pagesNumber + tmpPDF.getNumberOfPages(); // Get the number of pages
                        pdfSize     = tmpPdfFile.length()/1048576L; // Get its size in MegaBytes
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Now we need a few infos to create HTML code according to the pdf type and for the download link
            File    demoFile                    = files.get(0);
            File    demoFIleParentFile          = demoFile.getParentFile();
            String  demoFileParentString        = demoFile.getParent();
            String  demoFileParentParentString  = demoFIleParentFile.getParent();
            String  typeAndSubject              = demoFileParentString.replace(demoFileParentParentString + "/", "");

            // Here's the result of above string manipulations
            String  type                        = typeAndSubject.substring(0, typeAndSubject.indexOf("_"));
            String  subject                     = typeAndSubject.substring(typeAndSubject.indexOf("_") + 1,
                                                                           typeAndSubject.length());

            // Again specific lines of code
            htmlCode.append(HTMLConstants.LI_OPEN)
                    .append("\n");

            // Customize panel color according to PDF type
            if (type.equals("teo")) {
                htmlCode.append("  ")
                        .append(HTMLConstants.DIV_PANEL_TEO)
                        .append("\n");
            } else if (type.equals("es")) {
                htmlCode.append("  ")
                        .append(HTMLConstants.DIV_PANEL_ES)
                        .append("\n");
            } else {
                htmlCode.append("  ")
                        .append(HTMLConstants.DIV_PANEL_SCHEMI)
                        .append("\n");
            }

            htmlCode.append("    ")
                    .append(HTMLConstants.DIV_HEADING)
                    .append("\n")
                    .append("      ")
                    .append(HTMLConstants.H3_TITLE)
                    .append(excelData.get(i).get(0))
                    .append(HTMLConstants.H3_CLOSE).append("\n")
                    .append("    ")
                    .append(HTMLConstants.DIV_CLOSE)
                    .append("\n")
                    .append("    ")
                    .append(HTMLConstants.DIV_BODY)
                    .append("\n")
                    .append("      ")
                    .append(HTMLConstants.ARTICLE_DESC)
                    .append(excelData.get(i).get(1))
                    .append(HTMLConstants.ARTICLE_CLOSE).append("\n")
                    .append("      ")
                    .append(HTMLConstants.HR)
                    .append("\n")
                    .append("      ")
                    .append(HTMLConstants.ARTICLE_OPEN)
                    .append(HTMLConstants.STRONG)
                    .append("Tipologia")
                    .append(HTMLConstants.STRONG_CLOSE)
                    .append(": ")
                    .append(HTMLConstants.SPAN_TYPE);
            if (type.equals("teo")) {
                htmlCode.append("Teoria")
                        .append(HTMLConstants.SPAN_CLOSE)
                        .append(HTMLConstants.ARTICLE_CLOSE)
                        .append("\n");
            } else if (type.equals("es")) {
                htmlCode.append("Esercizi")
                        .append(HTMLConstants.SPAN_CLOSE)
                        .append(HTMLConstants.ARTICLE_CLOSE)
                        .append("\n");
            } else {
                htmlCode.append("Schemi")
                        .append(HTMLConstants.SPAN_CLOSE)
                        .append(HTMLConstants.ARTICLE_CLOSE)
                        .append("\n");
            }
            htmlCode.append("      ")
                    .append(HTMLConstants.ARTICLE_OPEN)
                    .append(HTMLConstants.STRONG)
                    .append("Tag contenuti")
                    .append(HTMLConstants.STRONG_CLOSE)
                    .append(": ");

            // According to our conventions tags in the excel file start from column #3 onwards
            ListIterator<String> listIterator = excelData.get(i).listIterator(2); // Get iterator from position 3
            int tagNumber = 1;
            while (listIterator.hasNext()) {
                if (tagNumber != 1) {
                    htmlCode.append(" ,");
                }
                htmlCode.append(HTMLConstants.SPAN_TAG)
                        .append(tagNumber)
                        .append("\">")
                        .append(listIterator.next())
                        .append(HTMLConstants.SPAN_CLOSE);
                tagNumber++;
            }

            htmlCode.append(HTMLConstants.ARTICLE_CLOSE)
                    .append("\n")
                    .append("      ")
                    .append(HTMLConstants.ARTICLE_OPEN)
                    .append(HTMLConstants.STRONG)
                    .append("Pagine")
                    .append(HTMLConstants.STRONG_CLOSE)
                    .append(": ")
                    .append(pagesNumber)
                    .append(HTMLConstants.ARTICLE_CLOSE)
                    .append("\n")
                    .append("      ")
                    .append(HTMLConstants.HR)
                    .append("\n");

            // Customize download section according to single or multiple PDF files availability and build correct link
            if (HowManyTextFields(contentsPanelList.get(i)) == 1) {
                htmlCode.append("        ")
                        .append(HTMLConstants.SINGLE_LINK)
                        .append(subject)
                        .append("/").append(typeAndSubject)
                        .append("/").append(files.get(0).getName())
                        .append("\">Scarica ~ ")
                        .append(pdfSize)
                        .append(" MB")
                        .append(HTMLConstants.LINK_CLOSE)
                        .append("\n");
            } else {
                htmlCode.append("      ")
                        .append(HTMLConstants.NAV_PILLS)
                        .append("\n");
                for (int k = 0; k < files.size(); k++) {
                    htmlCode.append("        ")
                            .append(HTMLConstants.LI_OPEN)
                            .append(HTMLConstants.MULTIPLE_LINK)
                            .append(subject)
                            .append("/").append(typeAndSubject)
                            .append("/")
                            .append(files.get(k).getName())
                            .append("\">PDF ")
                            .append(k + 1)
                            .append(HTMLConstants.LINK_CLOSE)
                            .append(HTMLConstants.LI_CLOSE)
                            .append("\n");
                } htmlCode.append("      ")
                        .append(HTMLConstants.NAV_PILLS_CLOSE)
                        .append("\n");
            }
            htmlCode.append("    ")
                    .append(HTMLConstants.DIV_CLOSE)
                    .append("\n")
                    .append("  ")
                    .append(HTMLConstants.DIV_CLOSE)
                    .append("\n")
                    .append(HTMLConstants.LI_CLOSE)
                    .append("\n");
        } // Start over

        htmlTextArea.setText(htmlCode.toString()); // Show the HTML code built and cry of happiness for not having
                                                   // written it on your own wasting hours
    }
}
