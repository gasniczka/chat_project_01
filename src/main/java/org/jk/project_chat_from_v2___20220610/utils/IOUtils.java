package org.jk.project_chat_from_v2___20220610.utils;

import lombok.extern.java.Log;
import org.jk.project_chat_from_v2___20220610.commons.HistoryObject;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;


@Log
public final class IOUtils {

    private static final String FILE_NAME = "clientTokens.ser";

    private IOUtils() {

    }


    public static List<HistoryObject> readHistory() {

        List<HistoryObject> recordList = new LinkedList<>();

        try (FileInputStream fIn = new FileInputStream(FILE_NAME);
             ObjectInputStream oIn = new ObjectInputStream(fIn)
        ) {

            Object readObject;

            while ((readObject = oIn.readObject()) != null) {
                recordList.add((HistoryObject) readObject);
            }

        } catch (EOFException e) {
            log.info("EOF: " + FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordList;
    }


    public static void saveHistory(HistoryObject historyObject) {

        File file = new File(FILE_NAME);

        boolean append = file.exists();


        try (FileOutputStream fOut = new FileOutputStream(file, true)
        ) {

            ObjectOutputStream oOut;

            if (append) {
                oOut = new AppendingObjectOutputStream(fOut);
            } else {
                oOut = new ObjectOutputStream(fOut);
            }

            oOut.writeObject(historyObject);

//            System.out.println("IOUtils.saveHistory: "
//                    + historyObject.getChatroom() + " "
//                    + historyObject.getTransferObject().getClientId() + " "
//                    + historyObject.getTransferObject().getMessage());

            oOut.flush();

            oOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    static class AppendingObjectOutputStream extends ObjectOutputStream {

        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            // do not write a header, but reset
            reset();
        }

    }

}
