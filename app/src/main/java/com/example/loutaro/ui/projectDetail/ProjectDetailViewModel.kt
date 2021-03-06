package com.example.loutaro.ui.projectDetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task

class ProjectDetailViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var statusGetDetailProject: MutableLiveData<Project?> = MutableLiveData()

    fun getDetailProject(idProject: String){
        loutaroRepository.getDetailProject(idProject).addOnSuccessListener { project->
            val projectConvert = project.toObject(Project::class.java)
            statusGetDetailProject.postValue(projectConvert)
        }
    }

    fun updateSaveProject(idProject: String, isSaved: Boolean){
        loutaroRepository.updateSavedProject(idProject, isSaved)
    }

    fun applyAsFreelancerToProject(idProject: String, dataProject: Project): Task<Void> {
        return loutaroRepository.applyAsFreelancerToProject(idProject, dataProject)
    }

}