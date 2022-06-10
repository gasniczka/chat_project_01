package org.jk.project_chat_from_v2;

import java.io.*;
import java.util.LinkedList;
import java.util.List;


public final class IOUtils {

    private static final String FILE_NAME = "clientTokens.ser";

    private IOUtils() {

    }


    public static List<ServerEvent> readHistory() {

        List<ServerEvent> recordList = new LinkedList<>();

        try (FileInputStream fIn = new FileInputStream(FILE_NAME);
             ObjectInputStream oIn = new ObjectInputStream(fIn)
        ) {

            Object readObject;

            while ((readObject = oIn.readObject()) != null) {
                recordList.add((ServerEvent) readObject);
            }

        } catch (EOFException e) {
            System.out.println("EOF: " + FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordList;
    }


    public static void saveHistory(ServerEvent serverEvent) {

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

            oOut.writeObject(serverEvent);

            oOut.flush();

            oOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
            // do not write a header, but reset:
            // this line added after another question
            // showed a problem with the original
            reset();
        }

    }

}
