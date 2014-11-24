package uk.co.massimocarli.restmethod.http.deserializer;

import android.content.Context;
import android.util.Log;
import uk.co.massimocarli.restmethod.util.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is the implementation of the Deserializer that reads the data from a Stream and save
 * them to a specific directory on File system
 * <p/>
 * Created by Massimo Carli on 05/11/2013.
 */
public final class FileDeserializer implements Deserializer<Void> {

    /**
     * The name for this Deserializer implementation
     */
    public static final String DESERIALIZER_NAME = "FileDeserializer";
    /*
     * The Tag for the log
     */
    private static final String LOG_TAG = FileDeserializer.class.getName();
    /**
     * The File to save
     */
    private final File mFileToSave;

    /**
     * If true the file is overwritten if already present. If false the file is skipped if
     * present.
     */
    private final boolean mOverwrite;

    /**
     * Private constructor that creates a FileDeserializer for a given File
     *
     * @param fileName  The name of the file to save
     * @param overwrite If true the file is overwritten if already present
     */
    private FileDeserializer(final String fileName, final boolean overwrite) {
        mFileToSave = new File(fileName);
        mOverwrite = overwrite;
    }

    /**
     * Private constructor that creates a FileDeserializer for a given File
     *
     * @param file      The  file to save
     * @param overwrite If true the file is overwritten if already present
     */
    private FileDeserializer(final File file, final boolean overwrite) {
        mFileToSave = file;
        mOverwrite = overwrite;
    }

    /**
     * Private constructor that creates a FileDeserializer with a given encoding
     */
    private FileDeserializer(final String directory, final String fileName, final boolean overwrite) {
        mFileToSave = new File(directory, fileName);
        mOverwrite = overwrite;
    }

    /**
     * This is the static factory method for the FileDeserializer given the destination
     * file and the overwrite flag
     *
     * @param fileName  The name of the file to save
     * @param overwrite If true the file is overwritten if already present
     * @return The FileDeserializer instance
     */
    public synchronized static FileDeserializer get(final String fileName, final boolean overwrite) {
        return new FileDeserializer(fileName, overwrite);
    }

    /**
     * This is the static factory method for the FileDeserializer given the destination
     * file and the overwrite flag
     *
     * @param file      The file to save
     * @param overwrite If true the file is overwritten if already present
     * @return The FileDeserializer instance
     */
    public synchronized static FileDeserializer get(final File file, final boolean overwrite) {
        return new FileDeserializer(file, overwrite);
    }

    /**
     * This is the static factory method for the FileDeserializer given the destination
     * file and the overwrite flag
     *
     * @param directory The directory of the file to save
     * @param fileName  The name of the file to save
     * @param overwrite If true the file is overwritten if already present
     * @return The FileDeserializer instance
     */
    public synchronized static FileDeserializer get(final String directory, final String fileName,
                                                    final boolean overwrite) {
        return new FileDeserializer(directory, fileName, overwrite);
    }

    @Override
    public Void realise(InputStream inputStream, Context context) throws IOException {
        if (mFileToSave.exists() && !mOverwrite) {
            Log.d(LOG_TAG, mFileToSave + " is already present and overwrite is false!");
            return null;
        }
        // We check if the directory exists. if not we create it
        final File containerDir = mFileToSave.getParentFile();
        if (!containerDir.exists()) {
            containerDir.mkdirs();
            Log.d(LOG_TAG, "Folder " + containerDir + " created!");
        }
        final String tempDestinationFile = mFileToSave.getAbsolutePath() + "_tmp";
        FileOutputStream tmp = null;
        BufferedInputStream flushedInputStream = null;
        OutputStream fos = null;
        try {
            tmp = new FileOutputStream(tempDestinationFile);
            flushedInputStream = new BufferedInputStream(inputStream);
            fos = new BufferedOutputStream(tmp);
            IOUtils.copy(flushedInputStream, fos);
        } catch (Exception e) {
            throw new IOException("Error downloading image " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(tmp);
            IOUtils.closeQuietly(flushedInputStream);
        }
        File tmpFile = new File(tempDestinationFile);
        if (tmpFile.exists()) {
            // Renaming the temporary file
            tmpFile.renameTo(mFileToSave);
        }
        return null;
    }

}
