package chellotech.br.agendatelefonica.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Banco (context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {


    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE $TABLE_PESSOAS (" +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$NOME TEXT NOT NULL," +
                "$ENDERECO TEXT NOT NULL," +
                "$NUMERO INT NOT NULL, " +
                "$BAIRRO TEXT NOT NULL, " +
                "$EMAIL TEXT UNIQUE NOT NULL, "+
                "$TELEFONE TEXT UNIQUE NOT NULL);"

        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        val dropTable = "DROP TABLE IF EXISTS $TABLE_PESSOAS"
        db?.execSQL(dropTable)
        onCreate(db)
    }

    companion object Pessoa{

        //Nome da versão e nome do banco de dados
        private const val DB_VERSION = 1
        private const val DB_NAME = "agenda_telefonica"

        //Aqui se define o(s) nome(s) da(s) tabela(s) do banco de dados
        private const val TABLE_PESSOAS = "Pessoas"

        //Nome do(s) campo(s) da(s) tabela(s)
        private const val ID = "id"
        private const val NOME = "nome"
        private const val ENDERECO = "endereco"
        private const val NUMERO = "numero"
        private const val BAIRRO = "bairro"
        private const val EMAIL = "email"
        private const val TELEFONE = "telefone"

    }


}