package com.example.loutaro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.loutaro.data.source.LoutaroRepository
import com.example.loutaro.di.Injection
import com.example.loutaro.ui.boardKanban.BoardKanbanViewModel
import com.example.loutaro.ui.boardKanban.detailCard.DetailCardViewModel
import com.example.loutaro.ui.createProfile.CreateProfileViewModel
import com.example.loutaro.ui.createProject.CreateProjectViewModel
import com.example.loutaro.ui.freelancerDetail.FreelancerDetailViewModel
import com.example.loutaro.ui.freelancerRecommendation.FreelancerRecommendationViewModel
import com.example.loutaro.ui.home.HomeViewModel
import com.example.loutaro.ui.login.LoginViewModel
import com.example.loutaro.ui.members.addMember.AddMemberFragment
import com.example.loutaro.ui.members.addMember.AddMemberViewModel
import com.example.loutaro.ui.members.listMember.ListMemberViewModel
import com.example.loutaro.ui.notification.NotificationViewModel
import com.example.loutaro.ui.onboarding.OnBoardingViewModel
import com.example.loutaro.ui.paymentProject.PaymentProjectViewModel
import com.example.loutaro.ui.profile.ProfileViewModel
import com.example.loutaro.ui.profile.updateProfile.UpdateProfileViewModel
import com.example.loutaro.ui.project.activeProject.ActiveProjectViewModel
import com.example.loutaro.ui.project.activeProject.applyers.ApplyersViewModel
import com.example.loutaro.ui.project.finishProject.FinishProjectViewModel
import com.example.loutaro.ui.project.pendingProject.PendingProjectViewModel
import com.example.loutaro.ui.projectDetail.ProjectDetailViewModel
import com.example.loutaro.ui.register.RegisterViewModel
import com.example.loutaro.ui.saved.SavedProjectViewModel
import com.example.loutaro.ui.search.SearchProjectViewModel
import com.example.loutaro.ui.withdrawFreelancer.WithdrawFreelancerViewModel

class ViewModelFactory private constructor(private val loutaroRepository: LoutaroRepository): ViewModelProvider.NewInstanceFactory(){

    companion object{
        @Volatile
        private var INSTACE: ViewModelFactory?=null

        fun getInstance():ViewModelFactory{
            return INSTACE?: synchronized(this){
                ViewModelFactory(Injection.provideLoutaroRepository()).apply {
                    INSTACE=this
                }
            }
        }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(RegisterViewModel::class.java)->{
                RegisterViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(CreateProfileViewModel::class.java)->{
                CreateProfileViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(OnBoardingViewModel::class.java)->{
                OnBoardingViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java)->{
                LoginViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java)->{
                HomeViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(ProjectDetailViewModel::class.java) ->{
                ProjectDetailViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(ActiveProjectViewModel::class.java) ->{
                ActiveProjectViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(FinishProjectViewModel::class.java)->{
                FinishProjectViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(SearchProjectViewModel::class.java)->{
                SearchProjectViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(UpdateProfileViewModel::class.java) ->{
                UpdateProfileViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(SavedProjectViewModel::class.java)->{
                SavedProjectViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(FreelancerDetailViewModel::class.java)->{
                FreelancerDetailViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(CreateProjectViewModel::class.java)->{
                CreateProjectViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(BoardKanbanViewModel::class.java)->{
                BoardKanbanViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(ListMemberViewModel::class.java)->{
                ListMemberViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(AddMemberViewModel::class.java)->{
                AddMemberViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(NotificationViewModel::class.java)->{
                NotificationViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(FreelancerRecommendationViewModel::class.java)->{
                FreelancerRecommendationViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(DetailCardViewModel::class.java)->{
                DetailCardViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(PendingProjectViewModel::class.java)->{
                PendingProjectViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(PaymentProjectViewModel::class.java)->{
                PaymentProjectViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(ApplyersViewModel::class.java)->{
                ApplyersViewModel(loutaroRepository) as T
            }
            modelClass.isAssignableFrom(WithdrawFreelancerViewModel::class.java)->{
                WithdrawFreelancerViewModel(loutaroRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel Class ${modelClass.name}")
        }
    }

}