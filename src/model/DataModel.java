package model;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.jpeg.JpegDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class DataModel {


    private String fileName, imageExtension, infos, directory;

    public int getID() {
        return id;
    }

    private int id;
    private String[] champs = new String[]{"", "1_Index_INTERNE", "2_Mission_PUBLIC", "3_Référence_Nasa_avec_Titre_INTERNE",
            "4_Référence_Nasa_INTERNE", "5_Titre_PUBLIC", "6_Date_de_prise_de_vue_/_Traitement_de_l’image_PUBLIC",
            "7_Date_de_traitement_de_l’image_PUBLIC", "8_Description_PUBLIC",
            "9_Référence_Galaxy_PUBLIC", "10_Référence_Nasa_ou_FL/Galaxy_INTERNE", "11_Référence_FL_INTERNE", "12_Credit_PUBLIC",
            "13_Wikipedia_Infos_about_...._PUBLIC", "14_Mots_clés_PUBLIC", "15_Taille_MB_PUBLIC", "16_Width_PUBLIC", "17_Height_PUBLIC",
            "18_Depth_PUBLIC", "19_Dpi_PUBLIC", "20_Format_PUBLIC", "21_Orientation_PUBLIC", "22_Focal_Length_PUBLIC",
            "23_Aperture_PUBLIC", "24_Aperture maxi_PUBLIC", "25_Exposure_PUBLIC", "26_Sensitivity_PUBLIC",
            "27_Mode Flash_PUBLIC", "28_Manufacturer_PUBLIC", "29_Model_PUBLIC", "30_User_Comment_INTERNE", "31_Propriétaire_PUBLIC"};

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
        File f = new File(filePath);
        if (!f.isFile())
            return;
        BufferedReader br = new BufferedReader(new FileReader(f));
        while ((line = br.readLine()) != null) {
            res.append("\n").append(line);
        }
        infos = res.toString();
    }

    public void parseData() {
        parsedData.putIfAbsent(champs[1], (id + 1) + "");
        if (infos == null)
            return;
        String[] splited = infos.split("\n");
        for (int i = 0; i < splited.length; i++)
            splited[i] = splited[i].trim();
        parsedData.putIfAbsent(champs[5], htmlStrip(getBigText(splited, "TITLE")));
        parsedData.putIfAbsent(champs[6], getTakenDate(splited));
        parsedData.putIfAbsent(champs[8], htmlStrip(getBigText(splited, "DESCRIPTION")));
        parsedData.putIfAbsent(champs[12], htmlStrip(getCredit(splited)));
        parsedData.putIfAbsent(champs[11], getFL(splited));
        parsedData.putIfAbsent(champs[14], getTags(splited));
    }

    private String getCredit(String[] splited) {
        for (String s : splited) {
            if (s.contains("*Credit:*"))
                return s.substring(s.indexOf("*Credit:*") + 9).trim();
        }
        return null;
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
        return null;
    }

    private String getFL(String[] splited) {
        for (String s : splited) {
            if (s.startsWith("Photo URL")) {
                String[] temp = s.split("/");
                return temp[temp.length - 1];
            }
        }
        return null;
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
        if (s == null)
            return null;
        int pos = s.indexOf(target);
        while (pos >= 0) {
            s = s.substring(0, pos) + s.substring(pos + target.length());
            pos = s.indexOf(target);
        }
        return s;
    }

    private String deleteHashtag(String s) {
        if (s == null)
            return null;
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
        if (s == null)
            return null;
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

        s = deleteHashtag(s);
        s = deleteSentence(s, "<a", "a>");

        for (String target : deleteList)
            s = delete(s, target);

//        System.out.println("Result  : " + s);

        return s;
    }

    private void loadMetaData() throws ImageProcessingException, IOException {
        File imageFile = new File(directory);
        metadata = ImageMetadataReader.readMetadata(imageFile);
    }


    private void loadExif() throws MetadataException {
        Directory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
        Directory fileDirectory = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
        Directory fileTypeDirectory = metadata.getFirstDirectoryOfType(FileTypeDirectory.class);
        Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        Directory exifSubIFDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        Directory iptcDirectory = metadata.getFirstDirectoryOfType(IptcDirectory.class);

        int numChannel = jpegDirectory.getInt(JpegDirectory.TAG_NUMBER_OF_COMPONENTS);
        int bitDepth = jpegDirectory.getInt(JpegDirectory.TAG_DATA_PRECISION);

        if (exifSubIFDirectory != null) {
            System.out.println(fileName + " " + exifSubIFDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
            parsedData.putIfAbsent(champs[6], exifSubIFDirectory.getDescription(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
            parsedData.putIfAbsent(champs[22], exifSubIFDirectory.getDescription(ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
            parsedData.putIfAbsent(champs[23], exifSubIFDirectory.getDescription(ExifSubIFDDirectory.TAG_APERTURE));
            parsedData.putIfAbsent(champs[24], exifSubIFDirectory.getDescription(ExifSubIFDDirectory.TAG_MAX_APERTURE));
            parsedData.putIfAbsent(champs[25], exifSubIFDirectory.getDescription(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
            if (exifSubIFDirectory.containsTag(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT))
                parsedData.putIfAbsent(champs[26], exifSubIFDirectory.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT) + "");
            parsedData.putIfAbsent(champs[27], exifSubIFDirectory.getDescription(ExifSubIFDDirectory.TAG_FLASH));
            parsedData.putIfAbsent(champs[30], exifSubIFDirectory.getDescription(ExifSubIFDDirectory.TAG_USER_COMMENT));

        }

        if (fileDirectory != null) {
            String fileName = fileDirectory.getDescription(FileSystemDirectory.TAG_FILE_NAME);
            String[] fileNameSeparated = fileName.substring(0, fileName.indexOf("." + imageExtension)).split("_");
            parsedData.putIfAbsent(champs[3], fileName);
            if (fileNameSeparated.length == 3) {
                parsedData.putIfAbsent(champs[4], fileNameSeparated[0]);
                parsedData.putIfAbsent(champs[5], fileNameSeparated[1]);
                parsedData.putIfAbsent(champs[6], fileNameSeparated[2]);
            } else if (fileNameSeparated.length == 2) {
                parsedData.putIfAbsent(champs[4], fileNameSeparated[0]);
                parsedData.putIfAbsent(champs[6], fileNameSeparated[1]);
            }


            parsedData.putIfAbsent(champs[7], fileDirectory.getDescription(FileSystemDirectory.TAG_FILE_MODIFIED_DATE));
            parsedData.putIfAbsent(champs[15], String.valueOf(fileDirectory.getFloat(FileSystemDirectory.TAG_FILE_SIZE) / 1024 / 1024));
        }

        if (exifIFD0Directory != null) {
            parsedData.putIfAbsent(champs[8], exifIFD0Directory.getDescription(ExifIFD0Directory.TAG_IMAGE_DESCRIPTION));
            parsedData.putIfAbsent(champs[19], String.valueOf(exifIFD0Directory.getInt(ExifIFD0Directory.TAG_X_RESOLUTION)));
            parsedData.putIfAbsent(champs[20], fileTypeDirectory.getDescription(ExifIFD0Directory.TAG_INTEROP_INDEX));
            parsedData.putIfAbsent(champs[28], exifIFD0Directory.getDescription(ExifIFD0Directory.TAG_MAKE));
            parsedData.putIfAbsent(champs[29], exifIFD0Directory.getDescription(ExifIFD0Directory.TAG_MODEL));
            parsedData.putIfAbsent(champs[12], exifIFD0Directory.getDescription(ExifIFD0Directory.TAG_ARTIST));

        }

        parsedData.putIfAbsent(champs[16], String.valueOf(jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_WIDTH)));
        parsedData.putIfAbsent(champs[17], String.valueOf(jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_HEIGHT)));

        if (jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_WIDTH) > jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_HEIGHT))
            parsedData.putIfAbsent(champs[21], "horizontal");
        else if (jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_WIDTH) < jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_HEIGHT))
            parsedData.putIfAbsent(champs[21], "vertical");
        else
            parsedData.putIfAbsent(champs[21], "quadratic");

        if (iptcDirectory != null)
            parsedData.putIfAbsent(champs[14], iptcDirectory.getDescription(IptcDirectory.TAG_KEYWORDS));

        parsedData.putIfAbsent(champs[18], (numChannel * bitDepth) + "");

    }


    public void parseImage() throws ImageProcessingException, IOException {
        loadMetaData();
        try {
            loadExif();
        } catch (MetadataException e) {
            e.printStackTrace();
        }
        parsedData.putIfAbsent(champs[19], imageExtension.toUpperCase());
    }
}
