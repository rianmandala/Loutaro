package com.example.loutaro.data.dummy

object Categori {
    var skill= Skill(
            mobileDevelopment = listOf("Kotlin Native","Swift","React Native","Ionic","Flutter","C#"),
            webDevelopment = listOf("HTML","CSS","SCSS/SASS","Javascript","PHP","React","Angular","Vue"),
            desktopDevelopment = listOf("C","C++","C#","Electron","Java"),
            design = listOf("UI/UX","Mockup","Illustration","Logo","Presentation","Print Design","Adobe XD","Adobe Photoshop","Adobe Illustrator"),
            dataScience = listOf("Python ","R","Hadoop Platform","SQL database","Machine Learning and AI","Data Visualization")
    )
}

data class Skill(
        var mobileDevelopment: List<String>,
        var webDevelopment: List<String>,
        var desktopDevelopment: List<String>,
        var design: List<String>,
        var dataScience: List<String>
)