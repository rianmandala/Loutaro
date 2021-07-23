package com.example.loutaro.ui.boardKanban.detailCard

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.adapter.ListActivityChatGroupInCard
import com.example.loutaro.adapter.ListAttachments
import com.example.loutaro.data.entity.*
import com.example.loutaro.databinding.ActivityDetailCardBinding
import com.example.loutaro.databinding.AttachALinkBinding
import com.example.loutaro.databinding.EditCommentBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.ExperimentalTime

class DetailCardActivity : BaseActivity() {
    private var detailBoard= Boards()
    private var detailProject= Project()
    private var listMemberBoards= mutableListOf<Freelancer>()
    private lateinit var listAttachments:ListAttachments
    private lateinit var listActivityChatGroupInCard: ListActivityChatGroupInCard
    var idBoards=""
    var idProject=""
    var positionColumn=0
    var positionRow=0
    var freelancerMember: Freelancer? = null
    var myProfileBusinessMan: BusinessMan?=null
    private val detailCardViewModel: DetailCardViewModel by viewModels {
        ViewModelFactory.getInstance()
    }

    private lateinit var binding: ActivityDetailCardBinding

    companion object{
        const val EXTRA_ID_BOARD= "EXTRA ID BOARD"
        const val EXTRA_ID_PROJECT= "EXTRA ID PROJECT"
        const val EXTRA_POSITION_COLUMN= "EXTRA POSITION COLUMN"
        const val EXTRA_POSITION_ROW= "EXTRA POSITION ROW"
        const val EXTRA_RESULT_CODE=1234
        const val EXTRA_CARD_ROW="EXTRA_CARD_ROW"
        const val EXTRA_CARD_NAME="EXTRA_CARD_NAME"
        const val EXTRA_STATUS_DELETE="EXTRA_STATUS_DELETE"
    }

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listAttachments =  ListAttachments(getCurrentUser()?.uid.toString())
        listActivityChatGroupInCard = ListActivityChatGroupInCard(getCurrentUser()?.uid.toString())
        setToolbarTitle(getString(R.string.card_details))
        activatedToolbarBackButton()
        initTinyDB(this)
        supportActionBar?.elevation = 0F

         idBoards = intent.getStringExtra(EXTRA_ID_BOARD).toString()
         idProject = intent.getStringExtra(EXTRA_ID_PROJECT).toString()
         positionColumn= intent.getIntExtra(EXTRA_POSITION_COLUMN, 0)
         positionRow= intent.getIntExtra(EXTRA_POSITION_ROW, 0)

        setViewToGone(binding.edtChatMessage)

        CoroutineScope(Dispatchers.IO).launch {
            val response = detailCardViewModel.getDetailProject(idProject).await()
            val convertToProject = response.toObject(Project::class.java)
            if(convertToProject!=null){
                detailProject = convertToProject
            }

            if(isUserBusinessMan()){
                val responseMyProfile = detailCardViewModel.getdetailBusinessMan(getCurrentUser()?.uid.toString()).await()
                val converToBusinessMan = responseMyProfile.toObject(BusinessMan::class.java)

                if(converToBusinessMan!=null){
                    myProfileBusinessMan= converToBusinessMan
                }
            }

            withContext(Dispatchers.Main) {
                detailCardViewModel.responseGetDetailBoards.observe(this@DetailCardActivity){ boards->
                    detailBoard = boards
                    binding.run {


                        val attachLink = detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.attachLink
                        if(attachLink != null){
                            listAttachments.submitList(detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.attachLink)
                            listAttachments.notifyDataSetChanged()
                            if(attachLink.isNotEmpty()){
                                setViewToVisible(parentLayoutTagAttactALink, dividerAttactTagALink, rvAttachments, dividerAttachments)
                            }else{
                                setViewToGone(parentLayoutTagAttactALink, dividerAttactTagALink, rvAttachments, dividerAttachments)
                            }
                        }else{
                            setViewToGone(parentLayoutTagAttactALink, dividerAttactTagALink, rvAttachments, dividerAttachments)
                        }

                        if(detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.activity!=null){
                            setViewToVisible(rvActivityChatGroup)
                            listActivityChatGroupInCard.submitList(detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.activity)
                            listActivityChatGroupInCard.notifyDataSetChanged()
                        }else{
                            setViewToGone(rvActivityChatGroup)
                        }


                        tvDetailCardName.text = detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.name.toString()
                        tvDetailCardDescription.text = detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.description?: getString(R.string.description)
                        val dueDate = detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.dueDate
                        val formatter = SimpleDateFormat("EEEE, d MMM yyyy 'at' hh:mm a")
                        tvDueDate.text = if(dueDate!=null) {
                            val deadlineDay = dueDate.toDate().time - Calendar.getInstance().timeInMillis
                            val seconds = deadlineDay / 1000
                            val minutes = seconds / 60
                            val hours = minutes / 60
                            val days = hours / 24
                            Log.d("deadlin", "ini dia $days")
                            if(days.toInt()>1){
                                tvDueDate.setTextColor(ContextCompat.getColor(this@DetailCardActivity, R.color.text_color))
                                imgBtnDueDate.setColorFilter(ContextCompat.getColor(this@DetailCardActivity, R.color.primary))
                            }
                            else if(days.toInt() == 1){
                                tvDueDate.setTextColor(ContextCompat.getColor(this@DetailCardActivity, R.color.secondary))
                                imgBtnDueDate.setColorFilter(ContextCompat.getColor(this@DetailCardActivity, R.color.secondary))
                            }else if(days.toInt()==0){
                                tvDueDate.setTextColor(ContextCompat.getColor(this@DetailCardActivity, R.color.error))
                                imgBtnDueDate.setColorFilter(ContextCompat.getColor(this@DetailCardActivity, R.color.error))
                            }
                            formatter.format(dueDate.toDate().time)
                        }
                        else getString(R.string.due_date)

                        val member = detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.member
                        if(member != null){
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val responseMemberFreelancer = detailCardViewModel.getDetailFreelancer(member).await()
                                    freelancerMember = responseMemberFreelancer.toObject(Freelancer::class.java)
                                    withContext(Dispatchers.Main){
                                        val drawable = TextDrawable.builder()
                                                .buildRect("${freelancerMember?.name?.get(0)}", ContextCompat.getColor(this@DetailCardActivity, R.color.secondary))
                                        if(freelancerMember?.photo!=null){
                                            Glide.with(this@DetailCardActivity)
                                                    .load(freelancerMember!!.photo)
                                                    .into(imgAvatarMember)
                                        }else{
                                            imgAvatarMember.setImageDrawable(drawable)
                                        }
                                        setViewToVisible(cvImgAvatarMember)
                                        setViewToVisible(edtChatMessage)

                                    }

                                }catch (e: Exception){
                                    Log.e("error", "Error when try to get member")
                                }
                            }
                        }else{
                            setViewToGone(cvImgAvatarMember)
                        }
                    }
                }

                detailCardViewModel.getDetailBoards(idBoards)

                binding.run {

                    rvAttachments.layoutManager = LinearLayoutManager(this@DetailCardActivity)
                    rvAttachments.adapter = listAttachments

                    rvActivityChatGroup.layoutManager = LinearLayoutManager(this@DetailCardActivity)
                    rvActivityChatGroup.adapter = listActivityChatGroupInCard

                    edtChatMessage.setEndIconOnClickListener {
                        if(edtChatMessage.editText?.text?.trim()?.isNotEmpty() == true){
                            val chat=Activity()
                            if(isUserFreelancer()){
                                chat.name= freelancerMember?.name
                                chat.message= edtChatMessage.editText?.text.toString()
                                chat.idUser = freelancerMember?.idFreelancer
                                chat.photo = freelancerMember?.photo
                            }else{
                                chat.name=myProfileBusinessMan?.name
                                chat.idUser=getCurrentUser()?.uid
                                chat.photo=myProfileBusinessMan?.photo
                                chat.message=edtChatMessage.editText?.text.toString()
                            }
                            if(detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.activity == null){
                                detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.activity= mutableListOf(chat)
                            }else{
                                detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.activity!!.add(chat)
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    detailCardViewModel.updateDataBoardsColumns(idBoards.toString(), detailBoard.columns as List<BoardsColumn>).await()
                                    withContext(Dispatchers.Main){
                                        edtChatMessage.editText?.setText("")
                                        hideKeyboard()
                                    }
                                }catch (e: Exception){
                                    Log.e("error", "Error when try to update data column")
                                    withContext(Dispatchers.Main){
                                        edtChatMessage.editText?.setText("")
                                        hideKeyboard()
                                    }

                                }
                            }

                        }
                    }

                    parentLayoutAttactALink.setOnClickListener {
                        val attachALinkView = AttachALinkBinding.inflate(layoutInflater)

                        val attachLinkDialogBuilder = MaterialAlertDialogBuilder(this@DetailCardActivity)
                        attachLinkDialogBuilder.setTitle(getString(R.string.attach_a_link))
                        attachLinkDialogBuilder.setView(attachALinkView.root)
                        attachLinkDialogBuilder.setPositiveButton(getString(R.string.ok)){ dialogInterface: DialogInterface, i: Int ->

                        }
                        attachLinkDialogBuilder.setNegativeButton(getString(R.string.cancel)){ dialogInterface: DialogInterface, i: Int ->

                        }

                        val dialogBuilder = attachLinkDialogBuilder.create()
                        dialogBuilder.show()

                        dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            val isValidateName = validateRequire(field = attachALinkView.inputNameLink.text.toString().trim(), attachALinkView.txtInputNameLink, getString(R.string.name))
                            val isValidateLink = validateRequire(field = attachALinkView.inputLink.text.toString().trim(), attachALinkView.txtInputLink, getString(R.string.link_url))
                            if(isValidateName && isValidateLink){
                                if(detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.attachLink!=null){
                                    detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.attachLink!!.add(AttachLink(
                                            name = attachALinkView.inputNameLink.text.toString().trim(),
                                            link = attachALinkView.inputLink.text.toString().trim(),
                                            attachedBy = getCurrentUser()?.uid.toString()
                                    ))
                                }else{
                                    detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.attachLink = mutableListOf(AttachLink(
                                            name = attachALinkView.inputNameLink.text.toString(),
                                            link = attachALinkView.inputLink.text.toString(),
                                            attachedBy = getCurrentUser()?.uid.toString()
                                    ))
                                }
                                Log.d("hasil_link","ini ${detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.attachLink}")
                                detailCardViewModel.updateDataBoardsColumns(idBoards,detailBoard.columns as List<BoardsColumn>)
                                dialogBuilder.dismiss()
                            }

                        }
                    }

                    if(getUserTypeLogin() == getString(R.string.value_business_man)){
                        tvDetailCardDescription.setOnClickListener {
                            edtDetailCardDescription.editText?.setText(tvDetailCardDescription.text.toString())
                            showEditCatdDescription()
                        }

                        parentLayoutDueDate.setOnClickListener {

                            if(detailProject.projectDueDate!=null){
                                val currentDateTime = Calendar.getInstance()
                                val startYear = currentDateTime.get(Calendar.YEAR)
                                val startMonth = currentDateTime.get(Calendar.MONTH)
                                val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
                                val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
                                val startMinute = currentDateTime.get(Calendar.MINUTE)

                                val datePickerDialog = DatePickerDialog(this@DetailCardActivity, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                                    val timePicker = TimePickerDialog(this@DetailCardActivity, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                                        val pickedDateTime = Calendar.getInstance()
                                        pickedDateTime.set(year, month, day, hour, minute)
                                        //                            val formatter = SimpleDateFormat("EEEE, d MMM yyyy 'at' hh:mm a")
                                        //                            tvDueDate.text = formatter.format(pickedDateTime.time)

                                        val timeStampForFirestore = Timestamp(pickedDateTime.time)

                                        detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.dueDate = timeStampForFirestore

                                        detailCardViewModel.updateDataBoardsColumns(idBoards, detailBoard.columns as List<BoardsColumn>)

                                        Log.d("timstamp", "ini dia $timeStampForFirestore")

                                    }, startHour, startMinute, false)

                                    timePicker.setOnCancelListener {
                                        val pickedDateTime = Calendar.getInstance()
                                        pickedDateTime.set(year, month, day, startHour, startMinute)
                                        //                            val formatter = SimpleDateFormat("EEEE, d MMM yyyy 'at' hh:mm a")
                                        //                            tvDueDate.text = formatter.format(pickedDateTime.time)

                                        val timeStampForFirestore = Timestamp(pickedDateTime.time)

                                        detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.dueDate = timeStampForFirestore

                                        detailCardViewModel.updateDataBoardsColumns(idBoards, detailBoard.columns as List<BoardsColumn>)

                                        Log.d("timstamp", "ini dia $timeStampForFirestore")
                                    }

                                    timePicker.show()

                                }, startYear, startMonth, startDay)

                                datePickerDialog.datePicker.minDate = currentDateTime.timeInMillis
                                datePickerDialog.datePicker.maxDate = detailProject.projectDueDate!!.toDate().time
                                datePickerDialog.show()
                            }else{
                                showWarningSnackbar(message = getString(R.string.you_have_to_start_this_project_first_to_be_able_set_due_date))
                            }
                        }

                        edtDetailCardDescription.setStartIconOnClickListener {
                            hideEditCatdDescription()
                        }

                        edtDetailCardDescription.setEndIconOnClickListener {
                            if(edtDetailCardDescription.editText?.text?.trim()?.isNotEmpty() == true){
                                detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.description= edtDetailCardDescription.editText!!.text.toString()
                                tvDetailCardDescription.text = edtDetailCardDescription.editText!!.text.toString()
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        detailCardViewModel.updateDataBoardsColumns(idBoards, detailBoard.columns as List<BoardsColumn>).await()
                                        hideKeyboard()
                                    }catch (e: Exception){
                                        Log.e("error", "Error when try to update data column")
                                        hideKeyboard()
                                    }
                                }
                            }
                            hideEditCatdDescription()
                        }

//                tvDetailCardName.setOnClickListener {
//                    edtDetailCardName.editText?.setText(tvDetailCardName.text.toString())
//                    showEditCardName()
//                }


                        edtDetailCardName.editText?.setOnEditorActionListener { v, actionId, event ->
                            if(actionId== EditorInfo.IME_ACTION_DONE){
                                if(edtDetailCardName.editText?.text?.trim()?.isNotEmpty() == true){
                                    detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.name= edtDetailCardName.editText!!.text.toString()
                                    tvDetailCardName.text = edtDetailCardName.editText!!.text.toString()
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            detailCardViewModel.updateDataBoardsColumns(idBoards.toString(), detailBoard.columns as List<BoardsColumn>).await()
                                            hideKeyboard()
                                        }catch (e: Exception){
                                            Log.e("error", "Error when try to update data column")
                                            hideKeyboard()
                                        }
                                    }
                                }
                                hideEditCardName()
                                return@setOnEditorActionListener true
                            }
                            return@setOnEditorActionListener false
                        }

                        edtDetailCardName.setStartIconOnClickListener {
                            hideEditCardName()
                        }

                        edtDetailCardName.setEndIconOnClickListener {
                            if(edtDetailCardName.editText?.text?.trim()?.isNotEmpty() == true){
                                detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.name= edtDetailCardName.editText!!.text.toString()
                                tvDetailCardName.text = edtDetailCardName.editText!!.text.toString()
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        detailCardViewModel.updateDataBoardsColumns(idBoards.toString(), detailBoard.columns as List<BoardsColumn>).await()
                                    }catch (e: Exception){
                                        Log.e("error", "Error when try to update data column")
                                    }
                                }
                            }
                            hideEditCardName()
                        }

                    }else if(isUserFreelancer(this@DetailCardActivity)){
                        parentLayoutDueDate.isClickable= false
                    }

                }
            }
        }

        listAttachments.onEditClickCallback={ attachLink->
            val attachALinkView = AttachALinkBinding.inflate(layoutInflater)
            attachALinkView.inputNameLink.setText(attachLink.name)
            attachALinkView.inputLink.setText(attachLink.link)

            val attachLinkDialogBuilder = MaterialAlertDialogBuilder(this@DetailCardActivity)
            attachLinkDialogBuilder.setTitle(getString(R.string.attach_a_link))
            attachLinkDialogBuilder.setView(attachALinkView.root)
            attachLinkDialogBuilder.setPositiveButton(getString(R.string.ok)){ dialogInterface: DialogInterface, i: Int ->

            }
            attachLinkDialogBuilder.setNegativeButton(getString(R.string.cancel)){ dialogInterface: DialogInterface, i: Int ->

            }

            val dialogBuilder = attachLinkDialogBuilder.create()
            dialogBuilder.show()

            dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val isValidateName = validateRequire(field = attachALinkView.inputNameLink.text.toString().trim(), attachALinkView.txtInputNameLink, getString(R.string.name))
                val isValidateLink = validateRequire(field = attachALinkView.inputLink.text.toString().trim(), attachALinkView.txtInputLink, getString(R.string.link_url))
                if(isValidateName && isValidateLink){
                    val indexFound = detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.attachLink?.indexOf(attachLink)
                    if(indexFound!=null && indexFound!=-1){
                        detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.attachLink?.set(indexFound, AttachLink(
                                name = attachALinkView.inputNameLink.text.toString(),
                                link = attachALinkView.inputLink.text.toString(),
                                attachedBy = getCurrentUser()?.uid.toString()
                        ))
                    }

                    Log.d("hasil_link","ini ${detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.attachLink}")
                    detailCardViewModel.updateDataBoardsColumns(idBoards,detailBoard.columns as List<BoardsColumn>)
                    dialogBuilder.dismiss()
                }

            }
        }

        listActivityChatGroupInCard.onEditClickCallback={ activity->
            val editCommentView = EditCommentBinding.inflate(layoutInflater)
            editCommentView.inputNameEditMessage.setText(activity.message)

            val attachLinkDialogBuilder = MaterialAlertDialogBuilder(this@DetailCardActivity)
            attachLinkDialogBuilder.setTitle(getString(R.string.edit_comment))
            attachLinkDialogBuilder.setView(editCommentView.root)
            attachLinkDialogBuilder.setPositiveButton(getString(R.string.ok)){ dialogInterface: DialogInterface, i: Int ->

            }
            attachLinkDialogBuilder.setNegativeButton(getString(R.string.cancel)){ dialogInterface: DialogInterface, i: Int ->

            }

            val dialogBuilder = attachLinkDialogBuilder.create()
            dialogBuilder.show()

            dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val isValidateName = validateRequire(field = editCommentView.inputNameEditMessage.text.toString().trim(), editCommentView.txtInputEditMessage, getString(R.string.comment))
                if(isValidateName){
                    val indexFound = detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.activity?.indexOf(activity)
                    if(indexFound!=null && indexFound!=-1){
                        detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.activity?.set(indexFound, Activity(
                                name = activity.name,
                                photo = activity.photo,
                                idUser = activity.idUser,
                                message = editCommentView.inputNameEditMessage.text.toString(),
                        ))
                    }

                    Log.d("hasil_link","ini ${detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.activity}")
                    detailCardViewModel.updateDataBoardsColumns(idBoards,detailBoard.columns as List<BoardsColumn>)
                    dialogBuilder.dismiss()
                }

            }
        }

        listAttachments.onDeleteClickCallback={
            MaterialAlertDialogBuilder(this@DetailCardActivity)
                    .setMessage(getString(R.string.are_you_sure_want_to_delete_this_attachment))
                    .setPositiveButton(getString(R.string.ok)){ dialogInterface: DialogInterface, i: Int ->
                        detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.attachLink?.remove(it)
                        detailCardViewModel.updateDataBoardsColumns(idBoards,detailBoard.columns as List<BoardsColumn>)
                    }
                    .setNegativeButton(getString(R.string.cancel)){ dialogInterface: DialogInterface, i: Int ->

                    }
                    .show()
        }

        listActivityChatGroupInCard.onDeleteClickCallback={
            MaterialAlertDialogBuilder(this@DetailCardActivity)
                    .setMessage(getString(R.string.are_you_sure_want_to_delete_this_comment))
                    .setPositiveButton(getString(R.string.ok)){ dialogInterface: DialogInterface, i: Int ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try{
                                detailBoard.columns?.get(positionColumn)?.cards?.get(positionRow)?.activity?.remove(it)
                                Log.d("data_board","ini dia ${detailBoard.columns}")
                                detailCardViewModel.updateDataBoardsColumns(idBoards,detailBoard.columns as List<BoardsColumn>).await()
                            }catch(e: Exception){
                                showWarningSnackbar(message = "error ${e.message}")
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel)){ dialogInterface: DialogInterface, i: Int ->

                    }
                    .show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        if(getUserTypeLogin() == getString(R.string.value_business_man)){
//            menuInflater.inflate(R.menu.card_detail_menu_options, menu)
//        }
        return true
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.delete_card_menu -> {
                MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.are_you_sure_want_to_delete_this_card))
                        .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    detailBoard.columns?.get(positionColumn)?.cards?.removeAt(positionRow)
                                    detailCardViewModel.updateDataBoardsColumns(idBoards, detailBoard.columns as List<BoardsColumn>).await()
                                    withContext(Dispatchers.Main) {
                                        val resultIntent = Intent()
                                        resultIntent.putExtra(EXTRA_POSITION_COLUMN, positionColumn)
                                        resultIntent.putExtra(EXTRA_POSITION_ROW, positionRow)
                                        resultIntent.putExtra(EXTRA_CARD_NAME, binding.tvDetailCardName.text)
                                        resultIntent.putExtra(EXTRA_STATUS_DELETE, true)
                                        setResult(EXTRA_RESULT_CODE, resultIntent)
                                        finish()
                                    }
                                } catch (e: Exception) {
                                    Log.e("error", "Error when try to delete card")
                                }
                            }

                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->

                        }
                        .show()
                true
            }
//            R.id.withdraw_payment_menu->{
//                navigateForward(WithdrawFreelancerActivity::class.java,this)
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_POSITION_COLUMN, positionColumn)
        resultIntent.putExtra(EXTRA_POSITION_ROW, positionRow)
        var nameCardRetun = binding.tvDetailCardName.text
        if(freelancerMember!=null){
            if(freelancerMember?.photo != null){
                nameCardRetun = "${binding.tvDetailCardName.text}#%photo%#${freelancerMember!!.photo}"
            }else{
                nameCardRetun = "${binding.tvDetailCardName.text}#%name%#${freelancerMember?.name}"
            }
        }
        resultIntent.putExtra(EXTRA_CARD_NAME, nameCardRetun)
        resultIntent.putExtra(EXTRA_STATUS_DELETE, false)
        setResult(EXTRA_RESULT_CODE, resultIntent)
        super.onBackPressed()
    }

    fun showEditCatdDescription(){
        binding.run {
            setViewToVisible(edtDetailCardDescription)
            setViewToGone(tvDetailCardDescription)
        }
    }

    fun hideEditCatdDescription(){
        binding.run {
            setViewToGone(edtDetailCardDescription)
            setViewToVisible(tvDetailCardDescription)
        }
    }

    fun showEditCardName(){
        binding.run {
            setViewToVisible(edtDetailCardName)
            setViewToGone(tvDetailCardName)
        }
    }

    fun hideEditCardName(){
        binding.run {
            setViewToGone(edtDetailCardName)
            setViewToVisible(tvDetailCardName)
        }
    }
}