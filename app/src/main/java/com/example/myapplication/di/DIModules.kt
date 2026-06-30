package com.example.myapplication.di

import com.example.myapplication.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val firebaseModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
}

val repositoryModule = module {
    single<StudentRepository> { StudentRepositoryImpl(get()) }
    single<QuizScoreRepository> { QuizScoreRepositoryImpl(get(), get()) }
    single<PlanRepository> { PlanRepositoryImpl(get(), get()) }
    single<InjuryRepository> { InjuryRepositoryImpl(get(), get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(get(), get()) }
}

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { UserProfileViewModel(get()) }
    viewModel { PlansViewModel(get(), get()) }
    viewModel { StudentsViewModel(get()) }
    viewModel { QuizScoresViewModel(get()) }
    viewModel { MuscleFatigueViewModel(get<InjuryRepository>()) }
}
