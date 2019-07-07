package model;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashMap;

public class DataModel {

    private String fileName, imageExtension, infos, directory;
    private int id;
    private String[] champs = new String[]{"", "1_Index_INTERNE", "2_Mission_PUBLIC", "3_Référence_Nasa_avec_Titre_INTERNE",
            "4_Référence_Nasa_INTERNE", "5_Date_de_prise_de_vue_/_Traitement_de_l’image_PUBLIC",
            "6_Date_de_traitement_de_l’image_PUBLIC", "7_Référence_Galaxy_PUBLIC", "8_Référence_Nasa_ou_FL/Galaxy_INTERNE",
            "9_Référence_FL_INTERNE", "10_Titre_PUBLIC", "11_Description_PUBLIC", "12_Wikipedia_Infos_about_...._PUBLIC",
            "13_Mots_clés_PUBLIC", "14_Taille_MB_PUBLIC", "15_Width_PUBLIC", "16_Height_PUBLIC", "17_Depth_PUBLIC",
            "18_Dpi_PUBLIC", "19_Format_PUBLIC", "20_Orientation_PUBLIC", "21_Focal_Length_PUBLIC", "22_Aperture_PUBLIC",
            "23_Exposure_PUBLIC", "24_Sensitivity_PUBLIC", "25_Manufacturer_PUBLIC", "26_Model_PUBLIC",
            "27_User_Comment_INTERNE", "28_Propriétaire_PUBLIC", "29_OPTION_1", "30_OPTION_2", "31_OPTION_3"};

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
    private ExifSubIFDDirectory exifDirectory;

    public String getInfos() {
        return infos;
    }

    public DataModel(int id, String directory, String fileName, String imageExtension) {
        this.fileName = fileName;
        this.infos = infos;
        this.directory = directory;
        this.id = id;
        this.imageExtension = imageExtension;
    }

    public void loadInfos(String directoryToImage) throws IOException {
        StringBuilder res = new StringBuilder();
        String line;
        String filePath = directoryToImage.replaceAll(imageExtension, "txt");
        File f = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(f));
        while ((line = br.readLine()) != null) {
            res.append("\n").append(line);
        }
        infos = res.toString();
    }


    public String getImageExtension() {
        return imageExtension;
    }

    public void parseData() {
        String[] splited = infos.split("\n");
        for (int i = 1; i < champs.length; i++)
            parsedData.put(champs[i], "");
        for (int i = 0; i < splited.length; i++)
            splited[i] = splited[i].trim();
        parsedData.put(champs[1], (id + 1) + "");
        parsedData.put(champs[4], getBigText(splited, "TITLE").replaceAll("\"", "\"\""));
        parsedData.put(champs[5], getTakenDate(splited).replaceAll("\"", "\"\""));
        parsedData.put(champs[9], getFL(splited).replaceAll("\"", "\"\""));
        parsedData.put(champs[10], parsedData.get(champs[4]));
        parsedData.put(champs[11], htmlStrip(getBigText(splited, "DESCRIPTION")).replaceAll("\"", "\"\""));
        parsedData.put(champs[13], getTags(splited).replaceAll("\"", "\"\""));
    }

    private void debugParsed() {
        for (int i = 1; i < champs.length; i++)
            if (!parsedData.get(champs[i]).equals(""))
                System.out.println(champs[i] + ": " + parsedData.get(champs[i]));
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

    public void loadMetaData() throws ImageProcessingException, IOException {
        File image = new File(directory);
        metadata = ImageMetadataReader.readMetadata(image);
        exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
    }

    private void loadExif() {
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                switch (tag.getTagType()) {
                    case ExifSubIFDDirectory.TAG_X_RESOLUTION:
                        if (parsedData.get(champs[18]).equals(""))
                            parsedData.put(champs[18], tag.getDescription().split(" ")[0]);
                        break;
                    case ExifSubIFDDirectory.TAG_FOCAL_LENGTH:
                        parsedData.put(champs[21], tag.getDescription());
                        break;
                    case ExifSubIFDDirectory.TAG_APERTURE:
                        parsedData.put(champs[22], tag.getDescription());
                        break;
                    case ExifSubIFDDirectory.TAG_EXPOSURE_TIME:
                        parsedData.put(champs[23], tag.getDescription());
                        break;
                    case ExifSubIFDDirectory.TAG_SENSITIVITY_TYPE:
                        parsedData.put(champs[24], tag.getDescription());
                        break;
                    case ExifSubIFDDirectory.TAG_MODEL:
                        parsedData.put(champs[26], tag.getDescription());
                        break;
                }
            }
        }
        for (String champ : champs)
            parsedData.putIfAbsent(champ, "");

    }


    public void parseImage() throws ImageProcessingException, IOException {
        File imageFile = new File(directory.replaceAll(".txt", imageExtension));
        BufferedImage brImage;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        try {
            brImage = ImageIO.read(imageFile);
            parsedData.put(champs[14], df.format(imageFile.length() / 1024.0 / 1024.0));
            parsedData.put(champs[15], brImage.getWidth() + "");
            parsedData.put(champs[16], brImage.getHeight() + "");

        } catch (IOException e) {
            System.out.println("Not found image at " + directory.replaceAll(".txt", imageExtension));
        }

        loadMetaData();
        loadExif();

//        debugParsed();
    }
}
