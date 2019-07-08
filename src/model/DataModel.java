package model;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

public class DataModel {


    private File imageFile;
    private String fileName, imageExtension, infos, directory;
    private int id;
    private String[] champs = new String[]{"", "1_Index_INTERNE", "2_Mission_PUBLIC", "3_Référence_Nasa_avec_Titre_INTERNE",
            "4_Référence_Nasa_INTERNE", "5_Date_de_prise_de_vue_/_Traitement_de_l’image_PUBLIC",
            "6_Date_de_traitement_de_l’image_PUBLIC", "7_Référence_Galaxy_PUBLIC", "8_Référence_Nasa_ou_FL/Galaxy_INTERNE",
            "9_Référence_FL_INTERNE", "10_Titre_PUBLIC", "11_Description_PUBLIC", "12_Wikipedia_Infos_about_...._PUBLIC",
            "13_Mots_clés_PUBLIC", "14_Taille_MB_PUBLIC", "15_Width_PUBLIC", "16_Height_PUBLIC", "17_Depth_PUBLIC",
            "18_Dpi_PUBLIC", "19_Format_PUBLIC", "20_Orientation_PUBLIC", "21_Focal_Length_PUBLIC", "22_Aperture_PUBLIC",
            "23_Exposure_PUBLIC", "24_Sensitivity_PUBLIC", "25_Manufacturer_PUBLIC", "26_Model_PUBLIC",
            "27_User_Comment_INTERNE", "28_Mode_Flash_PUBLIC", "29_Aperture_Maxi_PUBLIC", "30_Propriétaire_PUBLIC", "31_OPTION_3"};

    private String[] deleteList = new String[]{"<strong>", "</strong>", "&quot;", "<b>", "</b>", "<br>", "<i>",};

    private HashMap<String, String> parsedData = new HashMap<>();

    public HashMap<String, String> getParsedData() {
        return parsedData;
    }

    public String getDirectory() {
        return directory;
    }

    public String getFileName() {
        return fileName;
    }

    private Metadata metadata;

    public String getInfos() {
        return infos;
    }

    public DataModel(int id, String directory, String fileName, String imageExtension) {
        this.fileName = fileName;
        this.directory = directory;
        this.id = id;
        this.imageExtension = imageExtension;
    }

    public void loadInfos(String directoryToImage) throws IOException {
        StringBuilder res = new StringBuilder();
        String line;
        String filePath = directoryToImage.replace("." + imageExtension, ".txt");
        System.out.println(filePath);
        File f = new File(filePath);
        System.out.println(f.isFile());
        if (!f.isFile())
            return;
        BufferedReader br = new BufferedReader(new FileReader(f));
        while ((line = br.readLine()) != null) {
            res.append("\n").append(line);
        }
        infos = res.toString();
//        System.out.println(infos + "\n");
    }

    public void parseData() {
        parsedData.put(champs[1], (id + 1) + "");
        if (infos == null)
            return;
        String[] splited = infos.split("\n");
        for (int i = 0; i < splited.length; i++)
            splited[i] = splited[i].trim();
        parsedData.put(champs[4], getBigText(splited, "TITLE"));
        parsedData.put(champs[5], getTakenDate(splited));
        parsedData.put(champs[9], getFL(splited));
        parsedData.put(champs[10], parsedData.get(champs[4]));
        parsedData.put(champs[11], htmlStrip(getBigText(splited, "DESCRIPTION")));
        parsedData.put(champs[13], getTags(splited));
    }

    private String getBigText(String[] splited, String group) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < splited.length; i++) {
            if (splited[i].contains(group)) {
                i += 2;
                while (splited[i].trim().equals(""))
                    i++;
                while ((i < splited.length) && (!splited[i].startsWith("+-"))) {
                    if (!splited[i].trim().equals(""))
                        res.append(splited[i].trim()).append(" ");
                    i++;
                }
                break;
            }
        }
        return res.toString();
    }

    private String getTakenDate(String[] splited) {
        for (String s : splited) {
            if (s.startsWith("Taken Date"))
                return s.substring(s.indexOf(":") + 1).trim();
        }
        return "";
    }

    private String getFL(String[] splited) {
        for (String s : splited) {
            if (s.startsWith("Photo URL")) {
                String[] temp = s.split("/");
                return temp[temp.length - 1];
            }
        }
        return "";
    }

    private String getTags(String[] splited) {
        StringBuilder tags = new StringBuilder(getBigText(splited, "TAGS"));
        String[] tagsList = tags.toString().split("\"");
        tags = new StringBuilder();
        for (String t : tagsList) {
            t = t.trim();
            if (!t.equals(""))
                tags.append(t).append(", ");
        }
        if (tags.charAt(tags.length() - 1) == '\"')
            return tags.substring(0, tags.length() - 3);
        else
            return tags.substring(0, tags.length() - 2);
    }

    private String delete(String s, String target) {
        int pos = s.indexOf(target);
        while (pos >= 0) {
            s = s.substring(0, pos) + s.substring(pos + target.length());
            pos = s.indexOf(target);
        }
        return s;
    }

    private String deleteHashtag(String s) {
        int pos = s.indexOf("#");
        while (pos >= 0) {
            int posEnd = pos + 1;
            while ((posEnd < s.length() - 1) && (s.charAt(posEnd + 1) != ' '))
                posEnd++;
            s = s.substring(0, pos) + s.substring(posEnd + 1);
            pos = s.indexOf("#");
        }
        return s;
    }

    private String deleteSentence(String s, String targetBegin, String targetEnd) {
        int posBegin = s.indexOf(targetBegin);
        while (posBegin >= 0) {
            int posEnd = s.indexOf(targetEnd, posBegin + 1);
            while ((posBegin > 0) && (s.charAt(posBegin - 1) != '.'))
                posBegin--;
            s = s.substring(0, posBegin) + s.substring(posEnd + targetEnd.length());
            posBegin = s.indexOf(targetBegin);
        }
        return s;
    }

    private String htmlStrip(String s) {
//        System.out.println("Striping: " + s);

        s = deleteHashtag(s);
        s = deleteSentence(s, "<a", "a>");

        for (String target : deleteList)
            s = delete(s, target);

//        System.out.println("Result  : " + s);

        return s;
    }

    private void loadMetaData() throws ImageProcessingException, IOException {
        metadata = ImageMetadataReader.readMetadata(imageFile);
    }

    private void loadExif() {

        int numChannel = 0, bitDepth = 0;
        String dateCreated = "";

        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                switch (tag.getTagName().trim()) {
                    case "File Modified Date":
                        parsedData.put(champs[6], tag.getDescription());
                        break;
                    case "Image Description":
                        parsedData.putIfAbsent(champs[11], tag.getDescription());
                        break;
                    case "Detected File Type Name":
                        parsedData.put(champs[19], tag.getDescription());
                        break;
                    case "Focal Length":
                        parsedData.put(champs[21], tag.getDescription());
                        break;
                    case "Aperture Value":
                        parsedData.put(champs[22], tag.getDescription());
                        break;
                    case "Exposure Time":
                        parsedData.put(champs[23], tag.getDescription());
                        break;
                    case "ISO Speed Ratings": {
                        String[] ISO = tag.getDescription().split(" ");
                        parsedData.put(champs[24], ISO[ISO.length - 1]);
                    }
                    break;
                    case "Make":
                        parsedData.put(champs[25], tag.getDescription());
                        break;
                    case "Model":
                        parsedData.put(champs[26], tag.getDescription());
                        break;
                    case "User Comment":
                        parsedData.put(champs[27], tag.getDescription());
                        break;
                    case "Flash":
                        parsedData.put(champs[28], tag.getDescription());
                        break;
                    case "Max Aperture Value":
                        parsedData.put(champs[29], tag.getDescription());
                        break;
                    case "Date/Time Original":
                    case "Date/Time":
                        parsedData.put(champs[5], tag.getDescription());
                        break;
                    case "Number of Components":
                        numChannel = Integer.parseInt(tag.getDescription().split(" ")[0]);
                        break;
                    case "Data Precision":
                        bitDepth = Integer.parseInt(tag.getDescription().split(" ")[0]);
                        break;
                    case "Keywords":
                        parsedData.putIfAbsent(champs[13], tag.getDescription());
                        break;
                    case "Resolution Info": {
                        if (parsedData.get(champs[18]) != null) {
                            int currentDPI = Integer.parseInt(parsedData.get(champs[18]));
                            int newDPI = Integer.parseInt(tag.getDescription().split("x")[0]);
                            parsedData.put(champs[18], Math.max(currentDPI, newDPI) + "");
                        } else
                            parsedData.put(champs[18], tag.getDescription().split("x")[0]);
                    }
                    break;

                }
            }
        }
        parsedData.putIfAbsent(champs[18], "300");
        if (numChannel == 0)
            parsedData.putIfAbsent(champs[17], "");
        parsedData.putIfAbsent(champs[17], (numChannel * bitDepth) + "");
        parsedData.putIfAbsent(champs[5], dateCreated);

    }


    public void parseImage() throws ImageProcessingException, IOException {
        imageFile = new File(directory);
        BufferedImage brImage;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        try {
            brImage = ImageIO.read(imageFile);
            parsedData.put(champs[14], df.format(imageFile.length() / 1024.0 / 1024.0));
            parsedData.put(champs[15], brImage.getWidth() + "");
            parsedData.put(champs[16], brImage.getHeight() + "");

            if (brImage.getWidth() < brImage.getHeight())
                parsedData.put(champs[20], "VERTICAL");
            else if (brImage.getWidth() > brImage.getHeight())
                parsedData.put(champs[20], "HORIZONTAL");
            else
                parsedData.put(champs[20], "QUADRATIC");
        } catch (IOException e) {
            System.out.println("Not found image at " + directory.replaceAll(".txt", imageExtension));
        }

        loadMetaData();
        loadExif();
        parsedData.putIfAbsent(champs[19], imageExtension.toUpperCase());
//        debugParsed();
    }
}
