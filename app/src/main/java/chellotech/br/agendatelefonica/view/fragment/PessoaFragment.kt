package chellotech.br.agendatelefonica.view.fragment

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import chellotech.br.agendatelefonica.R
import chellotech.br.agendatelefonica.db.BancoDAO
import chellotech.br.agendatelefonica.model.Pessoa
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.santalu.maskedittext.MaskEditText


class PessoaFragment : Fragment() {

    private var viewFragment: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_pessoa, container, false)

        return viewFragment
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nome = view.findViewById(R.id.nome)
        endereco = view.findViewById(R.id.endereco)
        numero = view.findViewById(R.id.numero)
        bairro = view.findViewById(R.id.bairro)
        email = view.findViewById(R.id.email)
        telefone = view.findViewById(R.id.telefone)
        campo_nome = view.findViewById(R.id.campo_nome)
        campo_endereco = view.findViewById(R.id.campo_endereco)
        campo_numero = view.findViewById(R.id.campo_numero)
        campo_bairro = view.findViewById(R.id.campo_bairro)
        campo_email = view.findViewById(R.id.campo_email)
        campo_telefone = view.findViewById(R.id.campo_telefone)
        cadastrar_atualizar = view.findViewById(R.id.cadastrar_atualizar)

        atualizarCampos()


    }

    private fun atualizarCampos(){

        val args: Bundle? = arguments

        if(args == null){

            nome?.setText("")
            endereco?.setText("")
            numero?.setText("")
            bairro?.setText("")
            email?.setText("")
            telefone?.setText("")
            nome?.requestFocus()

            cadastrar_atualizar?.setText(R.string.cadastrar_pessoa)
            cadastrar_atualizar?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.content_save,0,0,0)
            cadastrar_atualizar?.setOnClickListener {

                cadastrarPessoa("Pessoa cadastrada com sucesso")


            }


        }else{

            val nomeArgs = args.getString("nome").toString()
            val enderecoArgs = args.getString("endereco").toString()
            val numeroArgs = args.getInt("numero").toString()
            val bairroArgs = args.getString("bairro").toString()
            val emailArgs = args.getString("email").toString()
            val telefoneArgs = args.getString("telefone").toString()

            nome?.setText(nomeArgs)
            endereco?.setText(enderecoArgs)
            numero?.setText(numeroArgs)
            bairro?.setText(bairroArgs)
            email?.setText(emailArgs)
            telefone?.setText(telefoneArgs)
            campo_email?.visibility = View.GONE
            email?.visibility = View.GONE
            campo_telefone?.visibility = View.GONE
            telefone?.visibility = View.GONE
            nome?.requestFocus()

            cadastrar_atualizar?.setText(R.string.atualizar_pessoa)
            cadastrar_atualizar?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_update_white,0,0,0)

            cadastrar_atualizar?.setOnClickListener {

               cadastrarPessoa("Pessoa alterada com sucesso")


            }


        }

    }

    private fun validarCampos(): Boolean {

        val validar: Boolean

        val nomeCampo = nome?.editableText.toString().trim()
        val enderecoCampo = endereco?.editableText.toString().trim()
        val numeroCampo = numero?.editableText.toString().trim()
        val bairroCampo = bairro?.editableText.toString().trim()
        val telefoneCampo = telefone?.editableText.toString().trim()
        val emailCampo = email?.editableText.toString().trim()

        if (nomeCampo.isEmpty() && enderecoCampo.isEmpty() && numeroCampo.isEmpty() && bairroCampo.isEmpty() && emailCampo.isEmpty() && telefoneCampo.isEmpty() && !Patterns.PHONE.matcher(telefoneCampo).matches()) {

            campo_nome?.error = "Preencha o campo corretamente"
            campo_endereco?.error = "Preencha o campo corretamente"
            campo_numero?.error = "Preencha o campo corretamente"
            campo_bairro?.error = "Preencha o campo corretamente"
            campo_email?.error = "Por favor, digite um e-mail válido"
            campo_telefone?.error = "Por favor, digite um número de telefone válido"

             validar = false
            return validar

        } else
            if (!Patterns.EMAIL_ADDRESS.matcher(emailCampo).matches()) {

                campo_email?.error = "Por favor, digite um e-mail válido"

                validar = false
                return validar

            } else
                if (!Patterns.PHONE.matcher(telefoneCampo).matches()) {

                    campo_telefone?.error = "Por favor, digite um número de telefone válido"
                    validar = false
                    return validar


                } else {

                   validar = true
                    return validar


                }


    }


    private fun cadastrarPessoa(message:String) {

        if(validarCampos()){

            val dao = BancoDAO(requireContext())

            val pessoa = Pessoa(
                0,
                nome?.text.toString(),
                endereco?.text.toString(),
                numero?.text.toString().toInt(),
                bairro?.text.toString(),
                email?.text.toString(),
                telefone?.text.toString()
            )

            dao.inserirPessoas(pessoa)

                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .setAction("Acão", null).show()

                AgendaFragment.adapter?.adicionarPessoa(pessoa)

                findNavController().navigate(R.id.action_PessoaFragment_to_AgendaFragment)



        }

    }


    companion object {

        var nome: TextInputEditText? = null
        var endereco: TextInputEditText? = null
        var numero: TextInputEditText? = null
        var bairro: TextInputEditText? = null
        var email: TextInputEditText? = null
        var telefone: MaskEditText? = null
        var cadastrar_atualizar: Button? = null
        var campo_nome: TextInputLayout? = null
        var campo_endereco: TextInputLayout? = null
        var campo_numero: TextInputLayout? = null
        var campo_bairro: TextInputLayout? = null
        var campo_email: TextInputLayout? = null
        var campo_telefone: TextInputLayout? = null


    }
}