package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.data.LocalAuthManager
import com.example.myapplication.data.PreferencesManager
import com.example.myapplication.data.DataSource
import com.example.myapplication.data.FakeDataSource
import com.example.myapplication.data.SyncManager
import com.example.myapplication.data.local.LocalAchievementRepository
import com.example.myapplication.data.local.LocalActivityRepository
import com.example.myapplication.data.local.LocalAttendanceRepository
import com.example.myapplication.data.local.LocalConfigRepository
import com.example.myapplication.data.local.LocalExerciseFeedbackRepository
import com.example.myapplication.data.local.LocalInjuryRepository
import com.example.myapplication.data.local.LocalModelRepository
import com.example.myapplication.data.local.LocalNormRepository
import com.example.myapplication.data.local.LocalPlanFeedbackRepository
import com.example.myapplication.data.local.LocalPlanRepository
import com.example.myapplication.data.local.LocalQuizRepository
import com.example.myapplication.data.local.LocalRatingRepository
import com.example.myapplication.data.local.LocalStudentRepository
import com.example.myapplication.data.local.LocalUserProfileRepository
import com.example.myapplication.data.settingsDataStore
import com.example.myapplication.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val preferencesModule = module {
    single<PreferencesManager> { PreferencesManager(get<Context>().settingsDataStore) }
}

val authModule = module {
    single<LocalAuthManager> { LocalAuthManager(get<Context>()) }
    viewModel { AuthViewModel(get<UserProfileRepository>(), get<PreferencesManager>()) }
}

val localModule = module {
    single<UserProfileRepository> { LocalUserProfileRepository(get<Context>(), get<LocalAuthManager>()) }
    single<PlanRepository> { LocalPlanRepository(get<Context>(), get<LocalAuthManager>()) }
    single<QuizScoreRepository> { LocalQuizRepository(get<Context>(), get<LocalAuthManager>()) }
    single<InjuryRepository> { LocalInjuryRepository(get<Context>(), get<LocalAuthManager>()) }
    single<StudentRepository> { LocalStudentRepository(get<Context>()) }
    single<ModelRepository> { LocalModelRepository() }
    single<AttendanceRepository> { LocalAttendanceRepository(get<Context>(), get<LocalAuthManager>()) }
    single<NormRepository> { LocalNormRepository(get<Context>(), get<LocalAuthManager>()) }
    single<RatingRepository> { LocalRatingRepository(get<Context>(), get<LocalAuthManager>()) }
    single<AchievementRepository> { LocalAchievementRepository(get<Context>(), get<LocalAuthManager>()) }
    single<ExerciseFeedbackRepository> { LocalExerciseFeedbackRepository(get<Context>(), get<LocalAuthManager>()) }
    single<PlanFeedbackRepository> { LocalPlanFeedbackRepository(get<Context>(), get<LocalAuthManager>()) }
    single<ActivityRepository> { LocalActivityRepository(get<Context>(), get<LocalAuthManager>()) }
    single<ConfigRepository> { LocalConfigRepository(get<Context>(), get<LocalAuthManager>()) }
    single<DataSource> { FakeDataSource(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<SyncManager> { SyncManager(get(), get(), get()) }
}

val viewModelModule = module {
    viewModel { UserProfileViewModel(get()) }
    viewModel { PlansViewModel(get(), get()) }
    viewModel { StudentsViewModel(get()) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { ModelViewModel(get()) }
    viewModel { QuizScoresViewModel(get()) }
    viewModel { MuscleFatigueViewModel(get<InjuryRepository>()) }
    viewModel { AiPlanGeneratorViewModel() }
    viewModel { AttendanceViewModel(get(), get()) }
}
