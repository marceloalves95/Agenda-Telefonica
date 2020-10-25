package chellotech.br.agendatelefonica.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import chellotech.br.agendatelefonica.R
import chellotech.br.agendatelefonica.db.BancoDAO
import chellotech.br.agendatelefonica.model.Pessoa
import chellotech.br.agendatelefonica.view.fragment.AgendaFragment
import java.util.*
import kotlin.collections.ArrayList

class AgendaFragmentAdapter(private val context: Context, private var pessoasLista: MutableList<Pessoa>) : RecyclerView.Adapter<AgendaFragmentAdapter.AgendaViewHolder>(), Filterable {

    private var listaPessoa: MutableList<Pessoa> = ArrayList()
    private var pessoa: Pessoa? = null

    init {
        listaPessoa = pessoasLista
    }


    class AgendaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nome: TextView = view.findViewById(R.id.nome)
        val endereco: TextView = view.findViewById(R.id.endereco)
        val numero: TextView = view.findViewById(R.id.numero)
        val bairro: TextView = view.findViewById(R.id.bairro)
        val email: TextView = view.findViewById(R.id.email)
        val telefone: TextView = view.findViewById(R.id.telefone)
        val image: ImageView = view.findViewById(R.id.image_delete)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {

        val view: View = LayoutInflater.from(context).inflate(R.layout.card_view_first, parent, false)

        return AgendaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {

        pessoa = listaPessoa[position]
        holder.nome.text = listaPessoa[position].nome
        holder.endereco.text = listaPessoa[position].endereco
        holder.numero.text = listaPessoa[position].numero.toString()
        holder.bairro.text = listaPessoa[position].bairro
        holder.email.text = listaPessoa[position].email
        holder.telefone.text = listaPessoa[position].telefone

        holder.image.setOnClickListener { view ->

            showPopup(view, position)

        }

    }

    private fun showPopup(view: View, position: Int) {

        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.menu_main)

        try {

            val fieldPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldPopup.isAccessible = true
            val mPopup = fieldPopup.get(popupMenu)
            mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)

        } catch (e: Exception) {

            Log.d("Erro", "Erro: $e")

        }

        popupMenu.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {

                R.id.atualizar -> {


                    atualizarPessoa(view, position)

                }

                R.id.excluir -> {

                    deleteScreen(position)

                }


            }

            true


        }

        popupMenu.show()


    }

    override fun getItemCount(): Int {
        return listaPessoa.size
    }

    fun adicionarPessoa(pessoa: Pessoa) {

        listaPessoa.add(pessoa)
        notifyItemInserted(itemCount)

    }

    private fun atualizarPessoa(view: View, position: Int) {

        val bundle = Bundle()

        bundle.putString("nome", listaPessoa[position].nome)
        bundle.putString("endereco", listaPessoa[position].endereco)
        bundle.putInt("numero", listaPessoa[position].numero)
        bundle.putString("bairro", listaPessoa[position].bairro)
        bundle.putString("email", listaPessoa[position].email)
        bundle.putString("telefone", listaPessoa[position].telefone)

        view.findNavController().navigate(R.id.action_AgendaFragment_to_PessoaFragment, bundle)



    }

    private fun deleteScreen(position: Int) {


        val dialogBuiler: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        dialogBuiler.setMessage("Você deseja deletar esta Pessoa?")
        dialogBuiler.setCancelable(true)
        dialogBuiler.setPositiveButton("Sim") { _, _ -> removerPessoa(position) }
        dialogBuiler.setNegativeButton("Não") { dialogInterface, _ -> dialogInterface.dismiss() }

        val alert: android.app.AlertDialog = dialogBuiler.create()
        alert.setIcon(R.drawable.ic_delete)
        alert.show()


    }

    private fun removerPessoa(position: Int) {

        val dao = BancoDAO(context)
        dao.deletarPessoas(listaPessoa[position].id)
        listaPessoa.removeAt(position)
        notifyItemRemoved(position)

        val bancoVazio = BancoDAO(context)
        val tela = bancoVazio.conferirListaVazia()

        if (tela) {

            AgendaFragment.agenda_vazia?.visibility = View.VISIBLE

        }



    }


    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {

                    listaPessoa = pessoasLista


                } else {

                    val resultList: MutableList<Pessoa> = ArrayList()

                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()

                    for (row in pessoasLista) {

                        if (row.nome.toLowerCase(Locale.ROOT).contains(filterPattern) || row.endereco.toLowerCase(Locale.ROOT).contains(filterPattern) || row.bairro.toLowerCase(Locale.ROOT).contains(filterPattern) || row.email.toLowerCase(Locale.ROOT).contains(filterPattern) || row.telefone.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                            resultList.add(row)

                        }

                    }

                    listaPessoa = resultList


                }

                val results = FilterResults()
                results.values = listaPessoa

                return results


            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                if (results?.values == null) {

                    listaPessoa


                } else {

                    results.values as MutableList<Pessoa>
                }

                notifyDataSetChanged()

            }


        }

    }

}