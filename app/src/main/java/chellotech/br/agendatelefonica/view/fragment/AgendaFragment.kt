package chellotech.br.agendatelefonica.view.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chellotech.br.agendatelefonica.R
import chellotech.br.agendatelefonica.adapters.AgendaFragmentAdapter
import chellotech.br.agendatelefonica.db.BancoDAO
import chellotech.br.agendatelefonica.model.Pessoa
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream


class AgendaFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var recyclerView: RecyclerView? = null
    private var viewFragment: View? = null
    private var pdfFile: File? = null

    private val padding = 10
    private val rowspan = 2
    private val sizeFont = 20
    private val colspan = 14

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_agenda, container, false)
        setHasOptionsMenu(true)

        configurarRecyclerView()

        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarRecyclerView()

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {

            findNavController().navigate(R.id.action_AgendaFragment_to_PessoaFragment)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val search: SearchView = searchItem.actionView as SearchView
        search.imeOptions = EditorInfo.IME_ACTION_DONE
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                adapter?.filter?.filter(newText)
                return false
            }


        })


        super.onCreateOptionsMenu(menu, inflater)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.relatorio -> {

                gerarRelatorio()

            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun gerarRelatorio() {

        permitirArquivo()

        try {
            createPdf()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: DocumentException) {
            e.printStackTrace()
        }

    }

    @AfterPermissionGranted(123)
    fun permitirArquivo() {

        if (hasWhitePermission()) {

            createPdf()

        } else {

            EasyPermissions.requestPermissions(this@AgendaFragment, "Precisamos de permissão para esse aplicativo", 123, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        }

    }

    private fun hasWhitePermission(): Boolean {

        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this@AgendaFragment)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("Permissions Granted", requestCode.toString())
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this@AgendaFragment, perms)) {

            AppSettingsDialog.Builder(this@AgendaFragment).build().show()


        }
    }

    private fun pegarListaPessoas(): MutableList<Pessoa> {

        val dao = BancoDAO(requireContext())
        val listdao: MutableList<Pessoa> = ArrayList()
        listdao.addAll(dao.listarPessoas())

        return listdao

    }

    private fun createPdf() {

        val arquivo = "Relatorio.pdf"
        val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Agenda/")

        if (!docsFolder.exists()) {

            docsFolder.mkdir()

            pdfFile = File(docsFolder.absolutePath, arquivo)
            val output: OutputStream = FileOutputStream(pdfFile)
            val document = Document(PageSize.A4, 18F, 18F, 18F, 18F)
            PdfWriter.getInstance(document, output)
            document.open()
            tabela(document)
            document.close()

        }

        if (docsFolder.exists()) {

            val byteArray = byteArrayOf(docsFolder.length().toByte())
            pdfFile = File(docsFolder.absolutePath, arquivo)
            val output: OutputStream = FileOutputStream(pdfFile)
            output.write(byteArray)
            val document = Document(PageSize.A4, 18F, 18F, 18F, 18F)
            PdfWriter.getInstance(document, output)
            document.open()
            tabela(document)
            document.close()
            output.close()
            previewPDF(pdfFile!!)

        }

    }

    private fun previewPDF(file: File) {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.type = "application/pdf"

        val packageManager: PackageManager = requireContext().packageManager
        val list: List<ResolveInfo> = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.isNotEmpty()) {

            val uri: Uri = FileProvider.getUriForFile(requireContext(), "chellotech.br.agendatelefonica.fileprovider", file)
            intent.setDataAndType(uri, "application/pdf")
            startActivity(intent)

        } else {

            Toast.makeText(requireContext(), "Instale um aplicativo de PDF que possa gerar este documento", Toast.LENGTH_LONG).show()
        }


    }

    @Throws(DocumentException::class)
    private fun tabela(document: Document) {

        //Criação da Tabela com dimensão de 20 colunas
        val table = PdfPTable(20)
        table.widthPercentage = 100f
        table.spacingBefore()

        table.addCell(criarLinhaExtendida())

        table.addCell(cabecalhoSecundario("Nome", 4))
        table.addCell(cabecalhoSecundario("Endereço", 3))
        table.addCell(cabecalhoSecundario("Número", 3))
        table.addCell(cabecalhoSecundario("Bairro", 3))
        table.addCell(cabecalhoSecundario("E-mail",  3))
        table.addCell(cabecalhoSecundario("Telefone",  4))
        gerarLinhas(table)
        document.add(table)
    }

    private fun criarLinhaExtendida(): PdfPCell? {

        val f: Font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, sizeFont.toFloat())
        val celula = PdfPCell(Paragraph("Agenda Telefônica", f))
        celula.borderWidth = 1.0f
        celula.setPadding(padding.toFloat())
        celula.colspan = 20
        celula.rowspan = rowspan
        celula.horizontalAlignment = Element.ALIGN_CENTER
        celula.verticalAlignment = Element.ALIGN_MIDDLE
        return celula
    }


    private fun cabecalhoSecundario(content: String?, colspan: Int): PdfPCell? {

        val f: Font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f)
        f.color = BaseColor.WHITE
        val celula = PdfPCell(Phrase(content, f))
        celula.borderWidth = 1.0f
        celula.setPadding(padding.toFloat())
        celula.colspan = colspan
        celula.backgroundColor = BaseColor(26, 35, 126)
        celula.horizontalAlignment = Element.ALIGN_CENTER
        celula.verticalAlignment = Element.ALIGN_MIDDLE

        return celula
    }

    private fun criarLinhas(content: String?, colspan: Int): PdfPCell? {

        val f: Font = FontFactory.getFont(FontFactory.HELVETICA, 10f)
        f.color = BaseColor.BLACK
        val celula = PdfPCell(Phrase(content, f))
        celula.borderWidth = 1.0f
        celula.setPadding(padding.toFloat())
        celula.colspan = colspan
        celula.horizontalAlignment = Element.ALIGN_CENTER
        celula.verticalAlignment = Element.ALIGN_MIDDLE
        return celula
    }



    private fun gerarLinhas(tabela: PdfPTable) {


        val listaPessoas = pegarListaPessoas()

        for (pessoa in listaPessoas) {

            tabela.addCell(criarLinhas(pessoa.nome, 4))
            tabela.addCell(criarLinhas(pessoa.endereco, 3))
            tabela.addCell(criarLinhas(pessoa.numero.toString(),3))
            tabela.addCell(criarLinhas(pessoa.bairro, 3))
            tabela.addCell(criarLinhas(pessoa.email, 3))
            tabela.addCell(criarLinhas(pessoa.telefone, 4))


        }


    }


    private fun configurarRecyclerView() {

        agenda_vazia = viewFragment?.findViewById(R.id.tela_vazia)
        recyclerView = viewFragment?.findViewById(R.id.recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(activity)

        val bancoVazio = BancoDAO(requireContext())
        val tela = bancoVazio.conferirListaVazia()

        if (tela) {

            recyclerView?.visibility = View.INVISIBLE
            agenda_vazia?.visibility = View.VISIBLE

        } else {

            recyclerView?.visibility = View.VISIBLE
            agenda_vazia?.visibility = View.GONE
            val dao = BancoDAO(requireContext())
            adapter = AgendaFragmentAdapter(requireContext(), dao.listarPessoas())
            recyclerView?.adapter = adapter


        }


    }


    companion object {

        var adapter: AgendaFragmentAdapter? = null
        var agenda_vazia: RelativeLayout? = null

    }


}