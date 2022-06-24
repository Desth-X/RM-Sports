package co.edu.univalle.www;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DBLocalConection extends SQLiteOpenHelper {

    private static final String TAG = "APPMSG(DBLocalConection)";//DBLocalConection.class.getSimpleName();
    private static String DB_PATH = "";
    private static final int DATABASE_VERSION = 1;
    private static String DB_NAME = "Preferencias.db";
    private static DBLocalConection myInstance;
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    private DBLocalConection(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;

        DB_PATH = myContext.getDatabasePath(DB_NAME).getPath();
    }

    public static synchronized DBLocalConection getInstance(Context context) {
        if (myInstance == null) {
            myInstance = new DBLocalConection(context.getApplicationContext());
        }
        return myInstance;
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (!dbExist) {
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, "createDataBase " + e);
                throw new IOException("Error copiando Base de Datos");
            }
        }
    }

    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

            if (checkDB.getVersion() == 0)
                checkDB.setVersion(DATABASE_VERSION);


        } catch (SQLiteException e) {
            // si llegamos aqui es porque la base de datos no existe todavia.
            Log.e(TAG, "checkDataBase: " + e);
        } finally {
            if (checkDB != null) {
                checkDB.close();
            }
        }

        // si la base de datos se pudo abrir es porque existe sino retorna falso
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        // se abre la bd copiada en assets
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        // obtiene la ruta de la bd del dispositivo
        String outFileName = DB_PATH;
        // Abrimos la base de datos vacia como salida
        OutputStream myOutput = new FileOutputStream(outFileName);
        // Transferimos los bytes desde el fichero de entrada al de salida
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // se cierran los flujos
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void open() throws SQLException {

        // Abre la base de datos
        try {
            createDataBase();
        } catch (IOException e) {
            Log.e(TAG, "open " + e);
            throw new SQLException("Ha sido imposible crear la Base de Datos");
        }

        myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null) // cierra la base de datos
            myDataBase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //no se debe colocar un throw new UnsupportedOperationException(); ya que para la
        //aplicación y no permite que si el aarchivo no existe se cree en el movil
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //no se debe colocar un throw new UnsupportedOperationException(); ya que para la
        //aplicación y no permite que si el aarchivo no existe se cree en el movil
    }

    /*******************************************************************************************/

    public String obtenerIdUsuario() {
        Cursor query = null;
        try {
            open();
            query = myDataBase.rawQuery("SELECT * FROM sesion", null);
            if (query.moveToFirst()) {
                String Id = query.getString(1);
                return Id;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error al consultar bd local " + e);
        } finally {
            if (query != null)
                if (!query.isClosed())
                    query.close();
            close();
        }
        return "";
    }

    public void eliminarInformacionSesion(String Id) {
        try {
            open();
            //recibe la tabla y las condiciones, en este caso elimina un estudiante por su id
            myDataBase.delete("Sesion", "usuario " + " = '" + Id + "'", null);
        } catch (SQLException e) {
            Log.e(TAG, "eliminarInformacionSesion " + e);
        } finally {
            close();
        }
    }


    public void insertarInformacionSesion(String id) {
        try {
            open();
            ContentValues newValues = new ContentValues();
            newValues.put("usuario", id);
            myDataBase.insert("Sesion", null, newValues);
        } catch (SQLException e) {
            Log.e(TAG, "insertarInformacionSesion" + e);
        } finally {
            close();
        }
    }

}





