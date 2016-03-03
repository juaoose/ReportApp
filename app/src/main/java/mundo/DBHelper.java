package mundo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Camilo on 03/03/2016.
 */
public class DBHelper extends SQLiteOpenHelper
{
    private final static String DB_NAME = "Reportapp.db";
    final static String RUTA_TABLE_NAME = "Rutas";
    final static String RUTA_COLUMN_ID = "id";
    final static String RUTA_COLUMN_TITLE = "titulo";
    final static String VISITA_TABLE_NAME = "Visitas";
    final static String VISITA_COLUMN_ID = "idVisitas";
    final static String VISITA_COLUMN_HOUR = "hora";
    final static String VISITA_COLUMN_MINUTE = "minuto";
    final static String VISITA_COLUMN_POINT = "idPunto";
    final static String VISITA_COLUMN_RUTA_ID = "idRuta";
    final static String PUNTOS_TABLE_NAME = "Puntos";
    final static String PUNTOS_COLUMN_ID = "id";
    final static String PUNTOS_COLUMN_DESCRIPTION = "descripcion";

    public DBHelper(Context context)
    {
        super(context,DB_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+RUTA_TABLE_NAME+" ("+RUTA_COLUMN_ID+" integer primary key, "+RUTA_COLUMN_TITLE+" varchar)");
        db.execSQL("CREATE TABLE "+VISITA_TABLE_NAME+" ("+VISITA_COLUMN_ID+" integer primary key, "+VISITA_COLUMN_RUTA_ID+" integer, " +
                VISITA_COLUMN_HOUR+" integer, "+ VISITA_COLUMN_MINUTE+" integer, "+VISITA_COLUMN_POINT+" integer)");
        db.execSQL("CREATE TABLE "+PUNTOS_TABLE_NAME+" ("+PUNTOS_COLUMN_ID+" integer primary key, "+PUNTOS_COLUMN_DESCRIPTION+" varchar)");
        db.execSQL("ALTER TABLE "+ VISITA_TABLE_NAME +
                "ADD CONSTRAINT FK_"+VISITA_TABLE_NAME+"_"+PUNTOS_TABLE_NAME+" FOREIGN KEY ("+VISITA_COLUMN_POINT+") " +
                "    REFERENCES "+PUNTOS_TABLE_NAME+" ("+PUNTOS_COLUMN_ID+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+RUTA_TABLE_NAME
                +"\n DROP TABLE IF EXISTS "+"");
        onCreate(db);
    }
}
