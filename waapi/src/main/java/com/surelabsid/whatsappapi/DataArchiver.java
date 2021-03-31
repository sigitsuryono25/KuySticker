package com.surelabsid.whatsappapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.surelabsid.whatsappapi.whatsapp_api.StickerPack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DataArchiver {
    private static int BUFFER = 8192;

    public static boolean writeStickerBookJSON(List<StickerPack> sb, Context context) {
        try {
            SharedPreferences mSettings = context.getSharedPreferences("StickerMaker", Context.MODE_PRIVATE);

            String writeValue = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new UriSerializer())
                    .create()
                    .toJson(
                            sb,
                            new TypeToken<ArrayList<StickerPack>>() {
                            }.getType());
            SharedPreferences.Editor mEditor = mSettings.edit();
            mEditor.putString("stickerbook", writeValue);
            mEditor.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static ArrayList<StickerPack> readStickerPackJSON(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences("StickerMaker", Context.MODE_PRIVATE);

        String loadValue = mSettings.getString("stickerbook", "");
        Type listType = new TypeToken<ArrayList<StickerPack>>() {
        }.getType();
        return new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .create()
                .fromJson(loadValue, listType);
    }


    public static void zip(ArrayList<String> _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.size(); i++) {
                Log.v("Compress", "Adding: " + _files.get(i));
                FileInputStream fi = new FileInputStream(_files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unzip(String _zipFile, String _targetLocation) {

        //create target location folder if not exist
        dirChecker(_targetLocation);

        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.w("DECOMPRESSING FILE", ze.getName());
                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_targetLocation + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
            Log.w("ENDED DECOMPRESSING", "DONEEEEEE");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void dirChecker(String dir) {
        File f = new File(dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    private static void stickerPackToJSONFile(StickerPack sp, String path, Context context) {
        try {
            String writeValue = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new UriSerializer())
                    .create()
                    .toJson(
                            sp,
                            StickerPack.class);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(path, sp.identifier + ".json")));
            outputStreamWriter.write(writeValue);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static StickerPack JSONFileToStickerPack(String id, String path, Context context) {

        String ret = "";

        try {

            InputStream inputStream = new FileInputStream(new File(path, id + ".json"));


            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .create()
                .fromJson(ret, StickerPack.class);
    }


    public static class UriSerializer implements JsonSerializer<Uri> {
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class UriDeserializer implements JsonDeserializer<Uri> {
        @Override
        public Uri deserialize(final JsonElement src, final Type srcType,
                               final JsonDeserializationContext context) throws JsonParseException {
            return Uri.parse(src.toString().replace("\"", ""));
        }
    }

}
