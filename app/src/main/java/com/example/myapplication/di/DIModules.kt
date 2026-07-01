package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.data.LocalAuthManager
import com.example.myapplication.data.PreferencesManager
import com.example.myapplication.data.settingsDataStore
import com.example.myapplication.*
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val firebaseModule = module {
    single<PreferencesManager> { PreferencesManager(get<Context>().settingsDataStore) }
    single { FirebaseFirestore.getInstance() }
}

val repositoryModule = module {
    single<StudentRepository> { StudentRepositoryImpl() }
    single<LocalAuthManager> { LocalAuthManager(get<Context>()) }
    single<QuizScoreRepository> { QuizScoreRepositoryImpl(get<LocalAuthManager>(), get()) }
    single<PlanRepository> { PlanRepositoryImpl(get<LocalAuthManager>(), get()) }
    single<InjuryRepository> { InjuryRepositoryImpl(get<LocalAuthManager>(), get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(get<LocalAuthManager>(), get()) }
}

val viewModelModule = module {
    viewModel { AuthViewModel(get<LocalAuthManager>(), get<PreferencesManager>()) }
    viewModel { UserProfileViewModel(get()) }
    viewModel { PlansViewModel(get()) }
    viewModel { StudentsViewModel(get()) }
    viewModel { QuizScoresViewModel(get()) }
    viewModel { MuscleFatigueViewModel(get<InjuryRepository>()) }
    viewModel { AiPlanGeneratorViewModel() }
}
