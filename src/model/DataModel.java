package model;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.bmp.BmpHeaderDirectory;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.gif.GifHeaderDirectory;
import com.drew.metadata.gif.GifImageDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.png.PngDirectory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
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
//    private String[] months = new String[] {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

    private String[] deleteList = new String[]{"<strong>", "</strong>", "&quot;", "<b>", "</b>", "<br>", "<i>"};

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
        if (res.toString().equals(""))
            return null;
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
        String text = getBigText(splited, "TAGS");
        if (text == null)
            return null;
        text = text.trim();
        if (text.equals("") || text.equals("(no tags)"))
            return null;
        StringBuilder tags = new StringBuilder(text);

        String[] tagsList = tags.toString().split("\"");
//        System.out.println(text + " tag " + tagsList.length);
        tags = new StringBuilder();
        for (String t : tagsList) {
            t = t.trim();
            if (!t.equals(""))
                tags.append(t).append(";");
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
        if (s == null)
            return null;
        s = deleteHashtag(s);
        s = deleteSentence(s, "<a", "a>");

        for (String target : deleteList)
            s = delete(s, target);

//        System.out.println("Result  : " + s);

        return s;
    }

    private void loadMetaData() throws ImageProcessingException, IOException {
//        System.out.println(directory);
        File imageFile = new File(directory);
        metadata = ImageMetadataReader.readMetadata(imageFile);
    }

    private String getDes(Directory directory, int tag) {
        if (directory.containsTag(tag))
            return directory.getDescription(tag);
        else return null;
    }
    private int getInt(Directory directory, int tag) {
        if (directory.containsTag(tag)) {
            try {
                return directory.getInt(tag);
            } catch (MetadataException e) {
                return 0;
            }
        }
        return 0;
    }


    private void loadExif() throws MetadataException {
        Directory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
        Directory fileDirectory = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
        Directory fileTypeDirectory = metadata.getFirstDirectoryOfType(FileTypeDirectory.class);
        Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        Directory exifSubIFDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        Directory iptcDirectory = metadata.getFirstDirectoryOfType(IptcDirectory.class);
        Directory pngDirectory = metadata.getFirstDirectoryOfType(PngDirectory.class);
        Directory gifHeaderDirectory = metadata.getFirstDirectoryOfType(GifHeaderDirectory.class);
        Directory bmpHeaderDirectory = metadata.getFirstDirectoryOfType(BmpHeaderDirectory.class);



        int numChannel = 3, bitDepth = 0;

//        for (Directory d : metadata.getDirectories()) {
//            System.out.println("============ Directory " + d.getName() + " ==============");
//            for (Tag t : d.getTags()) {
//                System.out.println(t.getTagType() + "|" + t.getTagName() + "|" + t.getDescription());
//            }
//        }

        int width = 0;
        int height = 0;
        if (jpegDirectory != null) {
            numChannel = Math.max(numChannel, getInt(jpegDirectory, JpegDirectory.TAG_NUMBER_OF_COMPONENTS));
            bitDepth = Math.max(bitDepth, getInt(jpegDirectory, JpegDirectory.TAG_DATA_PRECISION));
            width = getInt(jpegDirectory, JpegDirectory.TAG_IMAGE_WIDTH);
            height = getInt(jpegDirectory, JpegDirectory.TAG_IMAGE_HEIGHT);
        }

        int iso = 0;

        if (exifSubIFDirectory != null) {
            if (width == 0) {
                width = getInt(exifSubIFDirectory, ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
                height = getInt(exifSubIFDirectory, ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
            }
            parsedData.putIfAbsent(champs[6], getDes(exifSubIFDirectory, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
            parsedData.putIfAbsent(champs[22], getDes(exifSubIFDirectory, ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
            parsedData.putIfAbsent(champs[23], getDes(exifSubIFDirectory, ExifSubIFDDirectory.TAG_APERTURE));
            parsedData.putIfAbsent(champs[23], getDes(exifSubIFDirectory, ExifSubIFDDirectory.TAG_FNUMBER));
            parsedData.putIfAbsent(champs[24], getDes(exifSubIFDirectory, ExifSubIFDDirectory.TAG_MAX_APERTURE));
            parsedData.putIfAbsent(champs[25], getDes(exifSubIFDirectory, ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
            iso = getInt(exifSubIFDirectory, ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
            parsedData.putIfAbsent(champs[27], getDes(exifSubIFDirectory, ExifSubIFDDirectory.TAG_FLASH));
            parsedData.putIfAbsent(champs[30], getDes(exifSubIFDirectory, ExifSubIFDDirectory.TAG_USER_COMMENT));

        }

        if (fileDirectory != null) {
            String fileName = getDes(fileDirectory, FileSystemDirectory.TAG_FILE_NAME);
            fileName = fileName.substring(0, fileName.indexOf("." + imageExtension));
            String[] fileNameSeparated = fileName.split("_");
            String reference = null;
            String date = null, title = null;
            if (fileNameSeparated[fileNameSeparated.length - 1].matches("\\d{0,5}[.]?\\d{1,5}[.]\\d{1,5}"))
                date = fileNameSeparated[fileNameSeparated.length - 1];

            if (!fileNameSeparated[0].equals(date))
                reference = fileNameSeparated[0];
            if ((fileNameSeparated.length > 1) && (!fileNameSeparated[1].equals(date)))
                title = fileNameSeparated[1];

            parsedData.putIfAbsent(champs[3], fileName);
            parsedData.putIfAbsent(champs[4], reference);
            parsedData.putIfAbsent(champs[5], title);
            parsedData.putIfAbsent(champs[6], date);


            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            parsedData.putIfAbsent(champs[7], getDes(fileDirectory, FileSystemDirectory.TAG_FILE_MODIFIED_DATE));
            parsedData.putIfAbsent(champs[15], df.format(fileDirectory.getFloat(FileSystemDirectory.TAG_FILE_SIZE) / 1024 / 1024));
        }

        int dpi = 0;

        if (exifIFD0Directory != null) {
            String textDes = parsedData.get(champs[8]);
            String imageDes = getDes(exifIFD0Directory, ExifIFD0Directory.TAG_IMAGE_DESCRIPTION);
            imageDes = htmlStrip(imageDes);
            if (textDes != null) {
                if (imageDes != null) {
                    if (!textDes.trim().equals(imageDes.trim()))
                        parsedData.put(champs[8], (textDes.trim() + ". " + imageDes.trim()).replaceAll("\\.{2}", ".")
                                .replaceAll("\\.{2}", "..."));
                } else
                    parsedData.put(champs[8], textDes);
            } else {
                if (imageDes != null) {
                    parsedData.put(champs[8], imageDes);
                }
            }

            dpi = getInt(exifIFD0Directory, ExifIFD0Directory.TAG_X_RESOLUTION);
            parsedData.putIfAbsent(champs[28], getDes(exifIFD0Directory, ExifIFD0Directory.TAG_MAKE));
            parsedData.putIfAbsent(champs[29], getDes(exifIFD0Directory, ExifIFD0Directory.TAG_MODEL));
            parsedData.putIfAbsent(champs[12], getDes(exifIFD0Directory, ExifIFD0Directory.TAG_ARTIST));

            bitDepth = Math.max(bitDepth, getInt(exifIFD0Directory, ExifIFD0Directory.TAG_BITS_PER_SAMPLE));
        }

        if (iptcDirectory != null) {
            String imageKey = getDes(iptcDirectory, IptcDirectory.TAG_KEYWORDS);
            String textKey = parsedData.get(champs[14]);

            if (textKey != null) {
                if (imageKey != null) {
                    if (!imageKey.equals(textKey))
                        parsedData.putIfAbsent(champs[14], imageKey + textKey);
                }
            }

            parsedData.putIfAbsent(champs[14], imageKey);
        }

        if (fileDirectory != null) {
            parsedData.putIfAbsent(champs[20], getDes(fileTypeDirectory, FileTypeDirectory.TAG_DETECTED_FILE_TYPE_NAME));
        }

        if (pngDirectory != null) {
            width = getInt(pngDirectory, PngDirectory.TAG_IMAGE_WIDTH);
            height = getInt(pngDirectory, PngDirectory.TAG_IMAGE_HEIGHT);
            bitDepth = getInt(pngDirectory, PngDirectory.TAG_BITS_PER_SAMPLE);
        }
        if (gifHeaderDirectory != null) {
            width = getInt(gifHeaderDirectory, GifHeaderDirectory.TAG_IMAGE_WIDTH);
            height = getInt(gifHeaderDirectory, GifHeaderDirectory.TAG_IMAGE_HEIGHT);
            bitDepth = Math.max(bitDepth, getInt(gifHeaderDirectory, GifHeaderDirectory.TAG_BITS_PER_PIXEL));
        }
        if (bmpHeaderDirectory != null) {
            width = getInt(bmpHeaderDirectory, BmpHeaderDirectory.TAG_IMAGE_WIDTH);
            height = getInt(bmpHeaderDirectory, BmpHeaderDirectory.TAG_IMAGE_HEIGHT);
            bitDepth = getInt(bmpHeaderDirectory, BmpHeaderDirectory.TAG_BITS_PER_PIXEL);
        }

        parsedData.putIfAbsent(champs[16], String.valueOf(width));
        parsedData.putIfAbsent(champs[17], String.valueOf(height));
//        System.out.println(numChannel + " " + bitDepth);
        parsedData.putIfAbsent(champs[18], String.valueOf(numChannel * bitDepth));
        parsedData.putIfAbsent(champs[19], String.valueOf(dpi));
        if (iso != 0)
            parsedData.putIfAbsent(champs[26], String.valueOf(iso));
        if (width > height)
            parsedData.putIfAbsent(champs[21], "horizontal");
        else if (width < height)
            parsedData.putIfAbsent(champs[21], "vertical");
        else
            parsedData.putIfAbsent(champs[21], "quadratic");


    }


    public void parseImage() throws ImageProcessingException, IOException {
        loadMetaData();
        try {
            loadExif();
        } catch (MetadataException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        parsedData.putIfAbsent(champs[20], imageExtension.toUpperCase());
    }
}
