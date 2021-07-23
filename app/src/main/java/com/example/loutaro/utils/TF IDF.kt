package com.example.loutaro.utils

import android.util.Log
import com.example.loutaro.data.entity.FreelancerRecommendation
import com.example.loutaro.data.entity.ProjectRecommendation
import java.lang.Math.log10
import java.math.RoundingMode
import java.text.DecimalFormat

class TFIDF {
    fun getProjectRecommendation(skillFreelancer: MutableList<String>, listProject: MutableList<ProjectRecommendation>): MutableList<ProjectRecommendation> {
        var Q = skillFreelancer


        var data = listProject.map {
            it.skills!!.toMutableList()
        }.toMutableList()
        var df = mutableListOf<Int>()
        var ddf= mutableListOf<Float>()
        var idf = mutableListOf<Float>()
        var wTotal = listProject
        var w = Array(data.size){ MutableList(data.size) {0F} }
        var tf= Array(data.size){ MutableList<String?>(data.size) {null} }
        var tf2 = Array(data.size){ MutableList(data.size) {-1} }

        Q.sort();

        val deformat = DecimalFormat("#.#####")
        deformat.roundingMode = RoundingMode.CEILING

        //mencari TF
        for (i in 0.until(data.size)) {
            data[i].sort();
            var cnt = 0;
            var a = 0;
            var b = 0;

            var j=0
            while (j < data[i].size) {
                if (data[i].getOrNull(j) == Q.getOrNull(a)) {
                    if(j < tf[i].size){
                        tf[i][j] = data[i][j];
                        b = a;
                        a++
                    }else{
                        tf[i].add(data[i][j])
                        b = a;
                        a++
                    }
                }
                else if (j >= data[i].size - 1 && Q.getOrNull(a) != null) {
                    j = b-1;
                    a++
                }else{
                    j++
                    continue;
                }
                j++
            }
        }

        //membuat Tf table
        for (i in 0.until(tf.size)){
            var a = 0
            tf[i].sortWith(nullsLast())
            var j=0
            while(j < tf[i].size){
                if(Q.getOrNull(a)!=null && Q[a] != tf[i][j]){
                    tf2[i][a] = 0;
                    j -= 1
                }else if ( tf[i].getOrNull(j) == null && Q.getOrNull(a) == null){
                    j++
                    continue
                }
                else{
                    if(tf2[i].getOrNull(a) != null){
                        tf2[i][a] = 1
                    }else{
                        tf2[i].add(1)
                    }
                }
                a++
                j++
            }
        }

        //mencari D DF IDF dan IDF+1
        for (i in 0.until(tf2.size)){
            var cnt = 0;
            var temp:Double
            var idf1:Float=0F
            for (j in 0.until(tf2.size)){
                if(tf2[j][i] == 1){
                    cnt++;
                }
            }

            try{
                if(cnt>0){
                    temp = (data.size)/cnt.toDouble()
                    idf1 = 1 + (deformat.format(log10(temp))).toFloat()
                    df.add(cnt)
                    ddf.add(temp.toFloat())
                    idf.add(idf1);
                }else{
                    temp = 0.0
//                    idf1 = 1F
                    df.add(cnt)
                    ddf.add(temp.toFloat())
                    idf.add(idf1)
                }
            }catch (e: Exception){
                Log.e("error_string","ini errornya ${e.message}")
            }



        }

        //membuat table W
        for (i in 0.until(tf2.size)){
            for(j in 0.until(tf2[i].size)){
                if (tf2[j][i] == 1){
                    w[j][i] = idf[i]
                }else{
                    w[j][i] = 0F
                }
            }
        }

        //hasil akhir
        var totalIdf=0F
        for (i in 0.until(w.size)){
            var total=0F
            totalIdf += idf[i]
            for(j in 0.until(Q.size)){
                total += w[i][j]
            }
            wTotal[i].recommendationScore = deformat.format(total).toFloat()
        }

        // Persentasi
        for(i in 0.until(w.size)){
            val deformatPercent = DecimalFormat("#.#")
            deformatPercent.roundingMode = RoundingMode.CEILING
            if(wTotal[i].recommendationScore!=null){
                var recommedationInPercent = wTotal[i].recommendationScore!! / totalIdf * 100
                wTotal[i].recommendationInPercent= deformatPercent.format(recommedationInPercent).toFloat()
            }
        }

        wTotal= wTotal.filter {
            it.recommendationScore!! >0.0
        }.toMutableList()
        wTotal.sortByDescending {
            it.recommendationScore
        }


        return wTotal
    }

    fun getFreelancerRecommendation(skillProjectNeeded: MutableList<String>, listFreelancer: MutableList<FreelancerRecommendation>): MutableList<FreelancerRecommendation> {
        var Q = skillProjectNeeded


        var data = listFreelancer.map {
            it.skills!!.toMutableList()
        }.toMutableList()
        var df = mutableListOf<Int>()
        var ddf= mutableListOf<Float>()
        var idf = mutableListOf<Float>()
        var wTotal = listFreelancer
        var w = Array(data.size){ MutableList(data.size) {0F} }
        var tf= Array(data.size){ MutableList<String?>(data.size) {null} }
        var tf2 = Array(data.size){ MutableList(data.size) {-1} }

        Q.sort();

        val deformat = DecimalFormat("#.#####")
        deformat.roundingMode = RoundingMode.CEILING

        //mencari TF
        for (i in 0.until(data.size)) {
            data[i].sort();
            var cnt = 0;
            var a = 0;
            var b = 0;

            var j=0
            while (j < data[i].size) {
                if (data[i].getOrNull(j) == Q.getOrNull(a)) {
                    if(j < tf[i].size){
                        tf[i][j] = data[i][j];
                        b = a;
                        a++
                    }else{
                        tf[i].add(data[i][j])
                        b = a;
                        a++
                    }
                }
                else if (j >= data[i].size - 1 && Q.getOrNull(a) != null) {
                    j = b-1;
                    a++
                }else{
                    j++
                    continue;
                }
                j++
            }
        }

        //membuat Tf table
        for (i in 0.until(tf.size)){
            var a = 0
            tf[i].sortWith(nullsLast())
            var j=0
            while(j < tf[i].size){
                if(Q.getOrNull(a)!=null && Q[a] != tf[i][j]){
                    tf2[i][a] = 0;
                    j -= 1
                }else if ( tf[i].getOrNull(j) == null && Q.getOrNull(a) == null){
                    j++
                    continue
                }
                else{
                    if(tf2[i].getOrNull(a) != null){
                        tf2[i][a] = 1
                    }else{
                        tf2[i].add(1)
                    }
                }
                a++
                j++
            }
        }

        //mencari D DF IDF dan IDF+1
        for (i in 0.until(tf2.size)){
            var cnt = 0;
            var temp:Double
            var idf1:Float=0F
            for (j in 0.until(tf2.size)){
                if(tf2[j][i] == 1){
                    cnt++;
                }
            }

            if(cnt>0){
                temp = (data.size)/cnt.toDouble()
                idf1 = 1 + (deformat.format(log10(temp))).toFloat()
                df.add(cnt)
                ddf.add(temp.toFloat())
                idf.add(idf1);
            }else{
                temp = 0.0
//                idf1 = 1F
                df.add(cnt)
                ddf.add(temp.toFloat())
                idf.add(idf1)
            }


        }

        //membuat table W
        for (i in 0.until(tf2.size)){
            for(j in 0.until(tf2[i].size)){
                if (tf2[j][i] == 1){
                    w[j][i] = idf[i]
                }else{
                    w[j][i] = 0F
                }
            }
        }

        //hasil akhir
        var totalIdf=0F
        for (i in 0.until(w.size)){
            var total=0F
            totalIdf += idf[i]
            for(j in 0.until(Q.size)){
                total += w[i][j]
            }
            wTotal[i].recommendationScore = deformat.format(total).toFloat()
        }

        // Persentasi
        for(i in 0.until(w.size)){
            val deformatPercent = DecimalFormat("#.#")
            deformatPercent.roundingMode = RoundingMode.CEILING
            if(wTotal[i].recommendationScore!=null){
                val recommedationInPercent = (wTotal[i].recommendationScore!! / totalIdf) * 100
                Log.d("wTotal","total oi $recommedationInPercent")
                wTotal[i].recommendationInPercent= deformatPercent.format(recommedationInPercent).toFloat()
            }
        }

        wTotal= wTotal.filter {
            it.recommendationInPercent!! >0.0
        }.toMutableList()
        wTotal.sortByDescending {
            it.recommendationInPercent
        }

        Log.d("wTotal","total oi $wTotal")

        return wTotal
    }
}