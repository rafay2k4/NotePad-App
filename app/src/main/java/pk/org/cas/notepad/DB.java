package pk.org.cas.notepad;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {
    static Context context;
    private static DB instance;
    public static final String DB_NAME = "NOTEPAD";
    public static final int DB_VERSION = 42;

    private DB(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DB getInstance(Context context){
        if(instance == null){
            instance = new DB(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Notes.CREATE_TABLE);
        db.execSQL(User.CREATE_TABLE);
        db.execSQL(Favourite.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if((oldVersion != newVersion)){
            db.execSQL(Notes.DROP_TABLE);
            db.execSQL(Notes.CREATE_TABLE);

            db.execSQL(User.DROP_TABLE);
            db.execSQL(User.CREATE_TABLE);

            db.execSQL(Favourite.DROP_TABLE);
            db.execSQL(Favourite.CREATE_TABLE);
        }
    }

    // Crude Operations of Note.
    public boolean insertNote(Notes note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Notes.COL_TITLE, note.getTitle());
        contentValues.put(Notes.COL_NOTE, note.getNote());
        contentValues.put(Notes.COL_DATE, note.getDate());
        long rowID;
        try {
            rowID = db.insert(Notes.TABLE_NAME, null, contentValues);
        }catch (Exception ex){
            return false;
        }
        return rowID != -1;
    }

    public boolean updateNote(Notes note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Notes.COL_TITLE, note.getTitle());
        contentValues.put(Notes.COL_NOTE, note.getNote());
        contentValues.put(Notes.COL_DATE, note.getDate());
        long rowID;
        try{
            rowID = db.update(Notes.TABLE_NAME, contentValues, Notes.COL_NOTE_ID+"= ?", new String[]{String.valueOf(note.getNoteId())});
        }catch (Exception ex){
            return false;
        }
        return rowID != -1;
    }

    public boolean deleteNote(int noteId){
        SQLiteDatabase db = getWritableDatabase();
        long rowId;
        try {
            rowId = db.delete(Notes.TABLE_NAME, Notes.COL_NOTE_ID+" = ? ", new String[]{String.valueOf(noteId)});
        }catch (Exception ex){
            return false;
        }
        return rowId != -1;
    }

    public List<Notes> fetchNotes(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(Notes.SELECT_ALL_NOTES, null);
        List<Notes> notes = new ArrayList<>(cursor.getCount());
        if(cursor.moveToFirst()){
            do{
                Notes note = new Notes();
                int index = cursor.getColumnIndex(Notes.COL_NOTE_ID);
                note.setNoteId(cursor.getInt(index));
                index = cursor.getColumnIndex(Notes.COL_TITLE);
                note.setTitle(cursor.getString(index));
                index = cursor.getColumnIndex(Notes.COL_NOTE);
                note.setNote(cursor.getString(index));
                index = cursor.getColumnIndex(Notes.COL_DATE);
                note.setDate(cursor.getString(index));
                notes.add(note);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }



    // Crude Operations of Note.
    public boolean insertFavourite(Favourite favourite){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Favourite.COL_TITLE, favourite.getTitle());
        contentValues.put(Favourite.COL_NOTE, favourite.getNote());
        long rowID;
        try {
            rowID = db.insert(Favourite.TABLE_NAME, null, contentValues);
        }catch (Exception ex){
            return false;
        }
        return rowID != -1;
    }

    public boolean updateFavourite(Favourite favourite){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Favourite.COL_TITLE, favourite.getTitle());
        contentValues.put(Favourite.COL_NOTE, favourite.getNote());
        long rowID;
        try{
            rowID = db.update(Favourite.TABLE_NAME, contentValues, Favourite.COL_NOTE_ID+"= ?", new String[]{String.valueOf(favourite.getNoteId())});
        }catch (Exception ex){
            return false;
        }
        return rowID != -1;
    }


    public boolean deleteFavourite(int noteId){
        SQLiteDatabase db = getWritableDatabase();
        long rowId;
        try {
            rowId = db.delete(Favourite.TABLE_NAME, Favourite.COL_NOTE_ID+" = ? ", new String[]{String.valueOf(noteId)});
        }catch (Exception ex){
            return false;
        }
        return rowId != -1;
    }

    public List<Favourite> fetchFavourite(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(Favourite.SELECT_ALL_NOTES, null);
        List<Favourite> favourites = new ArrayList<>(cursor.getCount());
        if(cursor.moveToFirst()){
            do{
                Favourite favourite = new Favourite();
                int index = cursor.getColumnIndex(Favourite.COL_NOTE_ID);
                favourite.setNoteId(cursor.getInt(index));
                index = cursor.getColumnIndex(Favourite.COL_TITLE);
                favourite.setTitle(cursor.getString(index));
                index = cursor.getColumnIndex(Favourite.COL_NOTE);
                favourite.setNote(cursor.getString(index));
                favourites.add(favourite);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return favourites;
    }



    

    // Crude Operations of User.
    public boolean insertUser(User user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User.COL_NAME, user.getName());
        contentValues.put(User.COL_EMAIL, user.getEmail());
        contentValues.put(User.COL_PROFILE_PIC, getBytes(user.getProfilePic()));

        long rowID;
        try {
            rowID = db.insert(User.TABLE_NAME, null, contentValues);
        }catch (Exception ex){
            return false;
        }
        return rowID != -1;
    }

    public boolean updateUser(User user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User.COL_NAME, user.getName());
        contentValues.put(User.COL_EMAIL, user.getEmail());
        contentValues.put(User.COL_PROFILE_PIC, getBytes(user.getProfilePic()));

        long rowID;
        try{
            rowID = db.update(User.TABLE_NAME, contentValues, User.COL_USER_ID+"= ?", new String[]{String.valueOf(user.getUserId())});
        }catch (Exception ex){
            return false;
        }
        return rowID != -1;
    }

    public boolean deleteUser(int userId){
        SQLiteDatabase db = getWritableDatabase();
        long rowId;
        try {
            rowId = db.delete(User.TABLE_NAME, User.COL_USER_ID+" = ? ", new String[]{String.valueOf(userId)});
        }catch (Exception ex){
            return false;
        }
        return rowId != -1;
    }

    public List<User> fetchUsers(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(User.SELECT_ALL_USERS, null);
        List<User> users = new ArrayList<>(cursor.getCount());
        if(cursor.moveToFirst()){
            do{
                User user = new User();
                int index = cursor.getColumnIndex(User.COL_USER_ID);
                user.setUserId(cursor.getInt(index));
                index = cursor.getColumnIndex(User.COL_NAME);
                user.setName(cursor.getString(index));
                index = cursor.getColumnIndex(User.COL_EMAIL);
                user.setEmail(cursor.getString(index));
                index = cursor.getColumnIndex(User.COL_PROFILE_PIC);
                user.setProfilePic(getImage(cursor.getBlob(index)));
                users.add(user);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }


    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        return compressImage(bitmap, 1100, 1100, 1200000);

    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static byte[] compressImage(Bitmap image, int maxWidth, int maxHeight, int maxSizeInBytes) {
        int width = image.getWidth();
        int height = image.getHeight();

        float scale = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        Bitmap resizedImage = Bitmap.createScaledBitmap(image, newWidth, newHeight, false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        while (outputStream.toByteArray().length > maxSizeInBytes) {
            outputStream.reset();
            resizedImage.compress(Bitmap.CompressFormat.JPEG, 50, outputStream); // Reduce quality by 50%
        }

        byte[] compressedData = outputStream.toByteArray();
        return compressedData;
    }
}
