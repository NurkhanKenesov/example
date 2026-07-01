package com.example.myapplication

interface StudentRepository {
    suspend fun getStudents(): Result<List<Student>>
}

class StudentRepositoryImpl : StudentRepository {

    override suspend fun getStudents(): Result<List<Student>> {
        val mockStudents = listOf(
            Student(id="1", initials="AS", name="Amir Seitkali",    gender="Male",   age="22", groupName="basic",    score="3.2/4", hasAlert=false),
            Student(id="2", initials="ZA", name="Zarina Akhmetova", gender="Female", age="20", groupName="prepared", score="2.5/4", hasAlert=true),
            Student(id="3", initials="RN", name="Ruslan Nurlanov",  gender="Male",   age="21", groupName="basic",    score="3.8/4", hasAlert=false),
            Student(id="4", initials="DI", name="Dias Issayev",     gender="Male",   age="19", groupName="special",  score="1.8/4", hasAlert=false),
            Student(id="5", initials="AK", name="Aliya Kasymova",   gender="Female", age="20", groupName="basic",    score="3.5/4", hasAlert=false),
            Student(id="6", initials="BM", name="Bekzat Muratov",   gender="Male",   age="22", groupName="prepared", score="2.9/4", hasAlert=true),
            Student(id="7", initials="GT", name="Gulnara Tleubek",  gender="Female", age="21", groupName="basic",    score="4.0/4", hasAlert=false),
            Student(id="8", initials="EA", name="Erik Akhanov",     gender="Male",   age="23", groupName="special",  score="2.1/4", hasAlert=false),
            Student(id="9", initials="MJ", name="Madina Jumabek",   gender="Female", age="20", groupName="basic",    score="3.7/4", hasAlert=false),
            Student(id="10", initials="KS", name="Kanat Seilov",   gender="Male",   age="21", groupName="prepared", score="2.3/4", hasAlert=true)
        )
        return Result.success(mockStudents)
    }
}