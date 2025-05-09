package com.example.cacaoapp;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import  android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper{

    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDatos) {
        BaseDatos.execSQL("CREATE TABLE inventario (" +
                "idInventario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Fecha TEXT, " +
                "Vendedor TEXT, " +
                "Cliente TEXT, " +
                "Tipocacao TEXT, " +
                "CantidadCacao INT, " +
                "PrecioAPagar INT, " +
                "PagoDeCacao INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
