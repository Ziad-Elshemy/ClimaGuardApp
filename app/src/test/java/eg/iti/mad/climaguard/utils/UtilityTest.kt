package eg.iti.mad.climaguard.utils

import junit.framework.TestCase.assertEquals
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class UtilityTest{

    @Test
    fun convertToArabicNumbers_send1_receive1InArabic(){
        //Given
        val task = "1"

        //When
        val result = Utility.convertToArabicNumbers(task)

        //Then

        //using Hamcrest
        assertThat(result, `is`("١") )
    }

    @Test
    fun convertToArabicNumbers_send23_receive23InArabic(){
        //Given
        val task = "2.3"

        //When
        val result = Utility.convertToArabicNumbers(task)

        //Then

        //using Hamcrest
        assertThat(result, `is`("٢.٣") )
    }


}