package chellotech.br.agendatelefonica.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import chellotech.br.agendatelefonica.model.Pessoa

class BancoDAO(context: Context) {

    private val banco:Banco = Banco(context)

    fun inserirPessoas(pessoa: Pessoa){

        val db: SQLiteDatabase = banco.writableDatabase
        val values = ContentValues()

        values.put("nome", pessoa.nome)
        values.put("endereco", pessoa.endereco)
        values.put("numero", pessoa.numero)
        values.put("bairro", pessoa.bairro)
        values.put("email", pessoa.email)
        values.put("telefone", pessoa.telefone)

        try {
            db.insertOrThrow("Pessoas", null, values)
            db.close()



        } catch (erro: SQLiteConstraintException) {

            db.update("Pessoas",values,"email = ?", arrayOf(pessoa.email))


        }


    }

    fun conferirListaVazia() : Boolean{

        val db: SQLiteDatabase = banco.writableDatabase
        var valorLogico = true

        try{

            val sql = "SELECT COUNT(*) FROM Pessoas"
            val cursor = db.rawQuery(sql, null)

            cursor?.moveToFirst()


            if (cursor.getInt(0) == 0 ){

                Log.d("Cursor", "Tabela Vazia")

            }else{

                valorLogico = false

            }

            cursor.close()


        }catch (e:SQLiteException){

            Log.d("Erro", "Erro:$e")

        }

        return valorLogico

    }
    fun deletarPessoas(id:Int){

        val db: SQLiteDatabase = banco.writableDatabase

        try {

            val sql = "DELETE FROM Pessoas WHERE id = $id "

            db.execSQL(sql)

        } catch (e: SQLiteException) {


        }

        db.close()


    }


    fun listarPessoas() : MutableList<Pessoa>{

        val listAll:MutableList<Pessoa> = ArrayList()

        try {

            val sql = "SELECT * FROM Pessoas"
            val db: SQLiteDatabase = banco.readableDatabase

            val cursor: Cursor = db.rawQuery(sql, null)

            while (cursor.moveToNext()) {

                val pessoas = Pessoa(

                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("nome")),
                    cursor.getString(cursor.getColumnIndex("endereco")),
                    cursor.getInt(cursor.getColumnIndex("numero")),
                    cursor.getString(cursor.getColumnIndex("bairro")),
                    cursor.getString(cursor.getColumnIndex("email")),
                    cursor.getString(cursor.getColumnIndex("telefone"))

                    )

                listAll.add(pessoas)

            }

            db.close()
            cursor.close()


        } catch (e: SQLiteException) {

            Log.d("Erro", "Erro:$e")

        }


        return listAll


    }

}